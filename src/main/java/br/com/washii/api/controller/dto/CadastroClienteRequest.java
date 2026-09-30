package br.com.washii.api.controller.dto;

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
