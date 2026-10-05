package br.com.washii.api.repository;

import br.com.washii.api.model.Cliente;
import br.com.washii.api.model.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VeiculoRepository extends JpaRepository<Veiculo, UUID> {

    /*

    - Coloquei isso para testar, se quiser pode apagar
        deixei assim porque eu achei que ia ser melhor do que receber
        a classe do cliente inteiro para verificar

    List<Veiculo> findByCliente_Id(UUID clienteId);

    */

    List<Veiculo> findByCliente(Cliente cliente);
    Boolean existsByIdAndCliente(UUID veiculoId, Cliente cliente);
}
