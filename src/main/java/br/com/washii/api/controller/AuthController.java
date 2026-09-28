package br.com.washii.api.controller;

import br.com.washii.api.model.Usuario;
import br.com.washii.api.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Usuario> register(@RequestBody Usuario user) {
        return null;
    }

    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@RequestBody Usuario user) {
        return null;
    }

    @PostMapping("/logout")
    public ResponseEntity<Usuario> logout(@RequestBody Usuario user) {
        return null;
    }
}
