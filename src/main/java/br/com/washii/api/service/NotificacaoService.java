package br.com.washii.api.service;

import br.com.washii.api.dto.response.NotificacaoResponse;
import br.com.washii.api.model.Agendamento;
import br.com.washii.api.model.Notificacao;
import br.com.washii.api.model.TipoUsuario;
import br.com.washii.api.repository.AgendamentoRepository;
import br.com.washii.api.repository.NotificacaoRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final AgendamentoRepository agendamentoRepository;

    @Transactional
    public void registrarNotificacao(UUID agendamentoId, TipoUsuario tipoUsuario,
                                     String titulo, String mensagem) {
        Agendamento agendamentoRef = agendamentoRepository.getReferenceById(agendamentoId);

        Notificacao notificacao = new Notificacao();
        notificacao.setAgendamento(agendamentoRef);
        notificacao.setTipoUsuario(tipoUsuario);
        notificacao.setTitulo(titulo);
        notificacao.setMensagem(mensagem);
        notificacao.setData(LocalDate.now());
        notificacao.setHora(LocalTime.now());
        notificacao.setLida(false);

        notificacaoRepository.save(notificacao);
    }

    @Transactional(readOnly = true)
    public List<NotificacaoResponse> listarRecebidasPorCliente(UUID clienteId) {
        return notificacaoRepository
                .buscarPorCliente(clienteId, TipoUsuario.LAVA_JATO)
                .stream()
                .map(NotificacaoResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotificacaoResponse> listarRecebidasPorLavaJato(UUID lavaJatoId) {
        return notificacaoRepository
                .buscarPorLavaJato(lavaJatoId, TipoUsuario.CLIENTE)
                .stream()
                .map(NotificacaoResponse::fromEntity)
                .toList();
    }

    @Transactional
    public NotificacaoResponse marcarComoLida(UUID id) {
        Notificacao notificacao = notificacaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Notificação não encontrada para o ID: " + id
                ));

        if (!notificacao.isLida()) {
            notificacao.setLida(true);
            notificacao = notificacaoRepository.save(notificacao);
        }

        return NotificacaoResponse.fromEntity(notificacao);
    }
}