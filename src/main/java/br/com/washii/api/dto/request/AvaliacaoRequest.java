package br.com.washii.api.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AvaliacaoRequest (
        UUID clienteId,
        @NotNull(message = "A nota de satisfação é obrigatória.")
        @Min(value = 1, message = "A nota de satisfação deve ser no mínimo 1.")
        @Max(value = 5, message = "A nota de satisfação deve ser no máximo 5.")
        Integer nivelSatisfacao,
        String comentario
) {}
