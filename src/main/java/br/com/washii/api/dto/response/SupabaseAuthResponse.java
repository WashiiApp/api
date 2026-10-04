package br.com.washii.api.dto.response;

import java.util.UUID;

public record SupabaseAuthResponse(
        SupabaseUser user
) {
    public record SupabaseUser(
        UUID id
    ){}
}
