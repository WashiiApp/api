
package br.com.washii.api.service;

import br.com.washii.api.exception.BusinessException;
import br.com.washii.api.model.Agendamento;
import br.com.washii.api.model.StatusAgendamento;
import br.com.washii.api.repository.AgendamentoRepository;
import br.com.washii.api.repository.AgendamentoServicoRepository;
import br.com.washii.api.service.cache.HorarioDisponivelRedisService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doAnswer;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;



@SpringBootTest
@EnabledIfEnvironmentVariable(
        named = "WASHII_TEST_AGENDAMENTO_ID",
        matches = ".+"
)
class AgendamentoStatusConcorrenciaE2EIT {

    @Autowired
    private AgendamentoService agendamentoService;

    /*
     * Espiona o repositório para pausar as duas chamadas logo após
     * carregarem o mesmo agendamento. O método real continua sendo executado.
     */
    @MockitoSpyBean
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private AgendamentoServicoRepository agendamentoServicoRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    /*
     * Os métodos de status invalidam o cache. Ele não é o alvo deste teste,
     * então o mock evita exigir um Redis ativo.
     */
    @MockitoBean
    private HorarioDisponivelRedisService horarioDisponivelCache;

    @PersistenceContext
    private EntityManager entityManager;

    private TransactionTemplate transacao;
    private Fixture fixture;

    private record Fixture(
            UUID agendamentoId,
            UUID clienteId,
            UUID lavaJatoId,
            StatusAgendamento statusOriginal,
            long versaoInicial
    ) {}

    @BeforeEach
    void prepararAgendamentoDeTeste() {
        transacao = new TransactionTemplate(transactionManager);

        UUID id = UUID.fromString(
                System.getenv("WASHII_TEST_AGENDAMENTO_ID")
        );

        Fixture antes = transacao.execute(status -> {
            Agendamento agendamento = agendamentoRepository.findById(id)
                    .orElseThrow(() -> new AssertionError(
                            "Agendamento de teste não encontrado: " + id
                    ));

            UUID clienteId = agendamento.getVeiculo()
                    .getCliente()
                    .getId();

            UUID lavaJatoId = agendamentoServicoRepository
                    .findByAgendamento(agendamento)
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new AssertionError(
                            "O agendamento de teste não possui serviço associado."
                    ))
                    .getServico()
                    .getLavaJato()
                    .getId();

            return new Fixture(
                    id,
                    clienteId,
                    lavaJatoId,
                    agendamento.getStatusAgendamento(),
                    agendamento.getVersao()
            );
        });

        assertNotNull(antes);

        // Prepara o registro para que as duas operações sejam válidas no início.
        transacao.executeWithoutResult(status -> {
            Agendamento agendamento = agendamentoRepository.findById(id)
                    .orElseThrow();

            agendamento.setStatusAgendamento(StatusAgendamento.AGENDADO);
            agendamentoRepository.saveAndFlush(agendamento);
        });

        long versaoInicial = transacao.execute(status ->
                agendamentoRepository.findById(id)
                        .orElseThrow()
                        .getVersao()
        );

        fixture = new Fixture(
                antes.agendamentoId(),
                antes.clienteId(),
                antes.lavaJatoId(),
                antes.statusOriginal(),
                versaoInicial
        );
    }

    @AfterEach
    void restaurarStatusOriginal() {
        if (fixture == null) {
            return;
        }

        transacao.executeWithoutResult(status -> {
            Agendamento agendamento = agendamentoRepository
                    .findById(fixture.agendamentoId())
                    .orElseThrow();

            agendamento.setStatusAgendamento(fixture.statusOriginal());
            agendamentoRepository.saveAndFlush(agendamento);
        });
    }

    @Test
    void cancelarEConcluirAoMesmoTempoApenasUmaOperacaoDeveVencer()
            throws Exception {

        CyclicBarrier ambosLeram = new CyclicBarrier(2);
        AtomicInteger leiturasConcorrentes = new AtomicInteger();

        doAnswer(invocacao -> {
            UUID idLido = invocacao.getArgument(0);

            Agendamento agendamento =
                    entityManager.find(Agendamento.class, idLido);

            if (fixture.agendamentoId().equals(idLido)
                    && leiturasConcorrentes.getAndIncrement() < 2) {
                ambosLeram.await(10, TimeUnit.SECONDS);
            }

            return Optional.ofNullable(agendamento);
        }).when(agendamentoRepository).findById(fixture.agendamentoId());

        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            Future<RuntimeException> cancelamento = executor.submit(() ->
                    capturarExcecao(() ->
                            agendamentoService.cancelarPeloCliente(
                                    fixture.agendamentoId(),
                                    fixture.clienteId()
                            )
                    )
            );

            Future<RuntimeException> conclusao = executor.submit(() ->
                    capturarExcecao(() ->
                            agendamentoService.atualizarStatus(
                                    fixture.agendamentoId(),
                                    fixture.lavaJatoId(),
                                    StatusAgendamento.CONCLUIDO
                            )
                    )
            );

            RuntimeException erroCancelamento =
                    cancelamento.get(20, TimeUnit.SECONDS);
            RuntimeException erroConclusao =
                    conclusao.get(20, TimeUnit.SECONDS);

            boolean cancelamentoVenceu =
                    erroCancelamento == null
                            && erroConclusao instanceof BusinessException;

            boolean conclusaoVenceu =
                    erroConclusao == null
                            && erroCancelamento instanceof BusinessException;

            assertTrue(
                    cancelamentoVenceu || conclusaoVenceu,
                    () -> "Esperava uma operação bem-sucedida e uma BusinessException. "
                            + "Erro do cancelamento: " + erroCancelamento
                            + "; erro da conclusão: " + erroConclusao
            );

            Agendamento resultado = transacao.execute(status ->
                    agendamentoRepository.findById(fixture.agendamentoId())
                            .orElseThrow()
            );

            assertTrue(
                    resultado.getStatusAgendamento() == StatusAgendamento.CANCELADO
                            || resultado.getStatusAgendamento()
                            == StatusAgendamento.CONCLUIDO
            );

            // Apenas a transação vencedora deve ter alterado a versão.
            assertEquals(
                    fixture.versaoInicial() + 1,
                    resultado.getVersao().longValue()
            );
        } finally {
            executor.shutdownNow();
        }
    }

    private RuntimeException capturarExcecao(Runnable operacao) {
        try {
            operacao.run();
            return null;
        } catch (RuntimeException exception) {
            return exception;
        }
    }
}