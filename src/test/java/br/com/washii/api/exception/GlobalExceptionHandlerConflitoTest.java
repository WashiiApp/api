package br.com.washii.api.exception;



import org.junit.jupiter.api.Test;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerConflitoTest {

    @RestController
    static class ControllerFalso {
        @GetMapping("/teste/conflito")
        void conflito() { throw new OptimisticLockingFailureException("versão antiga"); }

        @GetMapping("/teste/negocio")
        void negocio() { throw new BusinessException("regra de negócio"); }
    }

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new ControllerFalso())
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

    @Test
    void conflitoDeVersaoDeveResponder409() throws Exception {
        mockMvc.perform(get("/teste/conflito")).andExpect(status().isConflict());
    }

    // Só passa com a opção B (o status da BusinessException também precisa sair no HTTP).
    @Test
    void erroDeNegocioDeveResponder409() throws Exception {
        mockMvc.perform(get("/teste/negocio")).andExpect(status().isConflict());
    }
}
