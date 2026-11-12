package br.com.washii.api.service;

import br.com.washii.api.dto.request.AvaliacaoRequest;
import br.com.washii.api.dto.request.CadastroServicoRequest;
import br.com.washii.api.dto.request.CategoriaVeiculoServicoRequest;
import br.com.washii.api.dto.response.AvaliacaoResponse;
import br.com.washii.api.dto.response.ClienteResponse;
import br.com.washii.api.dto.response.ServicoResponse;
import br.com.washii.api.model.*;
import br.com.washii.api.repository.*;
import br.com.washii.api.service.cache.ServicoRedisService;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServicoService {

    private final ServicoRepository servicoRepository;
    private final CategoriaServicoRepository catServicoRepository;
    private final CategoriaVeiculoRepository catVeiculoRepository;
    private final CategoriaVeiculoServicoRepository catVeiculoServicoRepository;
    private final LavaJatoRepository lavaJatoRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final ClienteRepository clienteRepository;
    private final AgendamentoRepository agendamentoRepository;

    // Serviço de cache Redis manual
    private final ServicoRedisService servicoCache;

    // Cache local (Caffeine)
    private final Cache<UUID, CategoriaServico> categoriaServicoCache;
    private final Cache<UUID, CategoriaVeiculo> categoriaVeiculoCache;

    @Transactional
    public void salvarServico(UUID lavaJatoId, CadastroServicoRequest request){
        LavaJato lavaJato = buscarLavaJatoOuLancar(lavaJatoId);
        CategoriaServico categoria = buscarCategoriaServicoOuLancar(request.categoriaServicoId());

        Servico servico = new Servico();
        servico.setCategoriaServico(categoria);
        servico.setNome(request.nome());
        servico.setDescricao(request.descricao());
        servico.setAtivo(true);
        servico.setLavaJato(lavaJato);

        servicoRepository.save(servico);
        servicoCache.invalidarCache(lavaJatoId);
    }

    public List<ServicoResponse> buscarServicosPorLavaJato(UUID lavaJatoId) {
        // Delega para a camada de cache Redis
        return servicoCache.buscarServicosPorLavaJato(lavaJatoId);
    }

    @Transactional(readOnly = true)
    public Servico buscarPorId(UUID lavaJatoId, UUID servicoId) {
        return validarEObterServico(lavaJatoId, servicoId);
    }

    @Transactional
    public void atualizarServico(UUID lavaJatoId, UUID servicoId, CadastroServicoRequest request) {
        Servico servico = validarEObterServico(lavaJatoId, servicoId);
        CategoriaServico categoria = buscarCategoriaServicoOuLancar(request.categoriaServicoId());

        servico.setCategoriaServico(categoria);
        servico.setNome(request.nome());
        servico.setDescricao(request.descricao());

        servicoRepository.save(servico);
        servicoCache.invalidarCache(lavaJatoId);
    }

    @Transactional
    public void deletarServico(UUID lavaJatoId, UUID servicoId) {
        Servico servico = validarEObterServico(lavaJatoId, servicoId);
        servico.setAtivo(false);
        servicoRepository.save(servico);
        servicoCache.invalidarCache(lavaJatoId);
    }

    @Transactional(readOnly = true)
    public List<CategoriaVeiculoServico> buscarCustomizacoesDeServicos(
            UUID lavaJatoId, UUID servicoId) {
        Servico servico = validarEObterServico(lavaJatoId, servicoId);
        return catVeiculoServicoRepository.findAllByServico(servico);
    }

    @Transactional
    public void customizarServicoPorCategoriaServico(
            UUID lavaJatoId, UUID servicoId, List<CategoriaVeiculoServicoRequest> requestList
    ) {
        Servico servico = validarEObterServico(lavaJatoId, servicoId);

        List<CategoriaVeiculoServico> listCustom = requestList.stream()
                .map(request -> {
                    CategoriaVeiculo catVeiculo = buscarCategoriaVeiculoOuLancar(request.categoriaVeiculoId());

                    CategoriaVeiculoServico customServico = new CategoriaVeiculoServico();
                    customServico.setCategoriaVeiculo(catVeiculo);
                    customServico.setDuracao(request.duracao());
                    customServico.setPreco(request.preco());
                    customServico.setServico(servico);

                    return customServico;
                })
                .toList();

        catVeiculoServicoRepository.saveAll(listCustom);
        servicoCache.invalidarCache(lavaJatoId);
    }

    @Transactional
    public void atualizarCustomizacao(UUID lavaJatoId, UUID servicoId, UUID precoId, CategoriaVeiculoServicoRequest request) {
        validarEObterServico(lavaJatoId, servicoId);

        CategoriaVeiculoServico customServico = catVeiculoServicoRepository.findById(precoId)
                .orElseThrow(() -> new IllegalArgumentException("Customização de preço não encontrada com o id = " + precoId));

        if (!customServico.getServico().getId().equals(servicoId)) {
            throw new IllegalArgumentException("A customização informada não pertence ao serviço especificado");
        }

        CategoriaVeiculo catVeiculo = buscarCategoriaVeiculoOuLancar(request.categoriaVeiculoId());

        customServico.setCategoriaVeiculo(catVeiculo);
        customServico.setDuracao(request.duracao());
        customServico.setPreco(request.preco());

        catVeiculoServicoRepository.save(customServico);
        servicoCache.invalidarCache(lavaJatoId);
    }

    @Transactional
    public void deletarCustomizacao(UUID lavaJatoId, UUID servicoId, UUID precoId) {
        validarEObterServico(lavaJatoId, servicoId);

        CategoriaVeiculoServico customServico = catVeiculoServicoRepository.findById(precoId)
                .orElseThrow(() -> new IllegalArgumentException("Customização de preço não encontrada com o id = " + precoId));

        if (!customServico.getServico().getId().equals(servicoId)) {
            throw new IllegalArgumentException("A customização informada não pertence ao serviço especificado");
        }

        catVeiculoServicoRepository.delete(customServico);
        servicoCache.invalidarCache(lavaJatoId);
    }

    @Transactional
    public List<AvaliacaoResponse> buscarAvaliacoes(UUID lavaJatoId, UUID servicoId) {
        Servico servico = validarEObterServico(lavaJatoId, servicoId);

        List<Avaliacao> avaliacaos = avaliacaoRepository.findAllByServico(servico);

        return avaliacaos.stream()
                .map(AvaliacaoResponse::fromEntity)
                .toList();
    }

    public void salvarAvaliacao(UUID lavaJatoId, UUID servicoId, AvaliacaoRequest request) {
        Servico servico = validarEObterServico(lavaJatoId, servicoId);
        Cliente cliente = buscarClienteOuLancar(request.clienteId());

        Boolean jaUsouServico = agendamentoRepository
                .existsAgendamentoConcluidoByServicoIdAndClienteId(servico.getId(), cliente.getId());
        System.out.println(jaUsouServico);

        if (!jaUsouServico) {
            throw new IllegalArgumentException(
                    "Não é possível um cliente avaliar um serviço o qual ainda não tenha usado");
        }

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setServico(servico);
        avaliacao.setCliente(cliente);
        avaliacao.setData(LocalDate.now());
        avaliacao.setNivelSatisfacao(request.nivelSatisfacao());
        avaliacao.setComentario(request.comentario());

        avaliacaoRepository.save(avaliacao);
    }

    public void apagarAvaliacao(UUID lavaJatoId, UUID servicoId, UUID avaliacaoId) {
        validarEObterServico(lavaJatoId, servicoId);
        Avaliacao avaliacao = validarEObterAvalicao(servicoId, avaliacaoId);

        avaliacaoRepository.delete(avaliacao);
    }

    // --- MÉTODOS AUXILIARES PRIVADOS ---

    private LavaJato buscarLavaJatoOuLancar(UUID lavaJatoId) {
        return lavaJatoRepository.findById(lavaJatoId)
                .orElseThrow(() -> new IllegalArgumentException("Lava Jato não encontrado com o id = " + lavaJatoId));
    }

    private @NonNull CategoriaServico buscarCategoriaServicoOuLancar(UUID categoriaServicoId) {
        return categoriaServicoCache.get(categoriaServicoId, uuid ->
                catServicoRepository.findById(uuid)
                        .orElseThrow(() -> new IllegalArgumentException("Categoria de serviço não encontrada com o id = " + uuid))
        );
    }

    private @NonNull CategoriaVeiculo buscarCategoriaVeiculoOuLancar(UUID categoriaVeiculoId) {
        return categoriaVeiculoCache.get(categoriaVeiculoId, uuid ->
                catVeiculoRepository.findById(uuid)
                        .orElseThrow(() -> new IllegalArgumentException("Categoria de veiculo não encontrada com o id = " + uuid))
        );
    }

    private Servico validarEObterServico(UUID lavaJatoId, UUID servicoId) {
        LavaJato lavaJato = buscarLavaJatoOuLancar(lavaJatoId);

        Servico servico = servicoRepository.findById(servicoId)
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado com o id = " + servicoId));

        if (Boolean.FALSE.equals(servicoRepository.existsByIdAndLavaJato(servicoId, lavaJato))) {
            throw new IllegalArgumentException("O serviço não pertence ao lava jato informado");
        }

        return servico;
    }

    private Cliente buscarClienteOuLancar(UUID id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com id = " + id));
    }

    private Avaliacao validarEObterAvalicao(UUID servicoId, UUID avaliacaoId) {
        Avaliacao avaliacao = avaliacaoRepository.findById(avaliacaoId)
                .orElseThrow(() -> new IllegalArgumentException("Não foi encontrado uma avaliacão com o id = " + avaliacaoId));

        if (!avaliacao.getServico().getId().equals(servicoId)){
            throw new IllegalArgumentException("A avaliação de id = " + avaliacaoId + " não pertence ao servico de id = " + servicoId);
        }

        return avaliacao;
    }
}