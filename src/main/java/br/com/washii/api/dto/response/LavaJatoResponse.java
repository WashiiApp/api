package br.com.washii.api.dto.response;

import br.com.washii.api.model.LavaJato;

import java.util.UUID;

public record LavaJatoResponse(
        UUID id,
        String email,
        byte[] fotoPerfil,
        boolean ativo,
        String cidade,
        String estado,
        String razaoSocial,
        String nomeFantasia,
        Integer fluxoSimultaneo,
        String cnpj,
        EnderecoResponseDTO endereco,
        CoordenadasResponseDTO coordenadas
) {

    public static LavaJatoResponse fromEntity(LavaJato lavaJato) {
        if (lavaJato == null) {
            return null;
        }

        // Mapeando o endereço estruturado direto dos atributos da entidade LavaJato e Usuario
        EnderecoResponseDTO enderecoDto = new EnderecoResponseDTO(
                lavaJato.getLogradouro(),
                lavaJato.getNumero(),
                lavaJato.getBairro(),
                lavaJato.getCidade(), // herdado de Usuario
                lavaJato.getEstado(), // herdado de Usuario
                lavaJato.getCep()
        );

        // Extraindo Latitude (Y) e Longitude (X) do objeto Point do Hibernate Spatial (JTS)
        CoordenadasResponseDTO coordenadasDto = null;
        if (lavaJato.getCoordenadas() != null) {
            coordenadasDto = new CoordenadasResponseDTO(
                    lavaJato.getCoordenadas().getY(), // Latitude
                    lavaJato.getCoordenadas().getX()  // Longitude
            );
        }

        return new LavaJatoResponse(
                lavaJato.getId(),
                lavaJato.getEmail(),
                lavaJato.getFotoPerfil(),
                lavaJato.isAtivo(),
                lavaJato.getCidade(),
                lavaJato.getEstado(),
                lavaJato.getRazaoSocial(),
                lavaJato.getNomeFantasia(),
                lavaJato.getFluxoSimultaneo(),
                lavaJato.getCnpj(),
                enderecoDto,
                coordenadasDto
        );
    }

    public record EnderecoResponseDTO(
            String logradouro,
            String numero,
            String bairro,
            String cidade,
            String estado,
            String cep
    ) {}

    public record CoordenadasResponseDTO(
            Double latitude,
            Double longitude
    ) {}
}