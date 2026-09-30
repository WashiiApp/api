package br.com.washii.api.repository;

import br.com.washii.api.model.LavaJato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LavaJatoRepository extends JpaRepository<LavaJato, UUID> {
}
