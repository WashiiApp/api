package br.com.washii.api.infrastructure.auth.dto;

import java.util.UUID;

public record SupabaseAuthResponse(
        SupabaseUser user
) {
    public record SupabaseUser(
        UUID id
    ){}
}
