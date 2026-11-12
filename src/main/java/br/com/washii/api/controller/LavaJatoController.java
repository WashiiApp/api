package br.com.washii.api.controller;

import br.com.washii.api.dto.request.*;
import br.com.washii.api.dto.response.*;
import br.com.washii.api.model.CategoriaVeiculoServico;
import br.com.washii.api.model.Servico;
import br.com.washii.api.service.LavaJatoService;
import br.com.washii.api.service.ServicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import br.com.washii.api.dto.response.HorariosDisponiveisResponse;
import java.time.LocalDate;

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

    @PatchMapping(value = "/{id}/foto-perfil", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> atualizarFotoPerfil(
            @PathVariable UUID id,
            @RequestParam("arquivo") MultipartFile arquivo
    ) {
        lavaJatoService.atualizarFotoPerfil(id, arquivo);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id){
        lavaJatoService.deletar(id);

        return ResponseEntity.noContent().build();
    }

    // Expediente
    @PostMapping("/{id}/expedientes")
    public ResponseEntity<List<ExpedienteResponse>> cadastrarExpediente(
            @PathVariable UUID id,
            @Valid @RequestBody List<@Valid ExpedienteRequest> expediente)
    {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(lavaJatoService.cadastrarExpediente(id, expediente));
    }

    @GetMapping("/{id}/expedientes")
    public ResponseEntity<List<ExpedienteResponse>> buscarExpediente(@PathVariable UUID id)
    {
        return ResponseEntity.ok(lavaJatoService.buscarExpediente(id));
    }

    @PutMapping("/{id}/expedientes")
    public ResponseEntity<List<ExpedienteResponse>> atualizarExpediente(
            @PathVariable UUID id,
            @Valid @RequestBody List<@Valid ExpedienteRequest> expediente)
    {
        return ResponseEntity.ok(lavaJatoService.atualizarExpediente(id, expediente));
    }

    @DeleteMapping("/{id}/expedientes/{expedienteId}")
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
        List<ServicoResponse> response = servicoService.buscarServicosPorLavaJato(lavaJatoId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{lavaJatoId}/servicos/{servicoId}")
    public ResponseEntity<ServicoResponse> buscarServicoPorId(
            @PathVariable UUID lavaJatoId,
            @PathVariable UUID servicoId)
    {
        Servico servico = servicoService.buscarPorId(lavaJatoId, servicoId);

        return ResponseEntity.ok(ServicoResponse.fromEntity(servico));
    }

    @PutMapping("/{lavaJatoId}/servicos/{servicoId}")
    public ResponseEntity<Void> atualizarServico(
            @PathVariable UUID lavaJatoId,
            @PathVariable UUID servicoId,
            @RequestBody CadastroServicoRequest request)
    {
        servicoService.atualizarServico(lavaJatoId, servicoId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{lavaJatoId}/servicos/{servicoId}")
    public ResponseEntity<Void> deletarServico(
            @PathVariable UUID lavaJatoId,
            @PathVariable UUID servicoId
    ){
        servicoService.deletarServico(lavaJatoId, servicoId);
        return ResponseEntity.noContent().build();
    }

    //Avaliação de Serviços

    @GetMapping("{lavaJatoId}/servicos/{servicoId}/avaliacoes")
    public ResponseEntity<List<AvaliacaoResponse>> buscarAvaliacoes(
            @PathVariable UUID lavaJatoId,
            @PathVariable UUID servicoId
    ){
        List<AvaliacaoResponse> avaliacoesResponse = servicoService
                .buscarAvaliacoes(lavaJatoId, servicoId);

        return ResponseEntity.ok(avaliacoesResponse);
    }

    @PostMapping("{lavaJatoId}/servicos/{servicoId}/avaliacoes")
    public ResponseEntity<Void> salvarAvaliacao(
            @PathVariable UUID lavaJatoId,
            @PathVariable UUID servicoId,
            @RequestBody AvaliacaoRequest request
    ){
        servicoService.salvarAvaliacao(lavaJatoId, servicoId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{lavaJatoId}/servicos/{servicoId}/avaliacoes/{avaliacaoId}")
    public ResponseEntity<Void> apagarAvaliacao(
            @PathVariable UUID lavaJatoId,
            @PathVariable UUID servicoId,
            @PathVariable UUID avaliacaoId
    ){
        servicoService.apagarAvaliacao(lavaJatoId, servicoId, avaliacaoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{lavaJatoId}/servicos/{servicoId}/precos")
    public ResponseEntity<List<CategoriaVeiculoServicoResponse>> buscarCustomizacoes(
        @PathVariable UUID lavaJatoId,
        @PathVariable UUID servicoId
    ){
        List<CategoriaVeiculoServico> categoriaVeiculoServicos = servicoService.buscarCustomizacoesDeServicos(lavaJatoId, servicoId);

        List<CategoriaVeiculoServicoResponse> response = categoriaVeiculoServicos.stream()
                .map(CategoriaVeiculoServicoResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{lavaJatoId}/servicos/{servicoId}/precos")
    public ResponseEntity<Void> atualizarCustomizacaoDeServico(
            @PathVariable UUID lavaJatoId,
            @PathVariable UUID servicoId,
            @RequestBody List<CategoriaVeiculoServicoRequest> request
    ){
        servicoService.customizarServicoPorCategoriaServico(lavaJatoId, servicoId, request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{lavaJatoId}/servicos/{servicoId}/precos/{precoId}")
    public ResponseEntity<Void> atualizarCustomizacaoDeServico(
            @PathVariable UUID lavaJatoId,
            @PathVariable UUID servicoId,
            @PathVariable UUID precoId,
            @RequestBody CategoriaVeiculoServicoRequest request
    ){
        servicoService.atualizarCustomizacao(lavaJatoId, servicoId, precoId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{lavaJatoId}/servicos/{servicoId}/precos/{precoId}")
    public ResponseEntity<CategoriaVeiculoServicoResponse> buscarPorCustomizacoes(
            @PathVariable UUID lavaJatoId,
            @PathVariable UUID servicoId,
            @PathVariable UUID precoId
    ){
        servicoService.deletarCustomizacao(lavaJatoId, servicoId, precoId);
        return ResponseEntity.noContent().build();
    }

    // Operacionais e Consultas
    @GetMapping("/{id}/horarios-disponiveis")
    public ResponseEntity<HorariosDisponiveisResponse> buscarHorariosDisponiveis(
            @PathVariable UUID id,
            @RequestParam(required = false) LocalDate data,
            @RequestParam List<UUID> servicoIds,
            @RequestParam UUID categoriaVeiculoId
    ) {
        HorariosDisponiveisResponse response =
                lavaJatoService.buscarHorariosDisponiveis(
                        id,
                        data,
                        servicoIds,
                        categoriaVeiculoId
                );

        return ResponseEntity.ok(response);
    }
}
