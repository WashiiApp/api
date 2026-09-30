package br.com.washii.api.controller.dto;

import org.locationtech.jts.geom.Point;

public record CadastroLavaJatoRequest (
        String email,
        String senha,
        String cidade,
        String estado,
        String razaoSocial,
        String nomeFantasia,
        Integer fluxoSimultaneo,
        String cnpj,
        String numero,
        String logradouro,
        String bairro,
        String cep,
        Coordenadas coordenadas
) {
    public record Coordenadas (
            Double latitude,
            Double longitude
    ){}
}
