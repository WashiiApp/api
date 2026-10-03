package br.com.washii.api.controller.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest (
        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "Informe um e-mail válido.")
        @Size(max = 254, message = "O e-mail deve ter no máximo 254 caracteres.")
        String email,
        @NotBlank(message = "A senha é obrigatória.")
        @Size(max = 72, message = "A senha deve ter no máximo 72 caracteres.")
        String senha
) {
}
