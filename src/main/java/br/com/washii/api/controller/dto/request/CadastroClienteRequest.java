package br.com.washii.api.controller.dto.request;

public record CadastroClienteRequest (
        String email,
        String senha,
        String cidade,
        String estado,
        String cpf,
        String nome,
        String sobrenome
) {
}
