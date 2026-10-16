package br.com.washii.api.repository;

import br.com.washii.api.model.CategoriaServico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoriaServicoRepository extends JpaRepository<CategoriaServico, UUID> {
}
