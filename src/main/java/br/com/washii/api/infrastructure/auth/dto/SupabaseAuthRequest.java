package br.com.washii.api.infrastructure.auth.dto;

public record SupabaseAuthRequest(
        String email,
        String password
) {
}