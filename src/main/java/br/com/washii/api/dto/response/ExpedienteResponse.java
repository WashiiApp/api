package br.com.washii.api.dto.response;

import br.com.washii.api.model.Disponibilidade;

import java.time.LocalTime;
import java.util.UUID;

public record ExpedienteResponse(
        UUID id,
        UUID diaSemanaId,
        String diaSemana,
        LocalTime horarioInicio,
        LocalTime horarioTermino
) {
    public static ExpedienteResponse fromEntity(Disponibilidade disponibilidade) {
        return new ExpedienteResponse(
                disponibilidade.getId(),
                disponibilidade.getDiasSemana().getId(),
                disponibilidade.getDiasSemana().getNome(),
                disponibilidade.getHrInicio(),
                disponibilidade.getHrTermino()
        );
    }
}
