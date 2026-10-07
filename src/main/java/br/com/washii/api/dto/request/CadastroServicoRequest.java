package br.com.washii.api.dto.request;

import java.util.UUID;

public record CadastroServicoRequest(
        UUID categoriaServicoId,
        String nome,
        String descricao
) {
}
