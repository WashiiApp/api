package br.com.washii.api.controller.dto;

import br.com.washii.api.model.CategoriaVeiculo;

public record VeiculoDTO(
        String placa,
        String marca,
        String modelo,
        String cor,
        String categoria // MOTO, CARRO, SUV, PICKUP, etc.
) {}