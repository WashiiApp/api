package br.com.washii.api.service;

import br.com.washii.api.dto.request.CadastroServicoRequest;
import br.com.washii.api.dto.request.CategoriaVeiculoServicoRequest;
import br.com.washii.api.model.*;
import br.com.washii.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServicoService {

    private final ServicoRepository servicoRepository;
    private final CategoriaServicoRepository catServicoRepository;
    private final CategoriaVeiculoRepository catVeiculoRepository;
    private final CategoriaVeiculoServicoRepository catVeiculoServicoRepository;
    private final LavaJatoRepository lavaJatoRepository;

    @Transactional
    public void salvarServico(UUID lavaJatoId, CadastroServicoRequest request){
        LavaJato lavaJato = buscarLavaJatoOuLancar(lavaJatoId);

        CategoriaServico categoria = catServicoRepository.findById(request.categoriaServicoId())
                .orElseThrow(() -> new IllegalArgumentException("Categoria de serviço não encontrada com o id = " + request.categoriaServicoId()));

        Servico servico = new Servico();
        servico.setCategoriaServico(categoria);
        servico.setNome(request.nome());
        servico.setDescricao(request.descricao());
        servico.setAtivo(true);
        servico.setLavaJato(lavaJato);

        servicoRepository.save(servico);
    }

    @Transactional(readOnly = true)
    public List<Servico> buscarServicosPorLavaJato(UUID lavaJatoId) {
        LavaJato lavaJato = buscarLavaJatoOuLancar(lavaJatoId);
        return servicoRepository.findAllByLavaJato(lavaJato);
    }

    @Transactional(readOnly = true)
    public Servico buscarPorId(UUID lavaJatoId, UUID servicoId) {
        return validarEObterServico(lavaJatoId, servicoId);
    }

    @Transactional
    public void atualizarServico(UUID lavaJatoId, UUID servicoId, CadastroServicoRequest request) {
        Servico servico = validarEObterServico(lavaJatoId, servicoId);

        CategoriaServico categoria = catServicoRepository.findById(request.categoriaServicoId())
                .orElseThrow(() -> new IllegalArgumentException("Categoria de serviço não encontrada com o id = " + request.categoriaServicoId()));

        servico.setCategoriaServico(categoria);
        servico.setNome(request.nome());
        servico.setDescricao(request.descricao());

        servicoRepository.save(servico);
    }

    @Transactional
    public void deletarServico(UUID lavaJatoId, UUID servicoId) {
        Servico servico = validarEObterServico(lavaJatoId, servicoId);
        servico.setAtivo(false);
        servicoRepository.save(servico);
    }

    @Transactional
    public void customizarServicoPorCategoriaServico(
            UUID lavaJatoId, UUID servicoId, List<CategoriaVeiculoServicoRequest> requestList
    ) {
        Servico servico = validarEObterServico(lavaJatoId, servicoId);

        List<CategoriaVeiculoServico> listCustom = requestList.stream()
                .map(request -> {
                    CategoriaVeiculo catVeiculo = catVeiculoRepository.findById(request.categoriaVeiculoId())
                            .orElseThrow(() -> new IllegalArgumentException("Categoria de veículo não encontrada com o id = " + request.categoriaVeiculoId()));

                    CategoriaVeiculoServico customServico = new CategoriaVeiculoServico();
                    customServico.setCategoriaVeiculo(catVeiculo);
                    customServico.setDuracao(request.duracao());
                    customServico.setPreco(request.preco());
                    customServico.setServico(servico);

                    return customServico;
                })
                .toList();

        catVeiculoServicoRepository.saveAll(listCustom);
    }

    @Transactional
    public void atualizarCustomizacao(UUID lavaJatoId, UUID servicoId, UUID precoId, CategoriaVeiculoServicoRequest request) {
        validarEObterServico(lavaJatoId, servicoId);

        CategoriaVeiculoServico customServico = catVeiculoServicoRepository.findById(precoId)
                .orElseThrow(() -> new IllegalArgumentException("Customização de preço não encontrada com o id = " + precoId));

        if (!customServico.getServico().getId().equals(servicoId)) {
            throw new IllegalArgumentException("A customização informada não pertence ao serviço especificado");
        }

        CategoriaVeiculo catVeiculo = catVeiculoRepository.findById(request.categoriaVeiculoId())
                .orElseThrow(() -> new IllegalArgumentException("Categoria de veículo não encontrada com o id = " + request.categoriaVeiculoId()));

        customServico.setCategoriaVeiculo(catVeiculo);
        customServico.setDuracao(request.duracao());
        customServico.setPreco(request.preco());

        catVeiculoServicoRepository.save(customServico);
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
    }

    // --- MÉTODOS AUXILIARES PRIVADOS ---

    private LavaJato buscarLavaJatoOuLancar(UUID lavaJatoId) {
        return lavaJatoRepository.findById(lavaJatoId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Lava Jato não encontrado com o id = " + lavaJatoId
                        ));
    }

    private Servico validarEObterServico(UUID lavaJatoId, UUID servicoId) {
        LavaJato lavaJato = buscarLavaJatoOuLancar(lavaJatoId);

        Servico servico = servicoRepository.findById(servicoId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Serviço não encontrado com o id = " + servicoId
                ));

        if (!servicoRepository.existsByIdAndLavaJato(servicoId, lavaJato)) {
            throw new IllegalArgumentException("O serviço não pertence ao lava jato informado");
        }

        return servico;
    }
}