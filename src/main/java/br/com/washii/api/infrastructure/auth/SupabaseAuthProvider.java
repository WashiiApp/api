package br.com.washii.api.infrastructure.auth;

import br.com.washii.api.dto.response.LoginResponse;
import br.com.washii.api.exception.BusinessException;
import br.com.washii.api.exception.ExternalServiceException;
import br.com.washii.api.exception.InvalidCredentialsException;
import br.com.washii.api.exception.ValidationException;
import br.com.washii.api.dto.request.SupabaseAuthRequest;
import br.com.washii.api.dto.response.SupabaseAuthResponse;
import br.com.washii.api.service.AuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

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

        SupabaseAuthResponse response;
        try {
            response = restClient
                    .post()
                    .uri(supabaseUrl + "/auth/v1/signup")
                    .header("apikey", publicKey)
                    .body(request)
                    .retrieve()
                    .body(SupabaseAuthResponse.class);
        } catch (RestClientResponseException exception) {
            throw mapSignupException(exception);
        } catch (RestClientException exception) {
            throw new ExternalServiceException("Não foi possível conectar ao provedor de autenticação.", exception);
        }


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

        try {
            return restClient
                    .post()
                    .uri(supabaseUrl + "/auth/v1/token?grant_type=password")
                    .header("apikey", publicKey)
                    .body(request)
                    .retrieve()
                    .body(LoginResponse.class);
        } catch (RestClientResponseException exception) {
            if (exception.getStatusCode().value() == 400 || exception.getStatusCode().value() == 401) {
                throw new InvalidCredentialsException("E-mail ou senha inválidos.");
            }
            throw new ExternalServiceException("O provedor de autenticação não pôde concluir o login.", exception);
        } catch (RestClientException exception) {
            throw new ExternalServiceException("Não foi possível conectar ao provedor de autenticação.", exception);
        }
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

    private RuntimeException mapSignupException(RestClientResponseException exception) {
        return switch (exception.getStatusCode().value()) {
            case 400, 422 -> new ValidationException("Os dados de cadastro foram rejeitados pelo provedor de autenticação.");
            case 409 -> new BusinessException("Já existe uma conta com os dados informados.");
            default -> new ExternalServiceException("O provedor de autenticação não pôde concluir o cadastro.", exception);
        };
    }
}
