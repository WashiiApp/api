package br.com.washii.api.dto.response;

import br.com.washii.api.model.Servico;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public record ServicoResponse(
        UUID id,
        String nome,
        String descricao,
        boolean ativo,
        CategoriaResumoDTO categoriaServico,
        List<CategoriaVeiculoServicoResponse> custom
) implements Serializable {

    public static ServicoResponse fromEntity(Servico servico) {
        if (servico == null) {
            return null;
        }

        CategoriaResumoDTO categoriaDto = null;
        if (servico.getCategoriaServico() != null) {
            categoriaDto = new CategoriaResumoDTO(
                    servico.getCategoriaServico().getId(),
                    servico.getCategoriaServico().getNome() // Ajuste o getter caso o nome do método seja diferente
            );
        }

        List<CategoriaVeiculoServicoResponse> listCustom = null;
        if (servico.getCustom() != null) {
            listCustom = servico.getCustom().stream()
                    .map(CategoriaVeiculoServicoResponse::fromEntity)
                    .toList();
        }

        return new ServicoResponse(
                servico.getId(),
                servico.getNome(),
                servico.getDescricao(),
                servico.isAtivo(),
                categoriaDto,
                listCustom
        );
    }

    public record CategoriaResumoDTO(
            UUID id,
            String nome
    ) implements Serializable{}
}
