package br.com.washii.api.service;

import br.com.washii.api.dto.response.LavaJatoResponse;
import br.com.washii.api.model.LavaJato;
import br.com.washii.api.repository.LavaJatoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LavaJatoService {

    private final LavaJatoRepository lavaJatoRepository;

    // Gestão cadastral do sistema
    public LavaJatoResponse buscarPorId(UUID id){
        LavaJato lavaJato = lavaJatoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lava jato não encontrado com o id = " + id));

       return LavaJatoResponse.fromEntity(lavaJato);
    }

    // Expediente


    // Serviços


    // Operacionais e Consultas
}
