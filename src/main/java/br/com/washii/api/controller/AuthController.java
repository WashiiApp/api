package br.com.washii.api.controller;

import br.com.washii.api.controller.dto.CadastroUsuarioRequest;
import br.com.washii.api.controller.dto.LoginRequest;
import br.com.washii.api.controller.dto.LoginResponse;
import br.com.washii.api.model.Usuario;
import br.com.washii.api.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<Void> cadastrar(@RequestBody CadastroUsuarioRequest request) {
        authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Usuario> logout(@RequestBody Usuario user) {
        return null;
    }
}
