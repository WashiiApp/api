package br.com.washii.api.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record HorariosDisponiveisResponse(
        UUID lavaJatoId,
        LocalDate data,
        long duracaoTotalMinutos,
        List<LocalTime> horariosDisponiveis
) {
}