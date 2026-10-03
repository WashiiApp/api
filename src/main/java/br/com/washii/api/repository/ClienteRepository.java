package br.com.washii.api.repository;

import br.com.washii.api.model.Cliente;
import br.com.washii.api.model.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface ClienteRepository extends JpaRepository<Cliente, UUID> {
}
