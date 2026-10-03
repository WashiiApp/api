package br.com.washii.api.service;

import br.com.washii.api.controller.dto.VeiculoDTO;
import br.com.washii.api.controller.dto.request.AtualizarClienteRequest;
import br.com.washii.api.controller.dto.response.ClienteResponse;
import br.com.washii.api.exception.ResourceNotFoundException;
import br.com.washii.api.model.CategoriaVeiculo;
import br.com.washii.api.model.Cliente;
import br.com.washii.api.model.Veiculo;
import br.com.washii.api.repository.CategoriaVeiculoRepository;
import br.com.washii.api.repository.ClienteRepository;
import br.com.washii.api.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final VeiculoRepository  veiculoRepository;
    private final CategoriaVeiculoRepository catVeiculoRepository;

    public ClienteResponse buscarPorId(UUID id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado."));

        return ClienteResponse.from(cliente);
    }

    public ClienteResponse atualizar(UUID id, AtualizarClienteRequest clienteAtualizado) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado."));

        cliente.setCidade(clienteAtualizado.cidade());
        cliente.setEstado(clienteAtualizado.estado());
        cliente.setCpf(clienteAtualizado.cpf());
        cliente.setPrimeiroNome(clienteAtualizado.primeiroNome());
        cliente.setSobreNome(clienteAtualizado.sobreNome());

        clienteRepository.save(cliente);

        return ClienteResponse.from(cliente);
    }

    public void adicionarVeiculo(UUID clienteId, VeiculoDTO request) {
        Cliente cliente = clienteRepository.findById(clienteId).orElse(null);
        CategoriaVeiculo categoria = catVeiculoRepository.findByNome(request.categoria()).orElse(null);

        if (cliente == null)
            throw new IllegalArgumentException("Não foi encontrado nenhum Cliente com o id = " + clienteId);

        if (categoria == null)
            throw new IllegalArgumentException("Categoria inválida");

        Veiculo veiculo = new Veiculo();
        veiculo.setCliente(cliente);
        veiculo.setMarca(request.marca());
        veiculo.setModelo(request.modelo());
        veiculo.setCor(request.cor());
        veiculo.setCategoriaVeiculo(categoria);
        veiculo.setAtivo(true);
        veiculo.setPlaca(request.placa());

        veiculoRepository.save(veiculo);
    }

    public List<VeiculoDTO> listarVeiculos(UUID clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId).orElse(null);
        if (cliente == null)
            throw new IllegalArgumentException("Não foi encontrado nenhum Cliente com o id = " + clienteId);

        List<Veiculo> veiculos = veiculoRepository.findByCliente(cliente);

        return veiculos.stream()
                .map(VeiculoDTO::from)
                .toList();
    }

    public void desativarVeiculo(UUID clienteId, UUID veiculoId) {
    }
}