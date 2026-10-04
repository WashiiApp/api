package br.com.washii.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CadastroLavaJatoRequest (
        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "Informe um e-mail válido.")
        @Size(max = 254, message = "O e-mail deve ter no máximo 254 caracteres.")
        String email,
        @NotBlank(message = "A senha é obrigatória.")
        @Size(min = 8, max = 72, message = "A senha deve ter entre 8 e 72 caracteres.")
        String senha,
        @NotBlank(message = "A cidade é obrigatória.")
        @Size(max = 100, message = "A cidade deve ter no máximo 100 caracteres.")
        String cidade,
        @NotBlank(message = "O estado é obrigatório.")
        @Pattern(regexp = "[A-Za-z]{2}", message = "O estado deve ser informado pela sigla de duas letras.")
        String estado,
        @NotBlank(message = "A razão social é obrigatória.")
        @Size(max = 200, message = "A razão social deve ter no máximo 200 caracteres.")
        String razaoSocial,
        @NotBlank(message = "O nome fantasia é obrigatório.")
        @Size(max = 150, message = "O nome fantasia deve ter no máximo 150 caracteres.")
        String nomeFantasia,
        @NotNull(message = "O fluxo simultâneo é obrigatório.")
        @Min(value = 1, message = "O fluxo simultâneo deve ser maior que zero.")
        Integer fluxoSimultaneo,
        @NotBlank(message = "O CNPJ é obrigatório.")
        @Pattern(regexp = "(?:\\d{14}|\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2})", message = "Informe um CNPJ válido com 14 dígitos, com ou sem formatação.")
        String cnpj,
        @NotBlank(message = "O número é obrigatório.")
        @Size(max = 10, message = "O número deve ter no máximo 10 caracteres.")
        String numero,
        @NotBlank(message = "O logradouro é obrigatório.")
        @Size(max = 150, message = "O logradouro deve ter no máximo 150 caracteres.")
        String logradouro,
        @NotBlank(message = "O bairro é obrigatório.")
        @Size(max = 100, message = "O bairro deve ter no máximo 100 caracteres.")
        String bairro,
        @NotBlank(message = "O CEP é obrigatório.")
        @Pattern(regexp = "^(?:\\d{8}|\\d{5}-\\d{3})$", message = "Informe um CEP válido.")
        String cep,
        @Valid
        Coordenadas coordenadas
) {
    public record Coordenadas (
            @NotNull(message = "A latitude é obrigatória.")
            @DecimalMin(value = "-90.0", message = "A latitude deve estar entre -90 e 90.")
            @DecimalMax(value = "90.0", message = "A latitude deve estar entre -90 e 90.")
            Double latitude,
            @NotNull(message = "A longitude é obrigatória.")
            @DecimalMin(value = "-180.0", message = "A longitude deve estar entre -180 e 180.")
            @DecimalMax(value = "180.0", message = "A longitude deve estar entre -180 e 180.")
            Double longitude
    ){}
}
