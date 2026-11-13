package br.com.washii.api.service;

import br.com.washii.api.model.Agendamento;
import br.com.washii.api.model.StatusAgendamento;
import br.com.washii.api.repository.AgendamentoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "WASHII_TEST_AGENDAMENTO_ID", matches = ".+")
class AgendamentoBloqueioOtimistalTest {

    @Autowired AgendamentoRepository repository;
    @Autowired PlatformTransactionManager txManager;

    @Test
    void gravacaoComVersaoVelhaDeveGerarConflito() {
        String idTexto = System.getenv("WASHII_TEST_AGENDAMENTO_ID");
        assumeTrue(idTexto != null && !idTexto.isBlank(),
                "Defina WASHII_TEST_AGENDAMENTO_ID com o id de um agendamento de teste");
        UUID id = UUID.fromString(idTexto);
        TransactionTemplate tx = new TransactionTemplate(txManager);

        // 1) Os dois "usuários" leem a mesma versão (os dois Pix do slide)
        Agendamento leituraA = tx.execute(s -> repository.findById(id).orElseThrow());
        Agendamento leituraB = tx.execute(s -> repository.findById(id).orElseThrow());

        StatusAgendamento original = leituraA.getStatusAgendamento();
        List<StatusAgendamento> outros = Arrays.stream(StatusAgendamento.values())
                .filter(s -> s != original).toList();

        try {
            // 2) A grava primeiro: a versão sobe de N para N+1
            leituraA.setStatusAgendamento(outros.get(0));
            repository.saveAndFlush(leituraA);

            // 3) B tenta gravar com a versão velha: conflito
            leituraB.setStatusAgendamento(outros.get(1));
            assertThrows(OptimisticLockingFailureException.class,
                    () -> repository.saveAndFlush(leituraB));
        } finally {
            // Restaura o agendamento de teste
            tx.executeWithoutResult(s ->
                    repository.findById(id).orElseThrow().setStatusAgendamento(original));
        }
    }
}