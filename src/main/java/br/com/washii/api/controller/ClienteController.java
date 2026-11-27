package br.com.washii.api.controller;

import br.com.washii.api.dto.VeiculoDTO;
import br.com.washii.api.dto.request.AtualizarClienteRequest;
import br.com.washii.api.dto.response.ClienteResponse;
import br.com.washii.api.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            @Valid @RequestBody AtualizarClienteRequest request
    ) {
        clienteService.atualizar(id, request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{id}/foto-perfil", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> atualizarFotoPerfil(
            @PathVariable UUID id,
            @RequestParam("arquivo") MultipartFile arquivo
    ) {
        clienteService.atualizarFotoPerfil(id, arquivo);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{clienteId}/veiculos")
    public ResponseEntity<Void> adicionarVeiculo(
            @PathVariable UUID clienteId,
            @Valid @RequestBody VeiculoDTO request,
            Authentication authentication) {

        clienteService.adicionarVeiculo(clienteId, usuarioId(authentication), request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{clienteId}/veiculos")
    public ResponseEntity<List<VeiculoDTO>> listarVeiculos(@PathVariable UUID clienteId,
                                                            Authentication authentication) {
        return ResponseEntity.ok(clienteService.listarVeiculos(clienteId, usuarioId(authentication)));
    }

    @GetMapping("/veiculos/placa/{placa}")
    public ResponseEntity<VeiculoDTO> buscarVeiculoPorPlaca(@PathVariable String placa,
                                                             Authentication authentication) {
        return ResponseEntity.ok(clienteService.buscarVeiculoPorPlaca(usuarioId(authentication), placa));
    }

    @PutMapping("/{clienteId}/veiculos/{veiculoId}")
    public ResponseEntity<Void> atualizarVeiculo(
            @PathVariable UUID clienteId,
            @PathVariable UUID veiculoId,
            @Valid @RequestBody VeiculoDTO request,
            Authentication authentication) {

        clienteService.atualizarVeiculo(clienteId, usuarioId(authentication), veiculoId, request);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{clienteId}/veiculos/{veiculoId}")
    public ResponseEntity<Void> removerVeiculo(
            @PathVariable UUID clienteId,
            @PathVariable UUID veiculoId,
            Authentication authentication) {

        clienteService.desativarVeiculo(clienteId, usuarioId(authentication), veiculoId);
        return ResponseEntity.noContent().build();
    }

    private UUID usuarioId(Authentication authentication) {
        return UUID.fromString(authentication.getName());
    }
}
