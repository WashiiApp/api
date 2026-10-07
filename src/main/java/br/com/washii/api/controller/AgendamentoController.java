package br.com.washii.api.controller;

import br.com.washii.api.dto.request.AgendamentoRequest;
import br.com.washii.api.dto.request.AtualizarStatusAgendamentoRequest;
import br.com.washii.api.model.Agendamento;
import br.com.washii.api.service.AgendamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    public record AgendamentoCriadoResponse(UUID id, UUID veiculoId, java.time.LocalDate data,
                                             java.time.LocalTime hora, java.math.BigDecimal precoTotal,
                                             java.time.LocalTime duracaoTotal,
                                             br.com.washii.api.model.StatusAgendamento status) {
        static AgendamentoCriadoResponse from(Agendamento agendamento) {
            return new AgendamentoCriadoResponse(agendamento.getId(), agendamento.getVeiculo().getId(),
                    agendamento.getData(), agendamento.getHora(), agendamento.getPrecoTotal(),
                    agendamento.getDuracaoTotal(), agendamento.getStatusAgendamento());
        }
    }
}
