package br.com.washii.api.repository;

import br.com.washii.api.model.Usuario;
import br.com.washii.api.model.UsuarioTelefone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioTelefoneRepository extends JpaRepository<UsuarioTelefone, UUID> {
    Optional<UsuarioTelefone> findFirstByUsuario(Usuario usuario);
    List<UsuarioTelefone> findAllByUsuario(Usuario usuario);
    void deleteAllByUsuario(Usuario usuario);
}
