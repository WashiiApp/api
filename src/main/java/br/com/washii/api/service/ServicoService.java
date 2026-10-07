package br.com.washii.api.service;

import br.com.washii.api.dto.request.CadastroServicoRequest;
import br.com.washii.api.model.CategoriaServico;
import br.com.washii.api.model.LavaJato;
import br.com.washii.api.model.Servico;
import br.com.washii.api.repository.CategoriaServicoRepository;
import br.com.washii.api.repository.CategoriaVeiculoRepository;
import br.com.washii.api.repository.LavaJatoRepository;
import br.com.washii.api.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServicoService {

    private final ServicoRepository servicoRepository;
    private final CategoriaServicoRepository catServicoRepository;
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
        return List.of();
    }
}
