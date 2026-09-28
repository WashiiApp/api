package br.com.washii.api.infrastructure.auth;

import br.com.washii.api.service.AuthProvider;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SupabaseAuthProvider implements AuthProvider {
    @Override
    public UUID cadastrar(String email, String senha) {
        return null;
    }

    @Override
    public String autenticar(String email, String senha) {
        return "";
    }
}
