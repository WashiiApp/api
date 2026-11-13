package br.com.washii.api.service;

import br.com.washii.api.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.resilience.annotation.EnableResilientMethods;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
@SpringJUnitConfig(RetryAgendamentoTest.Config.class)
class RetryAgendamentoTest {

    @Configuration
    @EnableResilientMethods
    static class Config {
        @Bean
        OperacaoFalsa operacaoFalsa() { return new OperacaoFalsa(); }
    }

    public static class OperacaoFalsa {
        private final AtomicInteger chamadas = new AtomicInteger();

        // O estado é acessado por MÉTODOS (o proxy os repassa ao objeto real);
        // ler o campo direto do proxy devolve null.
        public int totalChamadas() { return chamadas.get(); }
        public void zerar() { chamadas.set(0); }

        @Retryable(includes = OptimisticLockingFailureException.class, maxRetries = 2, delay = 10)
        public String conflitoDuasVezesDepoisSucesso() {
            if (chamadas.incrementAndGet() <= 2) {
                throw new OptimisticLockingFailureException("conflito de versão");
            }
            return "ok";
        }

        @Retryable(includes = OptimisticLockingFailureException.class, maxRetries = 2, delay = 10)
        public void erroDeNegocio() {
            chamadas.incrementAndGet();
            throw new BusinessException("regra de negócio");
        }
    }

    @Autowired OperacaoFalsa operacao;

    @BeforeEach
    void zerar() { operacao.zerar(); }

    @Test
    void conflitoDeVersaoDeveSerRepetidoAteDarCerto() {
        assertEquals("ok", operacao.conflitoDuasVezesDepoisSucesso());
        assertEquals(3, operacao.totalChamadas());   // 1 inicial + 2 repetições
    }

    @Test
    void erroDeNegocioNaoDeveSerRepetido() {
        assertThrows(BusinessException.class, operacao::erroDeNegocio);
        assertEquals(1, operacao.totalChamadas());
    }

    // ...o teste metodosCriticosDoServiceDevemTerRetryable continua igual
    @Test
    void metodosCriticosDoServiceDevemTerRetryable() {
        for (String nome : List.of("cancelarPeloCliente", "atualizarStatus")) {
            Method metodo = Arrays.stream(AgendamentoService.class.getDeclaredMethods())
                    .filter(m -> m.getName().equals(nome))
                    .findFirst().orElseThrow();
            Retryable retryable = metodo.getAnnotation(Retryable.class);
            assertNotNull(retryable, nome + " precisa de @Retryable");
            assertTrue(Arrays.asList(retryable.includes()).contains(OptimisticLockingFailureException.class),
                    nome + " precisa repetir OptimisticLockingFailureException");
        }
    }
}
