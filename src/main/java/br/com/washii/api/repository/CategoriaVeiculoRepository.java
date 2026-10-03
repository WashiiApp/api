package br.com.washii.api.repository;

import br.com.washii.api.model.CategoriaVeiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CategoriaVeiculoRepository extends JpaRepository<CategoriaVeiculo, UUID> {
    Optional<CategoriaVeiculo> findByNome(String nome);
}
