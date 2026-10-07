package br.com.washii.api.controller;

import br.com.washii.api.dto.request.AtualizacaoLavaJatoRequest;
import br.com.washii.api.dto.request.CadastroServicoRequest;
import br.com.washii.api.dto.response.LavaJatoResponse;
import br.com.washii.api.dto.request.ExpedienteRequest;
import br.com.washii.api.dto.response.ExpedienteResponse;
import br.com.washii.api.dto.response.ServicoResponse;
import br.com.washii.api.model.Servico;
import br.com.washii.api.repository.ServicoRepository;
import br.com.washii.api.service.LavaJatoService;
import br.com.washii.api.service.ServicoService;
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
    private final ServicoService servicoService;

    // Gestão cadastral do sistema
    @GetMapping("/{id}")
    public ResponseEntity<LavaJatoResponse> buscar(@PathVariable UUID id){
        LavaJatoResponse lavaJatoResponse = lavaJatoService.buscarPorId(id);

        return ResponseEntity.ok(lavaJatoResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(
            @PathVariable UUID id,
            @RequestBody AtualizacaoLavaJatoRequest request)
    {
        lavaJatoService.atualizar(id, request);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id){
        lavaJatoService.deletar(id);

        return ResponseEntity.noContent().build();
    }

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
    @PostMapping("/{lavaJatoId}/servicos")
    public ResponseEntity<Void> salvarServico(
            @PathVariable UUID lavaJatoId,
            @RequestBody CadastroServicoRequest request)
    {
        servicoService.salvarServico(lavaJatoId, request);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{lavaJatoId}/servicos")
    public ResponseEntity<List<ServicoResponse>> buscarServicos(@PathVariable UUID lavaJatoId){
        List<Servico> servicos = servicoService.buscarServicosPorLavaJato(lavaJatoId);

        List<ServicoResponse> servicosDTO = servicos.stream()
                .map(ServicoResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(servicosDTO);
    }

    @GetMapping("/{lavaJatoId}/servicos/{servicoId}")
    public ResponseEntity<ServicoResponse> buscarPorId(
            @PathVariable UUID lavaJatoId,
            @PathVariable UUID servicoId)
    {
        Servico servico = servicoService.buscarPorId(lavaJatoId, servicoId);

        return ResponseEntity.ok(ServicoResponse.fromEntity(servico));
    }

    @PutMapping("/{lavaJatoId}/servicos/{servicoId}")
    public ResponseEntity<ServicoResponse> buscarPorId(
            @PathVariable UUID lavaJatoId,
            @PathVariable UUID servicoId,
            @RequestBody CadastroServicoRequest request)
    {
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{lavaJatoId}/servicos/{servicoId}")
    public ResponseEntity<Void> deletar(
            @PathVariable UUID lavaJatoId,
            @PathVariable UUID servicoId
    ){
        return ResponseEntity.noContent().build();
    }

//    @GetMapping("/{lavaJatoId}/servicos/{servicoId}/precos/{precoId}")
//    public ResponseEntity<CategoriaVeiculoServicoResponse> buscarPorCustomizacoes(
//            @PathVariable UUID lavaJatoId,
//            @PathVariable UUID servicoId,
//            @PathVariable UUID precoId
//    ){
//
//    }
//
//    @PostMapping("/{lavaJatoId}/servicos/{servicoId}/precos")
//    public ResponseEntity<Void> customizarServico(
//            @PathVariable UUID lavaJatoId,
//            @PathVariable UUID servicoId,
//            @RequestBody CategoriaVeiculoServicoRequest request
//    ){
//
//    }
//
//    @PutMapping("/{lavaJatoId}/servicos/{servicoId}/precos/{precoId}")
//    public ResponseEntity<Void> customizarServico(
//            @PathVariable UUID lavaJatoId,
//            @PathVariable UUID servicoId,
//            @PathVariable UUID precoId,
//            @RequestBody CategoriaVeiculoServicoRequest request
//    ){
//
//    }
//
//    @DeleteMapping("/{lavaJatoId}/servicos/{servicoId}/precos/{precoId}")
//    public ResponseEntity<CategoriaVeiculoServicoResponse> buscarPorCustomizacoes(
//            @PathVariable UUID lavaJatoId,
//            @PathVariable UUID servicoId,
//            @PathVariable UUID precoId
//    ){
//
//    }



    // Operacionais e Consultas

}
