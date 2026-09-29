package br.com.washii.api.service;

import br.com.washii.api.controller.dto.CadastroUsuarioRequest;
import br.com.washii.api.controller.dto.LoginRequest;
import br.com.washii.api.controller.dto.LoginResponse;
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

    public void register(CadastroUsuarioRequest request){
        UUID id = authProvider.cadastrar(
                request.email(),
                request.senha()
        );

        Usuario user = new Usuario();
        user.setId(id);
        user.setAtivo(true);
        user.setEmail(request.email());
        user.setCidade(request.cidade());
        user.setEstado(request.estado());
        user.setTipoUsuario(request.tipoUsuario());

        repository.save(user);
    }

    public LoginResponse login(LoginRequest request){
        return authProvider.autenticar(
                request.email(),
                request.senha()
        );
    }

    public void logout(){

    }
}
