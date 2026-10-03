package br.com.washii.api.controller;

import br.com.washii.api.controller.dto.request.CadastroClienteRequest;
import br.com.washii.api.controller.dto.request.CadastroLavaJatoRequest;
import br.com.washii.api.controller.dto.request.LoginRequest;
import br.com.washii.api.controller.dto.response.LoginResponse;
import br.com.washii.api.model.Usuario;
import br.com.washii.api.service.AuthService;
import jakarta.validation.Valid;
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

    @PostMapping("/register/cliente")
    public ResponseEntity<Void> cadastrarCliente(@Valid @RequestBody CadastroClienteRequest request) {
        authService.cadastrarCliente(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/register/lava-jato")
    public ResponseEntity<Void> cadastrarLavaJato(@Valid @RequestBody CadastroLavaJatoRequest request) {
        authService.cadastrarLavaJato(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Usuario> logout(@RequestBody Usuario user) {
        //TODO
        return null;
    }
}
