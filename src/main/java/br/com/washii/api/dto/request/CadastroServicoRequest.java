package br.com.washii.api.dto.request;

import br.com.washii.api.model.Servico;

import java.util.UUID;

public record CadastroServicoRequest(
        UUID categoriaServicoId,
        String nome,
        String descricao
) {
}
