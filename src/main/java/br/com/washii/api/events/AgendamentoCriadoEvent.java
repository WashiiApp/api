package br.com.washii.api.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record AgendamentoCriadoEvent(
        UUID agendamentoId,
        UUID clienteId,
        String nomeCliente,
        UUID lavaJatoId,
        String nomeServico,
        LocalDateTime dataHoraAgendada
) {}