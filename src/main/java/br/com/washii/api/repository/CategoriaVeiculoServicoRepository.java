package br.com.washii.api.repository;

import br.com.washii.api.model.CategoriaVeiculo;
import br.com.washii.api.model.CategoriaVeiculoServico;
import br.com.washii.api.model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CategoriaVeiculoServicoRepository extends JpaRepository<CategoriaVeiculoServico, UUID> {
    Optional<CategoriaVeiculoServico> findByCategoriaVeiculoAndServico(CategoriaVeiculo categoriaVeiculo, Servico servico);
}
