package br.com.washii.api.dto.response;

import br.com.washii.api.model.Agendamento;
import br.com.washii.api.model.StatusAgendamento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record AgendamentoCriadoResponse(UUID id, UUID veiculoId, LocalDate data,
                                        LocalTime hora, BigDecimal precoTotal,
                                        LocalTime duracaoTotal,
                                        StatusAgendamento status) {
    public static AgendamentoCriadoResponse from(Agendamento agendamento) {
        return new AgendamentoCriadoResponse(agendamento.getId(), agendamento.getVeiculo().getId(),
                agendamento.getData(), agendamento.getHora(), agendamento.getPrecoTotal(),
                agendamento.getDuracaoTotal(), agendamento.getStatusAgendamento());
    }
}
