package br.com.washii.api.infrastructure.storage;

import br.com.washii.api.exception.ExternalServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SupabaseStorageProviderTest {

    private final S3Client s3Client = mock(S3Client.class);
    private final SupabaseStorageProvider storageProvider = new SupabaseStorageProvider(s3Client);

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(storageProvider, "supabaseUrl", "https://projeto.supabase.co");
        ReflectionTestUtils.setField(storageProvider, "bucket", "fotos-perfil");
    }

    @Test
    void deveRealizarUploadViaS3ERetornarUrlPublica() {
        String caminho = "clientes/123/perfil.png";
        byte[] conteudo = new byte[]{1, 2, 3};

        String url = storageProvider.upload(caminho, conteudo, "image/png");

        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(requestCaptor.capture(), any(RequestBody.class));

        PutObjectRequest putRequest = requestCaptor.getValue();
        assertEquals("fotos-perfil", putRequest.bucket());
        assertEquals(caminho, putRequest.key());
        assertEquals("image/png", putRequest.contentType());
        assertEquals("https://projeto.supabase.co/storage/v1/object/public/fotos-perfil/" + caminho, url);
    }

    @Test
    void deveLancarExternalServiceExceptionQuandoFalharUploadNoS3() {
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(S3Exception.builder().message("Erro S3").build());

        assertThrows(ExternalServiceException.class,
                () -> storageProvider.upload("clientes/123/perfil.png", new byte[]{1}, "image/png"));
    }

    @Test
    void deveExcluirArquivoViaS3() {
        String caminho = "lavajatos/456/perfil.webp";

        storageProvider.excluir(caminho);

        ArgumentCaptor<DeleteObjectRequest> requestCaptor = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(s3Client).deleteObject(requestCaptor.capture());

        DeleteObjectRequest deleteRequest = requestCaptor.getValue();
        assertEquals("fotos-perfil", deleteRequest.bucket());
        assertEquals(caminho, deleteRequest.key());
    }
}
