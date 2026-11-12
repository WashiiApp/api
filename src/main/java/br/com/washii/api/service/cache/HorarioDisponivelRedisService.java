package br.com.washii.api.service.cache;

import br.com.washii.api.dto.response.HorariosDisponiveisResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;
import java.util.function.Supplier;

@Slf4j
@Service
public class HorarioDisponivelRedisService extends BaseRedisCacheService {

    private static final String HORARIOS_CACHE_PREFIX = "horariosDisponiveis::";

    public HorarioDisponivelRedisService(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper, Duration.ofMinutes(5));
    }

    public HorariosDisponiveisResponse buscarOuCalcular(
            UUID lavaJatoId,
            LocalDate data,
            Supplier<HorariosDisponiveisResponse> loader
    ) {
        String cacheKey = HORARIOS_CACHE_PREFIX + lavaJatoId + "::" + data;
        return buscarOuCarregar(cacheKey, HorariosDisponiveisResponse.class, loader);
    }

    public void invalidarCache(UUID lavaJatoId, LocalDate data) {
        invalidarChave(HORARIOS_CACHE_PREFIX + lavaJatoId + "::" + data);
    }
}