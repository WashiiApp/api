package br.com.washii.api.service;

import java.util.UUID;

public interface AuthProvider {
    UUID cadastrar(String email, String senha);
    String autenticar(String email, String senha);
}
