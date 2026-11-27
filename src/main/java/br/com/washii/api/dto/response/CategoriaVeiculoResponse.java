package br.com.washii.api.dto.response;

import br.com.washii.api.model.CategoriaVeiculo;

import java.util.UUID;

public record CategoriaVeiculoResponse(UUID id, String nome) {
    public static CategoriaVeiculoResponse fromEntity(CategoriaVeiculo categoria) {
        return new CategoriaVeiculoResponse(categoria.getId(), categoria.getNome());
    }
}
