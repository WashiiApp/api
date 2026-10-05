package br.com.washii.api.service;

import br.com.washii.api.dto.response.LavaJatoResponse;
import br.com.washii.api.dto.request.ExpedienteRequest;
import br.com.washii.api.dto.response.ExpedienteResponse;
import br.com.washii.api.exception.ResourceNotFoundException;
import br.com.washii.api.exception.ValidationException;
import br.com.washii.api.model.Disponibilidade;
import br.com.washii.api.model.DiasSemana;
import br.com.washii.api.model.LavaJato;
import br.com.washii.api.repository.DisponibilidadeRepository;
import br.com.washii.api.repository.DiasSemanaRepository;
import br.com.washii.api.repository.LavaJatoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LavaJatoService {

    private final LavaJatoRepository lavaJatoRepository;
    private final DisponibilidadeRepository disponibilidadeRepository;
    private final DiasSemanaRepository diasSemanaRepository;

    // Gestão cadastral do sistema
    public LavaJatoResponse buscarPorId(UUID id){
        LavaJato lavaJato = lavaJatoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lava jato não encontrado com o id = " + id));

       return LavaJatoResponse.fromEntity(lavaJato);
    }

    // Expediente
    @Transactional
    public List<ExpedienteResponse> cadastrarExpediente(UUID lavaJatoId, List<ExpedienteRequest> expediente) {
        LavaJato lavaJato = buscarLavaJato(lavaJatoId);
        List<Disponibilidade> disponibilidades = expediente.stream()
                .map(item -> criarDisponibilidade(lavaJato, item))
                .toList();

        return disponibilidadeRepository.saveAll(disponibilidades).stream()
                .map(ExpedienteResponse::fromEntity)
                .toList();
    }


    @Transactional(readOnly = true)
    public List<ExpedienteResponse> buscarExpediente(UUID lavaJatoId) {
        buscarLavaJato(lavaJatoId);
        return disponibilidadeRepository.findAllByLavaJato_IdOrderByDiasSemana_Id(lavaJatoId).stream()
                .map(ExpedienteResponse::fromEntity)
                .toList();
    }

    @Transactional
    public List<ExpedienteResponse> atualizarExpediente(UUID lavaJatoId, List<ExpedienteRequest> expediente) {
        LavaJato lavaJato = buscarLavaJato(lavaJatoId);
        List<Disponibilidade> disponibilidades = expediente.stream()
                .map(item -> criarDisponibilidade(lavaJato, item))
                .toList();

        disponibilidadeRepository.deleteAllByLavaJato_Id(lavaJatoId);
        return disponibilidadeRepository.saveAll(disponibilidades).stream()
                .map(ExpedienteResponse::fromEntity)
                .toList();
    }

    @Transactional
    public void removerExpediente(UUID lavaJatoId, UUID expedienteId) {
        Disponibilidade disponibilidade = disponibilidadeRepository.findByIdAndLavaJato_Id(expedienteId, lavaJatoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Expediente não encontrado para o estabelecimento informado."));
        disponibilidadeRepository.delete(disponibilidade);
    }

    private LavaJato buscarLavaJato(UUID id) {
        return lavaJatoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lava-jato não encontrado com o id = " + id));
    }

    private Disponibilidade criarDisponibilidade(LavaJato lavaJato, ExpedienteRequest request) {
        DiasSemana diaSemana = diasSemanaRepository.findById(request.diaSemanaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Dia da semana não encontrado com o id = " + request.diaSemanaId()));

        if (!request.isHorarioValido()) {
            throw new ValidationException("O horário de término deve ser posterior ao horário de início.");
        }

        Disponibilidade disponibilidade = new Disponibilidade();
        disponibilidade.setLavaJato(lavaJato);
        disponibilidade.setDiasSemana(diaSemana);
        disponibilidade.setHrInicio(request.horarioInicio());
        disponibilidade.setHrTermino(request.horarioTermino());
        return disponibilidade;
    }


    // Serviços


    // Operacionais e Consultas
}
