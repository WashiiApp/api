package br.com.washii.api.dto.response;

import br.com.washii.api.model.Avaliacao;

import java.time.LocalDate;
import java.util.UUID;

public record AvaliacaoResponse(
        UUID id,
        UUID clienteId,
        UUID servicoId,
        LocalDate data,
        Integer nivelSatisfacao,
        String comentario
) {
    public static AvaliacaoResponse fromEntity(Avaliacao entity){
        return new AvaliacaoResponse(
                entity.getId(),
                entity.getCliente().getId(),
                entity.getServico().getId(),
                entity.getData(),
                entity.getNivelSatisfacao(),
                entity.getComentario()
        );
    }
}
