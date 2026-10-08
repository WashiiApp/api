package br.com.washii.api.dto.request;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

public record CategoriaVeiculoServicoRequest(
        UUID categoriaVeiculoId,
        LocalTime duracao,
        BigDecimal preco
) {}