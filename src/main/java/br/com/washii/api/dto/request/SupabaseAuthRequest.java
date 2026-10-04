package br.com.washii.api.dto.request;

public record SupabaseAuthRequest(
        String email,
        String password
) {
}