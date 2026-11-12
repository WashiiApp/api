package br.com.washii.api.service;

import br.com.washii.api.exception.ValidationException;
import br.com.washii.api.model.LavaJato;
import br.com.washii.api.repository.*;
import br.com.washii.api.service.cache.ExpedienteRedisService;
import br.com.washii.api.service.cache.HorarioDisponivelRedisService;
import br.com.washii.api.service.cache.LavaJatoRedisService;
import com.github.benmanes.caffeine.cache.Cache;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
class LavaJatoServiceTest {

    private final LavaJatoRepository lavaJatoRepository = mock(LavaJatoRepository.class);
    private final StorageProvider storageProvider = mock(StorageProvider.class);
    private final LavaJatoRedisService lavaJatoCache = mock(LavaJatoRedisService.class);
    private final LavaJatoService lavaJatoService = new LavaJatoService(
            lavaJatoRepository,
            mock(DisponibilidadeRepository.class),
            mock(DiasSemanaRepository.class),
            mock(TelefoneRepository.class),
            mock(UsuarioTelefoneRepository.class),
            storageProvider,
            mock(AgendamentoRepository.class),
            mock(ServicoRepository.class),
            mock(CategoriaVeiculoRepository.class),
            mock(CategoriaVeiculoServicoRepository.class),
            lavaJatoCache,
            mock(ExpedienteRedisService.class),
            mock(HorarioDisponivelRedisService.class),
            mock(Cache.class),
            mock(Cache.class)
    );

    @Test
    void deveFazerUploadDaFotoDePerfilESalvarUrlNoLavaJato() {
        UUID id = UUID.randomUUID();
        LavaJato lavaJato = new LavaJato();
        lavaJato.setId(id);
        byte[] bytesImagem = new byte[]{10, 20, 30};
        MockMultipartFile arquivo = new MockMultipartFile("arquivo", "logo.webp", "image/webp", bytesImagem);
        String caminhoEsperado = "lavajatos/" + id + "/perfil.webp";
        String urlEsperada = "https://projeto.supabase.co/storage/v1/object/public/fotos-perfil/" + caminhoEsperado;

        when(lavaJatoRepository.findById(id)).thenReturn(Optional.of(lavaJato));
        when(storageProvider.upload(caminhoEsperado, bytesImagem, "image/webp")).thenReturn(urlEsperada);

        String urlRetornada = lavaJatoService.atualizarFotoPerfil(id, arquivo);

        assertEquals(urlEsperada, urlRetornada);
        assertEquals(urlEsperada, lavaJato.getFotoPerfil());
        verify(storageProvider).upload(caminhoEsperado, bytesImagem, "image/webp");
        verify(lavaJatoRepository).save(lavaJato);
        verify(lavaJatoCache).invalidarCache(id);
    }

    @Test
    void deveRejeitarUploadDeFormatoInvalidoNoLavaJato() {
        UUID id = UUID.randomUUID();
        MockMultipartFile arquivoTxt = new MockMultipartFile("arquivo", "nota.txt", "text/plain", new byte[]{1, 2});

        assertThrows(ValidationException.class,
                () -> lavaJatoService.atualizarFotoPerfil(id, arquivoTxt));

        verifyNoInteractions(storageProvider);
    }
}
