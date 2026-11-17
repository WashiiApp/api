package br.com.washii.api.listener;

import br.com.washii.api.events.AgendamentoCriadoEvent;
import br.com.washii.api.events.AgendamentoStatusAlteradoEvent;
import br.com.washii.api.model.TipoUsuario;
import br.com.washii.api.service.NotificacaoService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class NotificacaoEventListener {

    private static final Logger log = LoggerFactory.getLogger(NotificacaoEventListener.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

    private final NotificacaoService notificacaoService;

    @Async("notificationTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onAgendamentoCriado(AgendamentoCriadoEvent event) {
        try {
            String titulo = "Novo agendamento recebido";
            String mensagem = String.format(
                    "O cliente %s agendou o serviço '%s' para %s.",
                    event.nomeCliente(),
                    event.nomeServico(),
                    event.dataHoraAgendada().format(FORMATTER)
            );

            notificacaoService.registrarNotificacao(
                    event.agendamentoId(),
                    TipoUsuario.CLIENTE,
                    titulo,
                    mensagem
            );
        } catch (Exception ex) {
            log.error("Falha ao processar notificação de novo agendamento [agendamentoId={}]",
                    event.agendamentoId(), ex);
        }
    }

    @Async("notificationTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onAgendamentoStatusAlterado(AgendamentoStatusAlteradoEvent event) {
        try {
            String titulo = "Atualização no seu agendamento";
            String mensagem = String.format(
                    "O estabelecimento %s alterou o status do seu agendamento #%d para: %s.",
                    event.nomeLavaJato(),
                    event.agendamentoId(),
                    event.novoStatus()
            );

            notificacaoService.registrarNotificacao(
                    event.agendamentoId(),
                    TipoUsuario.LAVA_JATO,
                    titulo,
                    mensagem
            );
        } catch (Exception ex) {
            log.error("Falha ao processar notificação de status [agendamentoId={}]",
                    event.agendamentoId(), ex);
        }
    }
}