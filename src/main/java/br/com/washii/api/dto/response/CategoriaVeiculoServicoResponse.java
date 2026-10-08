package br.com.washii.api.dto.response;

import br.com.washii.api.model.CategoriaVeiculoServico;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

public record CategoriaVeiculoServicoResponse(
        UUID id,
        LocalTime duracao,
        BigDecimal preco,
        CategoriaVeiculoResumoDTO categoriaVeiculo
) {

    public static CategoriaVeiculoServicoResponse fromEntity(CategoriaVeiculoServico entity) {
        if (entity == null) {
            return null;
        }

        CategoriaVeiculoResumoDTO categoriaDto = null;
        if (entity.getCategoriaVeiculo() != null) {
            categoriaDto = new CategoriaVeiculoResumoDTO(
                    entity.getCategoriaVeiculo().getId(),
                    entity.getCategoriaVeiculo().getNome() // Ajuste o getter se necessário
            );
        }

        return new CategoriaVeiculoServicoResponse(
                entity.getId(),
                entity.getDuracao(),
                entity.getPreco(),
                categoriaDto
        );
    }

    public record CategoriaVeiculoResumoDTO(
            UUID id,
            String nome
    ) {}
}