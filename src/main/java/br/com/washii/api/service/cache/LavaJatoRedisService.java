package br.com.washii.api.service.cache;

import br.com.washii.api.dto.response.LavaJatoResponse;
import br.com.washii.api.exception.ResourceNotFoundException;
import br.com.washii.api.model.LavaJato;
import br.com.washii.api.repository.LavaJatoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
public class LavaJatoRedisService extends BaseRedisCacheService {

    private final LavaJatoRepository lavaJatoRepository;

    private static final String CACHE_PREFIX = "lavaJatoPerfil::";

    public LavaJatoRedisService(
            StringRedisTemplate redisTemplate,
            ObjectMapper objectMapper,
            LavaJatoRepository lavaJatoRepository
    ) {
        super(redisTemplate, objectMapper, Duration.ofMinutes(30));
        this.lavaJatoRepository = lavaJatoRepository;
    }

    @Transactional(readOnly = true)
    public LavaJatoResponse buscarPorId(UUID id) {
        String cacheKey = CACHE_PREFIX + id;

        // Tenta buscar do cache Redis; caso contrário, executa a busca no banco e armazena
        return buscarOuCarregar(cacheKey, LavaJatoResponse.class, () -> {
            LavaJato lavaJato = lavaJatoRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Lava-jato não encontrado com o id = " + id));

            return LavaJatoResponse.fromEntity(lavaJato);
        });
    }

    public void invalidarCache(UUID id) {
        invalidarChave(CACHE_PREFIX + id);
    }
}