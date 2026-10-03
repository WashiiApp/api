package br.com.washii.api.controller;

import br.com.washii.api.controller.dto.VeiculoDTO;
import br.com.washii.api.controller.dto.request.AtualizarClienteRequest;
import br.com.washii.api.controller.dto.response.ClienteResponse;
import br.com.washii.api.model.Cliente;
import br.com.washii.api.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> buscarPorId(
            @PathVariable UUID id
    ) {
        ClienteResponse response = clienteService.buscarPorId(id);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(
            @PathVariable UUID id,
            @RequestBody AtualizarClienteRequest request
    ) {
        clienteService.atualizar(id, request);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{clienteId}/veiculos")
    public ResponseEntity<Void> adicionarVeiculo(
            @PathVariable UUID clienteId,
            @RequestBody VeiculoDTO request) {

        clienteService.adicionarVeiculo(clienteId, request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{clienteId}/veiculos")
    public ResponseEntity<List<VeiculoDTO>> listarVeiculos(@PathVariable UUID clienteId) {
        return ResponseEntity.ok(clienteService.listarVeiculos(clienteId));
    }

    @DeleteMapping("/{clienteId}/veiculos/{veiculoId}")
    public ResponseEntity<Void> removerVeiculo(
            @PathVariable UUID clienteId,
            @PathVariable UUID veiculoId) {

        clienteService.desativarVeiculo(veiculoId);
        return ResponseEntity.noContent().build();
    }
}