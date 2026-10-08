package br.com.washii.api.repository;

import br.com.washii.api.model.CategoriaVeiculoServico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoriaVeiculoServicoRepository extends JpaRepository<CategoriaVeiculoServico, UUID> {
}
