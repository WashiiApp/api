package br.com.washii.api.service;

import br.com.washii.api.controller.dto.LoginResponse;

import java.util.UUID;

public interface AuthProvider {
    UUID cadastrar(String email, String senha);
    LoginResponse autenticar(String email, String senha);
}
