package br.com.washii.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CadastroServicoRequest(
        @NotNull(message = "A categoria do serviço é obrigatória.")
        UUID categoriaServicoId,
        @NotBlank(message = "O nome do serviço é obrigatório.")
        @Size(max = 100, message = "O nome do serviço deve ter no máximo 100 caracteres.")
        String nome,
        @NotBlank(message = "A descrição do serviço é obrigatória.")
        String descricao
) {
}
