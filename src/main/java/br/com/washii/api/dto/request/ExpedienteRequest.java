package br.com.washii.api.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.AssertTrue;

import java.time.LocalTime;
import java.util.UUID;

public record ExpedienteRequest(
        @NotNull(message = "O dia da semana é obrigatório.")
        UUID diaSemanaId,
        @NotNull(message = "O horário de início é obrigatório.")
        LocalTime horarioInicio,
        @NotNull(message = "O horário de término é obrigatório.")
        LocalTime horarioTermino
) {
    @AssertTrue(message = "O horário de término deve ser posterior ao horário de início.")
    public boolean isHorarioValido() {
        return horarioInicio == null || horarioTermino == null || horarioTermino.isAfter(horarioInicio);
    }
}
