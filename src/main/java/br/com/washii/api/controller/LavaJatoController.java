package br.com.washii.api.controller;

import br.com.washii.api.dto.response.LavaJatoResponse;
import br.com.washii.api.dto.request.ExpedienteRequest;
import br.com.washii.api.dto.response.ExpedienteResponse;
import br.com.washii.api.service.LavaJatoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("lavajatos")
@RequiredArgsConstructor
public class LavaJatoController {

    private final LavaJatoService lavaJatoService;

    // Gestão cadastral do sistema
    @GetMapping("/{id}")
    public ResponseEntity<LavaJatoResponse> buscar(@PathVariable UUID id){
        LavaJatoResponse lavaJatoResponse = lavaJatoService.buscarPorId(id);

        return ResponseEntity.ok(lavaJatoResponse);
    }

    @PutMapping
    public void atualizar(){}

    @DeleteMapping
    public void excluir(){}

    // Expediente
    @PostMapping("/{id}/expediente")
    public ResponseEntity<List<ExpedienteResponse>> cadastrarExpediente(
            @PathVariable UUID id,
            @Valid @RequestBody List<@Valid ExpedienteRequest> expediente)
    {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(lavaJatoService.cadastrarExpediente(id, expediente));
    }

    @GetMapping("/{id}/expediente")
    public ResponseEntity<List<ExpedienteResponse>> buscarExpediente(@PathVariable UUID id)
    {
        return ResponseEntity.ok(lavaJatoService.buscarExpediente(id));
    }

    @PutMapping("/{id}/expediente")
    public ResponseEntity<List<ExpedienteResponse>> atualizarExpediente(
            @PathVariable UUID id,
            @Valid @RequestBody List<@Valid ExpedienteRequest> expediente)
    {
        return ResponseEntity.ok(lavaJatoService.atualizarExpediente(id, expediente));
    }

    @DeleteMapping("/{id}/expediente/{expedienteId}")
    public ResponseEntity<Void> removerExpediente(
            @PathVariable UUID id,
            @PathVariable UUID expedienteId)
    {

        lavaJatoService.removerExpediente(id, expedienteId);
        return ResponseEntity.noContent().build();
    }

    // Serviços


    // Operacionais e Consultas

}
