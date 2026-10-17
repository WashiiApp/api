package br.com.washii.api.repository;

import br.com.washii.api.model.Telefone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TelefoneRepository extends JpaRepository<Telefone, UUID> {
    Optional<Telefone> findByDddAndNumero(String ddd, String numero);
}
