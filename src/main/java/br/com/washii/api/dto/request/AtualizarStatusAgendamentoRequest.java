package br.com.washii.api.dto.request;

import br.com.washii.api.model.StatusAgendamento;
import jakarta.validation.constraints.NotNull;

public record AtualizarStatusAgendamentoRequest(@NotNull StatusAgendamento status) {
}
