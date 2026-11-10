package br.com.washii.api.dto.request;

import java.util.UUID;

public record AvaliacaoRequest (
        UUID clienteId,
        Integer nivelSatisfacao,
        String comentario
) {}
