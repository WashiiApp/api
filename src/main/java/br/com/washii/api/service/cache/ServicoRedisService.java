package br.com.washii.api.service.cache;

import br.com.washii.api.dto.response.ServicoResponse;
import br.com.washii.api.model.CategoriaVeiculoServico;
import br.com.washii.api.model.LavaJato;
import br.com.washii.api.model.Servico;
import br.com.washii.api.repository.CategoriaVeiculoServicoRepository;
import br.com.washii.api.repository.LavaJatoRepository;
import br.com.washii.api.repository.ServicoRepository;
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
public class ServicoRedisService extends BaseRedisCacheService {

    private final ServicoRepository servicoRepository;
    private final LavaJatoRepository lavaJatoRepository;
    private final CategoriaVeiculoServicoRepository categoriaVeiculoServicoRepository;

    private static final String CACHE_PREFIX = "servicosLavaJato::";

    public ServicoRedisService(
            StringRedisTemplate redisTemplate,
            ObjectMapper objectMapper,
            ServicoRepository servicoRepository,
            LavaJatoRepository lavaJatoRepository,
            CategoriaVeiculoServicoRepository categoriaVeiculoServicoRepository
    ) {
        super(redisTemplate, objectMapper, Duration.ofMinutes(10));
        this.servicoRepository = servicoRepository;
        this.lavaJatoRepository = lavaJatoRepository;
        this.categoriaVeiculoServicoRepository = categoriaVeiculoServicoRepository;
    }

    @Transactional(readOnly = true)
    public List<ServicoResponse> buscarServicosPorLavaJato(UUID lavaJatoId) {
        String cacheKey = CACHE_PREFIX + lavaJatoId;

        // Define o tipo da Collection para o Jackson 3 deserializar corretamente a List
        var collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, ServicoResponse.class);

        return buscarOuCarregar(cacheKey, collectionType, () -> {
            LavaJato lavaJato = lavaJatoRepository.findById(lavaJatoId)
                    .orElseThrow(() -> new IllegalArgumentException("Lava Jato não encontrado com o id = " + lavaJatoId));

            List<Servico> servicos = servicoRepository.findAllByLavaJato(lavaJato);

            return servicos.stream()
                    .map(servico -> {
                        List<CategoriaVeiculoServico> custom = categoriaVeiculoServicoRepository.findAllByServico(servico);
                        servico.setCustom(custom);
                        return ServicoResponse.fromEntity(servico);
                    })
                    .toList();
        });
    }

    public void invalidarCache(UUID lavaJatoId) {
        invalidarChave(CACHE_PREFIX + lavaJatoId);
    }
}