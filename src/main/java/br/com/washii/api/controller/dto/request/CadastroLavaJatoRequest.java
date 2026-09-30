package br.com.washii.api.controller.dto.request;

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
