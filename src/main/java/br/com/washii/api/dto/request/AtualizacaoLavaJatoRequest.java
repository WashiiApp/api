package br.com.washii.api.dto.request;

public record AtualizacaoLavaJatoRequest(
        byte[] fotoPerfil,
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
        Double latitude,
        Double longitude
) {}
