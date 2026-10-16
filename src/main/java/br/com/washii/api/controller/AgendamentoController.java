package br.com.washii.api.controller;

import br.com.washii.api.dto.request.AgendamentoRequest;
import br.com.washii.api.dto.request.AtualizarStatusAgendamentoRequest;
import br.com.washii.api.dto.response.AgendamentoCriadoResponse;
import br.com.washii.api.model.Agendamento;
import br.com.washii.api.model.StatusAgendamento;
import br.com.washii.api.service.AgendamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/agendamentos")
@RequiredArgsConstructor
public class AgendamentoController {
    private final AgendamentoService agendamentoService;

    @PostMapping
    public ResponseEntity<AgendamentoCriadoResponse> criar(@Valid @RequestBody AgendamentoRequest request,
                                                           Authentication authentication) {
        Agendamento agendamento = agendamentoService.criar(usuarioId(authentication), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(AgendamentoCriadoResponse.from(agendamento));
    }

    @PatchMapping("/{agendamentoId}/cancelar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelar(@PathVariable UUID agendamentoId, Authentication authentication) {
        agendamentoService.cancelarPeloCliente(agendamentoId, usuarioId(authentication));
    }

    @PatchMapping("/{agendamentoId}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void atualizarStatus(@PathVariable UUID agendamentoId,
                                @Valid @RequestBody AtualizarStatusAgendamentoRequest request,
                                Authentication authentication) {
        agendamentoService.atualizarStatus(agendamentoId, usuarioId(authentication), request.status());
    }

    private UUID usuarioId(Authentication authentication) {
        return UUID.fromString(authentication.getName());
    }

    @GetMapping("/{usuarioId}/{agendamentoId}")
    public ResponseEntity<AgendamentoCriadoResponse> buscarAgendamento(
            @PathVariable UUID usuarioId,
            @PathVariable UUID agendamentoId
    ){
        Agendamento agendamento = agendamentoService.buscar(usuarioId, agendamentoId);

        return ResponseEntity.ok(AgendamentoCriadoResponse.from(agendamento));
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<List<AgendamentoCriadoResponse>> filtrar(
            @PathVariable UUID usuarioId,
            @RequestParam(required = false) String placaVeiculo,
            @RequestParam(required = false) StatusAgendamento status,
            @RequestParam(required = false) LocalDateTime dataInicio,
            @RequestParam(required = false) LocalDateTime dataFim,
            @RequestParam(required = false) UUID categoriaVeiculoId

    ){

        List<Agendamento> agendamentos =  agendamentoService
                .filtrar(usuarioId, placaVeiculo, status, dataInicio, dataFim, categoriaVeiculoId);

        List<AgendamentoCriadoResponse> response = agendamentos.stream()
                .map(AgendamentoCriadoResponse::from)
                .toList();

        return ResponseEntity.ok(response);
    }
}
