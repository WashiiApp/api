package br.com.washii.api.service;

import br.com.washii.api.repository.ClienteRepository;
import br.com.washii.api.repository.LavaJatoRepository;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AuthServiceTest {

    private final AuthProvider authProvider = mock(AuthProvider.class);
    private final AuthService authService = new AuthService(
            authProvider,
            mock(ClienteRepository.class),
            mock(LavaJatoRepository.class)
    );

    @Test
    void deveDelegarLogoutAoProvedorComAccessToken() {
        String accessToken = "jwt-access-token";

        authService.logout(accessToken);

        verify(authProvider).logout(accessToken);
    }
}
