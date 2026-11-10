package br.com.washii.api.service.cache;

import br.com.washii.api.dto.response.ExpedienteResponse;
import br.com.washii.api.model.Disponibilidade;
import br.com.washii.api.model.LavaJato;
import br.com.washii.api.repository.DisponibilidadeRepository;
import br.com.washii.api.repository.LavaJatoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ExpedienteRedisService extends BaseRedisCacheService {

    private final DisponibilidadeRepository disponibilidadeRepository;
    private final LavaJatoRepository lavaJatoRepository;

    private static final String CACHE_PREFIX = "expedientesLavaJato::";

    public ExpedienteRedisService(
            StringRedisTemplate redisTemplate,
            ObjectMapper objectMapper,
            DisponibilidadeRepository disponibilidadeRepository,
            LavaJatoRepository lavaJatoRepository
    ) {
        super(redisTemplate, objectMapper, Duration.ofMinutes(30));
        this.disponibilidadeRepository = disponibilidadeRepository;
        this.lavaJatoRepository = lavaJatoRepository;
    }

    @Transactional(readOnly = true)
    public List<ExpedienteResponse> buscarExpediente(UUID lavaJatoId) {
        String cacheKey = CACHE_PREFIX + lavaJatoId;

        // Constrói o tipo da collection para o Jackson 3 lidar corretamente com List<ExpedienteResponse>
        var collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, ExpedienteResponse.class);

        return buscarOuCarregar(cacheKey, collectionType, () -> {
            // Se não estiver no cache, valida se o lava jato existe e busca do banco
            buscarLavaJatoOuLancar(lavaJatoId);

            List<Disponibilidade> disponibilidades = disponibilidadeRepository.findAllByLavaJato_IdOrderByDiasSemana_Id(lavaJatoId);

            return disponibilidades.stream()
                    .map(ExpedienteResponse::fromEntity)
                    .toList();
        });
    }

    public void invalidarCache(UUID lavaJatoId) {
        invalidarChave(CACHE_PREFIX + lavaJatoId);
    }

    private LavaJato buscarLavaJatoOuLancar(UUID id) {
        return lavaJatoRepository.findById(id)
                .orElseThrow(() -> new br.com.washii.api.exception.ResourceNotFoundException("Lava-jato não encontrado com o id = " + id));
    }
}