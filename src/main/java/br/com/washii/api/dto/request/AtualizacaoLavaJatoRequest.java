package br.com.washii.api.dto.request;

public record AtualizacaoLavaJatoRequest(
        String fotoPerfil,
        String razaoSocial,
        String nomeFantasia,
        Integer fluxoSimultaneo,
        String cnpj,
        String cidade,
        String estado,
        String logradouro,
        String numero,
        String bairro,
        String cep,
        String telefone,
        Double latitude,
        Double longitude
) {}
