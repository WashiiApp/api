package br.com.washii.api.service.cache;

import br.com.washii.api.dto.response.ExpedienteResponse;
import br.com.washii.api.model.Disponibilidade;
import br.com.washii.api.model.LavaJato;
import br.com.washii.api.repository.DisponibilidadeRepository;
import br.com.washii.api.repository.LavaJatoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpedienteCacheService {

    private final DisponibilidadeRepository disponibilidadeRepository;
    private final LavaJatoRepository lavaJatoRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper; // ObjectMapper do Jackson 3

    @Value("${spring.cache.redis.time-to-live}")
    private Duration cacheTtl;

    private static final String CACHE_PREFIX = "expedientesLavaJato::";

    @Transactional(readOnly = true)
    public List<ExpedienteResponse> buscarExpediente(UUID lavaJatoId) {
        String cacheKey = CACHE_PREFIX + lavaJatoId;

        // 1. Tenta recuperar do Redis em formato JSON
        try {
            String cachedJson = redisTemplate.opsForValue().get(cacheKey);
            if (cachedJson != null) {
                return objectMapper.readValue(
                        cachedJson,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, ExpedienteResponse.class)
                );
            }
        } catch (Exception e) {
            log.warn("Falha ao ler do cache Redis para a chave {}: {}. Prosseguindo para o banco de dados.", cacheKey, e.getMessage());
        }

        // 2. Se não estiver no cache, valida se o lava jato existe e busca do banco
        buscarLavaJatoOuLancar(lavaJatoId);

        List<Disponibilidade> disponibilidades = disponibilidadeRepository.findAllByLavaJato_IdOrderByDiasSemana_Id(lavaJatoId);
        List<ExpedienteResponse> responses = disponibilidades.stream()
                .map(ExpedienteResponse::fromEntity)
                .toList();

        // 3. Salva no Redis como String JSON
        try {
            String jsonToCache = objectMapper.writeValueAsString(responses);
            redisTemplate.opsForValue().set(cacheKey, jsonToCache, cacheTtl);
        } catch (Exception e) {
            log.warn("Falha ao gravar no cache Redis para a chave {}: {}.", cacheKey, e.getMessage());
        }

        return responses;
    }

    public void invalidarCache(UUID lavaJatoId) {
        String cacheKey = CACHE_PREFIX + lavaJatoId;
        try {
            redisTemplate.delete(cacheKey);
        } catch (Exception e) {
            log.warn("Falha ao invalidar o cache Redis para a chave {}: {}.", cacheKey, e.getMessage());
        }
    }

    private LavaJato buscarLavaJatoOuLancar(UUID id) {
        return lavaJatoRepository.findById(id)
                .orElseThrow(() -> new br.com.washii.api.exception.ResourceNotFoundException("Lava-jato não encontrado com o id = " + id));
    }
}