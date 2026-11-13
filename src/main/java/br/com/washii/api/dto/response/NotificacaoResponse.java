package br.com.washii.api.dto.response;

import br.com.washii.api.model.Notificacao;
import br.com.washii.api.model.TipoUsuario;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;

public record NotificacaoResponse(
        UUID id,
        UUID agendamentoId,
        String titulo,
        String mensagem,
        LocalDate data,
        LocalTime hora,
        boolean lida,
        TipoUsuario tipoUsuario,
        OffsetDateTime createdAt
) {
    public static NotificacaoResponse fromEntity(Notificacao notificacao) {
        UUID agendamentoId = notificacao.getAgendamento() != null
                ? notificacao.getAgendamento().getId()
                : null;

        return new NotificacaoResponse(
                notificacao.getId(),
                agendamentoId,
                notificacao.getTitulo(),
                notificacao.getMensagem(),
                notificacao.getData(),
                notificacao.getHora(),
                notificacao.isLida(),
                notificacao.getTipoUsuario(),
                notificacao.getCreatedAt()
        );
    }
}