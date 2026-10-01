package br.com.washii.api.exception;

import java.time.Instant;

public record StandardError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
) {
}
