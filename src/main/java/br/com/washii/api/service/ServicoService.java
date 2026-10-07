package br.com.washii.api.service;

import br.com.washii.api.dto.request.CadastroServicoRequest;
import br.com.washii.api.model.Servico;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ServicoService {

    public void salvarServico(UUID lavaJatoId, CadastroServicoRequest request){

    }

    public List<Servico> buscarServicosPorLavaJato(UUID lavaJatoId) {
        return List.of();
    }
}
