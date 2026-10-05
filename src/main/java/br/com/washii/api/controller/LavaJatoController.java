package br.com.washii.api.controller;

import br.com.washii.api.dto.response.LavaJatoResponse;
import br.com.washii.api.service.LavaJatoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // Serviços


    // Operacionais e Consultas

}
