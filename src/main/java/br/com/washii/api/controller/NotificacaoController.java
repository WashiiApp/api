package br.com.washii.api.controller;

import br.com.washii.api.dto.response.NotificacaoResponse;
import br.com.washii.api.service.NotificacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notificacoes")
@RequiredArgsConstructor
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<NotificacaoResponse>> listarPorCliente(@PathVariable UUID clienteId) {
        return ResponseEntity.ok(notificacaoService.listarRecebidasPorCliente(clienteId));
    }

    @GetMapping("/lava-jato/{lavaJatoId}")
    public ResponseEntity<List<NotificacaoResponse>> listarPorLavaJato(@PathVariable UUID lavaJatoId) {
        return ResponseEntity.ok(notificacaoService.listarRecebidasPorLavaJato(lavaJatoId));
    }

    @PatchMapping("/{id}/ler")
    public ResponseEntity<NotificacaoResponse> marcarComoLida(@PathVariable UUID id) {
        return ResponseEntity.ok(notificacaoService.marcarComoLida(id));
    }
}