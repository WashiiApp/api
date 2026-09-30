package br.com.washii.api.controller.dto.response;

import br.com.washii.api.model.Cliente;
import br.com.washii.api.model.TipoUsuario;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ClienteResponse(
        UUID id,
        String email,
        boolean ativo,
        TipoUsuario tipoUsuario,
        String cidade,
        String estado,
        String cpf,
        String sobreNome,
        String primeiroNome,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static ClienteResponse from(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getEmail(),
                cliente.isAtivo(),
                cliente.getTipoUsuario(),
                cliente.getCidade(),
                cliente.getEstado(),
                cliente.getCpf(),
                cliente.getSobreNome(),
                cliente.getPrimeiroNome(),
                cliente.getCreatedAt(),
                cliente.getUpdatedAt()
        );
    }
}