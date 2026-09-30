package br.com.washii.api.controller.dto.request;

public record AtualizarClienteRequest(
        String email,
        String cidade,
        String estado,
        String cpf,
        String primeiroNome,
        String sobreNome
) {}