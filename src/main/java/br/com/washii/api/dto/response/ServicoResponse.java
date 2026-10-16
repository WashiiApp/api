package br.com.washii.api.dto.response;

import br.com.washii.api.model.Servico;
import java.util.UUID;

public record ServicoResponse(
        UUID id,
        String nome,
        String descricao,
        boolean ativo,
        CategoriaResumoDTO categoriaServico
) {

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

        return new ServicoResponse(
                servico.getId(),
                servico.getNome(),
                servico.getDescricao(),
                servico.isAtivo(),
                categoriaDto
        );
    }

    public record CategoriaResumoDTO(
            UUID id,
            String nome
    ) {}
}
