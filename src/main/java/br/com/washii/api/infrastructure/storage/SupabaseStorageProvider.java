package br.com.washii.api.infrastructure.storage;

import br.com.washii.api.exception.ExternalServiceException;
import br.com.washii.api.service.StorageProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Component
@RequiredArgsConstructor
public class SupabaseStorageProvider implements StorageProvider {

    private final RestClient restClient;

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.secret-key}")
    private String secretKey;

    @Value("${supabase.storage.bucket}")
    private String bucket;

    @Override
    public String upload(String caminhoArquivo, byte[] conteudo, String contentType) {
        try {
            restClient.post()
                    .uri(supabaseUrl + "/storage/v1/object/" + bucket + "/" + caminhoArquivo)
                    .header("apikey", secretKey)
                    .header("Authorization", "Bearer " + secretKey)
                    .header("x-upsert", "true")
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(conteudo)
                    .retrieve()
                    .toBodilessEntity();

            return supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + caminhoArquivo;

        } catch (RestClientResponseException exception) {
            throw new ExternalServiceException("Não foi possível enviar o arquivo para o provedor de armazenamento.", exception);
        } catch (RestClientException exception) {
            throw new ExternalServiceException("Não foi possível conectar ao provedor de armazenamento.", exception);
        }
    }

    @Override
    public void excluir(String caminhoArquivo) {
        try {
            restClient.delete()
                    .uri(supabaseUrl + "/storage/v1/object/" + bucket + "/" + caminhoArquivo)
                    .header("apikey", secretKey)
                    .header("Authorization", "Bearer " + secretKey)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException exception) {
            throw new ExternalServiceException("Não foi possível excluir o arquivo no provedor de armazenamento.", exception);
        } catch (RestClientException exception) {
            throw new ExternalServiceException("Não foi possível conectar ao provedor de armazenamento.", exception);
        }
    }
}

