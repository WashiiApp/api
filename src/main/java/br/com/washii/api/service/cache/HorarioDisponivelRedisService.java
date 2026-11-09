package br.com.washii.api.service.cache;

import br.com.washii.api.dto.response.HorariosDisponiveisResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class HorarioDisponivelRedisService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.cache.redis.time-to-live}")
    private Duration cacheTtl;

    private static final String HORARIOS_CACHE_PREFIX = "horariosDisponiveis::";

    public HorariosDisponiveisResponse buscarOuCalcular(
            UUID lavaJatoId,
            LocalDate data,
            Supplier<HorariosDisponiveisResponse> loader
    ) {
        // Chave limpa combinando apenas Lava Jato e Data
        String cacheKey = HORARIOS_CACHE_PREFIX + lavaJatoId + "::" + data;

        // 1. Tenta buscar do Redis
        try {
            String cachedJson = redisTemplate.opsForValue().get(cacheKey);
            if (cachedJson != null) {
                return objectMapper.readValue(cachedJson, HorariosDisponiveisResponse.class);
            }
        } catch (Exception e) {
            log.warn("Falha ao ler cache de horários para a chave {}: {}. Prosseguindo para o cálculo.", cacheKey, e.getMessage());
        }

        // 2. Se não estiver no cache, executa a lambda de cálculo
        HorariosDisponiveisResponse response = loader.get();

        // 3. Salva no Redis com TTL curto (ex: 3 minutos)
        try {
            String jsonToCache = objectMapper.writeValueAsString(response);
            redisTemplate.opsForValue().set(cacheKey, jsonToCache, cacheTtl);
        } catch (Exception e) {
            log.warn("Falha ao gravar cache de horários para a chave {}: {}.", cacheKey, e.getMessage());
        }

        return response;
    }

    public void invalidarCache(UUID lavaJatoId, LocalDate data) {
        try {
            String cacheKey = HORARIOS_CACHE_PREFIX + lavaJatoId + "::" + data;
            redisTemplate.delete(cacheKey);
        } catch (Exception e) {
            log.warn("Falha ao invalidar horários do lava jato {} na data {}: {}.", lavaJatoId, data, e.getMessage());
        }
    }
}