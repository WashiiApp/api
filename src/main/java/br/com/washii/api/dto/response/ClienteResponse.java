package br.com.washii.api.dto.response;

import br.com.washii.api.model.Cliente;
import br.com.washii.api.model.TipoRemetente;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ClienteResponse(
        UUID id,
        String email,
        boolean ativo,
        TipoRemetente tipoRemetente,
        String cidade,
        String estado,
        String cpf,
        String sobreNome,
        String primeiroNome,
        String telefone,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static ClienteResponse from(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getEmail(),
                cliente.isAtivo(),
                cliente.getTipoRemetente(),
                cliente.getCidade(),
                cliente.getEstado(),
                cliente.getCpf(),
                cliente.getSobreNome(),
                cliente.getPrimeiroNome(),
                cliente.getTelefone(),
                cliente.getCreatedAt(),
                cliente.getUpdatedAt()
        );
    }
}