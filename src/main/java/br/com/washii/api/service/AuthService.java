package br.com.washii.api.service;

import br.com.washii.api.model.Usuario;
import br.com.washii.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthProvider authProvider;
    private final UsuarioRepository repository;

    public void register(){
        UUID authId = authProvider.cadastrar(
                "Email", "Senha"
        );

        Usuario user = new Usuario();
        user.setId(authId);

        repository.save(user);
    }

    public void login(){

    }

    public void logout(){

    }
}
