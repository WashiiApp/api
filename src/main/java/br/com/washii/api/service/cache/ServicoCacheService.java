package br.com.washii.api.service.cache;

import br.com.washii.api.dto.response.ServicoResponse;
import br.com.washii.api.model.LavaJato;
import br.com.washii.api.model.Servico;
import br.com.washii.api.repository.LavaJatoRepository;
import br.com.washii.api.repository.ServicoRepository;
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
public class ServicoCacheService {

    @Value("${spring.cache.redis.time-to-live}")
    private Duration cacheTtl;

    private final ServicoRepository servicoRepository;
    private final LavaJatoRepository lavaJatoRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper; // ObjectMapper do Jackson 3

    private static final String CACHE_PREFIX = "servicosLavaJato::";

    @Transactional(readOnly = true)
    public List<ServicoResponse> buscarServicosPorLavaJato(UUID lavaJatoId) {
        String cacheKey = CACHE_PREFIX + lavaJatoId;

        // 1. Tenta recuperar do Redis em formato JSON
        try {
            String cachedJson = redisTemplate.opsForValue().get(cacheKey);
            if (cachedJson != null) {
                return objectMapper.readValue(
                        cachedJson,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, ServicoResponse.class)
                );
            }
        } catch (Exception e) {
            log.warn("Falha ao ler do cache Redis para a chave {}: {}. Prosseguindo para o banco de dados.", cacheKey, e.getMessage());
        }

        // 2. Se não estiver no cache (ou se o Redis falhou), busca do banco de dados
        LavaJato lavaJato = buscarLavaJatoOuLancar(lavaJatoId);
        List<Servico> servicos = servicoRepository.findAllByLavaJato(lavaJato);

        List<ServicoResponse> responses = servicos.stream()
                .map(ServicoResponse::fromEntity)
                .toList();

        // 3. Tenta salvar no Redis como String JSON (TTL de 10 minutos)
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

    private LavaJato buscarLavaJatoOuLancar(UUID lavaJatoId) {
        return lavaJatoRepository.findById(lavaJatoId)
                .orElseThrow(() -> new IllegalArgumentException("Lava Jato não encontrado com o id = " + lavaJatoId));
    }
}