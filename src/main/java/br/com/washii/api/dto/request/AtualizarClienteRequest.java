package br.com.washii.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AtualizarClienteRequest(
        @Email(message = "Informe um e-mail válido.")
        @Size(max = 254, message = "O e-mail deve ter no máximo 254 caracteres.")
        String email,
        @NotBlank(message = "A cidade é obrigatória.")
        @Size(max = 100, message = "A cidade deve ter no máximo 100 caracteres.")
        String cidade,
        @NotBlank(message = "O estado é obrigatório.")
        @Pattern(regexp = "[A-Za-z]{2}", message = "O estado deve ser informado pela sigla de duas letras.")
        String estado,
        @NotBlank(message = "O CPF é obrigatório.")
        @Pattern(regexp = "\\d{11}", message = "O CPF deve conter 11 dígitos numéricos.")
        String cpf,
        @NotBlank(message = "O nome é obrigatório.")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
        String primeiroNome,
        @NotBlank(message = "O sobrenome é obrigatório.")
        @Size(max = 100, message = "O sobrenome deve ter no máximo 100 caracteres.")
        String sobreNome
) {}
