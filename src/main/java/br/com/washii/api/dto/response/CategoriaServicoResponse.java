package br.com.washii.api.dto.response;

import br.com.washii.api.model.CategoriaServico;

import java.util.UUID;

public record CategoriaServicoResponse(UUID id, String nome) {
    public static CategoriaServicoResponse fromEntity(CategoriaServico categoria) {
        return new CategoriaServicoResponse(categoria.getId(), categoria.getNome());
    }
}
