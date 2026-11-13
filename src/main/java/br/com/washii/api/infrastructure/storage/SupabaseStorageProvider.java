package br.com.washii.api.infrastructure.storage;

import br.com.washii.api.exception.ExternalServiceException;
import br.com.washii.api.service.StorageProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Component
@RequiredArgsConstructor
public class SupabaseStorageProvider implements StorageProvider {

    private final S3Client s3Client;

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.storage.bucket}")
    private String bucket;

    @Override
    public String upload(String caminhoArquivo, byte[] conteudo, String contentType) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(caminhoArquivo)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(conteudo));

            return supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + caminhoArquivo;

        } catch (S3Exception exception) {
            throw new ExternalServiceException("Não foi possível enviar o arquivo para o provedor de armazenamento S3.", exception);
        } catch (SdkClientException exception) {
            throw new ExternalServiceException("Não foi possível conectar ao provedor de armazenamento S3.", exception);
        }
    }

    @Override
    public void excluir(String caminhoArquivo) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(caminhoArquivo)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);

        } catch (S3Exception exception) {
            throw new ExternalServiceException("Não foi possível excluir o arquivo no provedor de armazenamento S3.", exception);
        } catch (SdkClientException exception) {
            throw new ExternalServiceException("Não foi possível conectar ao provedor de armazenamento S3.", exception);
        }
    }
}
