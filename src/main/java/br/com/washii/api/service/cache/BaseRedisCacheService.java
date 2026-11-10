package br.com.washii.api.service.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.function.Supplier;

@Slf4j
public abstract class BaseRedisCacheService {

    protected final StringRedisTemplate redisTemplate;
    protected final ObjectMapper objectMapper;

    @Value("${spring.cache.redis.time-to-live}")
    protected Duration cacheTtl;

    protected BaseRedisCacheService(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Tenta buscar do cache Redis. Se não encontrar, executa o loader (banco ou cálculo),
     * armazena o resultado no Redis e o devolve.
     */
    protected <T> T buscarOuCarregar(String cacheKey, JavaType responseType, Supplier<T> loader) {
        try {
            String cachedJson = redisTemplate.opsForValue().get(cacheKey);
            if (cachedJson != null) {
                return objectMapper.readValue(cachedJson, responseType);
            }
        } catch (Exception e) {
            log.warn("Falha ao ler do cache Redis para a chave {}: {}. Prosseguindo sem cache.", cacheKey, e.getMessage());
        }

        T data = loader.get();

        try {
            String jsonToCache = objectMapper.writeValueAsString(data);
            redisTemplate.opsForValue().set(cacheKey, jsonToCache, cacheTtl);
        } catch (Exception e) {
            log.warn("Falha ao gravar no cache Redis para a chave {}: {}.", cacheKey, e.getMessage());
        }

        return data;
    }

    /**
     * Versão simplificada para classes concretas (como Records/DTOs únicos)
     */
    protected <T> T buscarOuCarregar(String cacheKey, Class<T> responseType, Supplier<T> loader) {
        return buscarOuCarregar(cacheKey, objectMapper.constructType(responseType), loader);
    }

    protected void invalidarChave(String cacheKey) {
        try {
            redisTemplate.delete(cacheKey);
        } catch (Exception e) {
            log.warn("Falha ao invalidar o cache Redis para a chave {}: {}.", cacheKey, e.getMessage());
        }
    }
}