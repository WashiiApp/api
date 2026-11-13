package br.com.washii.api.events;

import br.com.washii.api.model.StatusAgendamento;

import java.util.UUID;

public record AgendamentoStatusAlteradoEvent(
        UUID agendamentoId,
        UUID clienteId,
        UUID lavaJatoId,
        String nomeLavaJato,
        StatusAgendamento statusAnterior,
        StatusAgendamento novoStatus) {

}
