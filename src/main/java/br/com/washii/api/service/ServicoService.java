package br.com.washii.api.service;

import br.com.washii.api.dto.request.CadastroServicoRequest;
import br.com.washii.api.dto.request.CategoriaVeiculoServicoRequest;
import br.com.washii.api.model.*;
import br.com.washii.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public void salvarServico(UUID lavaJatoId, CadastroServicoRequest request){
        LavaJato lavaJato = lavaJatoRepository.findById(lavaJatoId)
                .orElseThrow(() -> new IllegalArgumentException("Lava Jato não encontrado com o id = " + lavaJatoId));

        CategoriaServico categoria = catServicoRepository.findById(request.categoriaServicoId())
                .orElseThrow(() -> new IllegalArgumentException("Categoria de veículo não encontrada com o id = " + request.categoriaServicoId()));

        Servico servico = new Servico();

        servico.setCategoriaServico(categoria);
        servico.setNome(request.nome());
        servico.setDescricao(request.descricao());
        servico.setAtivo(true);
        servico.setLavaJato(lavaJato);

        servicoRepository.save(servico);
    }

    public List<Servico> buscarServicosPorLavaJato(UUID lavaJatoId) {
        LavaJato lavaJato = lavaJatoRepository.findById(lavaJatoId)
                .orElseThrow(() -> new IllegalArgumentException("Lava Jato não encontrado com o id = " + lavaJatoId));

        return servicoRepository.findAllByLavaJato(lavaJato);
    }

    public Servico buscarPorId(UUID lavaJatoId, UUID servicoId) {
        LavaJato lavaJato = lavaJatoRepository.findById(lavaJatoId)
                .orElseThrow(() -> new IllegalArgumentException("Lava Jato não encontrado com o id = " + lavaJatoId));

        Servico servico = servicoRepository.findById(servicoId)
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado com o id = " + servicoId));

        if (!servicoRepository.existsByIdAndLavaJato(servicoId, lavaJato)){
            throw new IllegalArgumentException("O serviço não pertence ao lava jato informado");
        }

        return servico;
    }

    public void atualizarServico(UUID lavaJatoId, UUID servicoId, CadastroServicoRequest request) {
        CategoriaServico categoria = catServicoRepository.findById(request.categoriaServicoId())
                .orElseThrow(() -> new IllegalArgumentException("Categoria de veículo não encontrada com o id = " + request.categoriaServicoId()));

        LavaJato lavaJato = lavaJatoRepository.findById(lavaJatoId)
                .orElseThrow(() -> new IllegalArgumentException("Lava Jato não encontrado com o id = " + lavaJatoId));

        Servico servico = servicoRepository.findById(servicoId)
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado com o id = " + servicoId));

        if (!servicoRepository.existsByIdAndLavaJato(servicoId, lavaJato)){
            throw new IllegalArgumentException("O serviço não pertence ao lava jato informado");
        }

        servico.setCategoriaServico(categoria);
        servico.setNome(request.nome());
        servico.setDescricao(request.descricao());

        servicoRepository.save(servico);
    }

    public void deletarServico(UUID lavaJatoId, UUID servicoId) {
        LavaJato lavaJato = lavaJatoRepository.findById(lavaJatoId)
                .orElseThrow(() -> new IllegalArgumentException("Lava Jato não encontrado com o id = " + lavaJatoId));

        Servico servico = servicoRepository.findById(servicoId)
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado com o id = " + servicoId));

        if (!servicoRepository.existsByIdAndLavaJato(servicoId, lavaJato)){
            throw new IllegalArgumentException("O serviço não pertence ao lava jato informado");
        }

        servico.setAtivo(false);

        servicoRepository.save(servico);
    }

    public void customizarServicoPorCategoriaServico(
            UUID lavaJatoId, UUID servicoId, List<CategoriaVeiculoServicoRequest> requestList
    ) {
        LavaJato lavaJato = lavaJatoRepository.findById(lavaJatoId)
                .orElseThrow(() -> new IllegalArgumentException("Lava Jato não encontrado com o id = " + lavaJatoId));

        Servico servico = servicoRepository.findById(servicoId)
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado com o id = " + servicoId));

        if (!servicoRepository.existsByIdAndLavaJato(servicoId, lavaJato)){
            throw new IllegalArgumentException("O serviço não pertence ao lava jato informado");
        }

        List<CategoriaVeiculoServico> listCustom = requestList.stream()
                .map(request -> {
                    CategoriaVeiculo catVeiculo = catVeiculoRepository.findById(request.categoriaVeiculoId())
                            .orElseThrow(() ->
                                    new IllegalArgumentException("Categoria de veículo não encontrada com o id = " + request.categoriaVeiculoId()));

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
}
