package br.com.washii.api.infrastructure.auth;

import br.com.washii.api.controller.dto.response.LoginResponse;
import br.com.washii.api.infrastructure.auth.dto.SupabaseAuthRequest;
import br.com.washii.api.infrastructure.auth.dto.SupabaseAuthResponse;
import br.com.washii.api.service.AuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SupabaseAuthProvider implements AuthProvider {

    private final RestClient restClient;

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.public-key}")
    private String publicKey;

    @Value("${supabase.secret-key}")
    private String secretKey;

    @Override
    public UUID cadastrar(String email, String senha) {

        SupabaseAuthRequest request =
                new SupabaseAuthRequest(email, senha);

        SupabaseAuthResponse response = restClient
                .post()
                .uri(supabaseUrl + "/auth/v1/signup")
                .header("apikey", publicKey)
                .body(request)
                .retrieve()
                .body(SupabaseAuthResponse.class);


        /*
         * Se por acaso ele for nulo, o programa interrompe a execução
         *  imediatamente e lança um erro (uma AssertionError).
         *  Isso serve para evitar que o código tente acessar algo que não existe.
         * */
        assert response != null;
        return response.user().id();
    }

    @Override
    public LoginResponse autenticar(String email, String senha) {

        SupabaseAuthRequest request =
                new SupabaseAuthRequest(email, senha);

        return restClient
                .post()
                .uri(supabaseUrl + "/auth/v1/token?grant_type=password")
                .header("apikey", publicKey)
                .body(request)
                .retrieve()
                .body(LoginResponse.class);
    }

    @Override
    public void excluir(UUID id) {
        restClient
                .delete()
                .uri(supabaseUrl + "/auth/v1/admin/users/" + id)
                .header("apikey", secretKey)
                .retrieve()
                .toBodilessEntity();
    }
}
