package br.com.washii.api.service;

import br.com.washii.api.controller.dto.request.AtualizarClienteRequest;
import br.com.washii.api.controller.dto.response.ClienteResponse;
import br.com.washii.api.exception.ResourceNotFoundException;
import br.com.washii.api.model.Cliente;
import br.com.washii.api.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

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
}
