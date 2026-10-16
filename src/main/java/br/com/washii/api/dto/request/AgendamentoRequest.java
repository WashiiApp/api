package br.com.washii.api.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record AgendamentoRequest(
        @NotNull UUID lavaJatoId,
        @NotNull UUID veiculoId,
        @NotNull LocalDate data,
        @NotNull LocalTime hora,
        @NotEmpty List<UUID> servicoIds
) {
}
