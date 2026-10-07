package br.com.washii.api.repository;

import br.com.washii.api.model.Cliente;
import br.com.washii.api.model.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VeiculoRepository extends JpaRepository<Veiculo, UUID> {
    List<Veiculo> findByCliente(Cliente cliente);
    Boolean existsByIdAndCliente(UUID veiculoId, Cliente cliente);
}
