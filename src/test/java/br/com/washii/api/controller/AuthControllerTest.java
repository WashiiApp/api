package br.com.washii.api.controller;

import br.com.washii.api.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AuthControllerTest {

    private final AuthService authService = mock(AuthService.class);
    private final AuthController authController = new AuthController(authService);

    @Test
    void deveEncerrarSessaoUsandoTokenBearerERetornarNoContent() {
        ResponseEntity<Void> response = authController.logout("Bearer jwt-access-token");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(authService).logout("jwt-access-token");
    }

    @Test
    void deveAceitarPrefixoBearerSemDiferenciarMaiusculas() {
        authController.logout("bearer jwt-access-token");

        verify(authService).logout("jwt-access-token");
    }
}
