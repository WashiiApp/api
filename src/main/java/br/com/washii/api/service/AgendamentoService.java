package br.com.washii.api.service;

import br.com.washii.api.dto.request.AgendamentoRequest;
import br.com.washii.api.exception.BusinessException;
import br.com.washii.api.exception.ResourceNotFoundException;
import br.com.washii.api.exception.ValidationException;
import br.com.washii.api.model.*;
import br.com.washii.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AgendamentoService {
    private final AgendamentoRepository agendamentoRepository;
    private final AgendamentoServicoRepository agendamentoServicoRepository;
    private final ClienteRepository clienteRepository;
    private final LavaJatoRepository lavaJatoRepository;
    private final VeiculoRepository veiculoRepository;
    private final ServicoRepository servicoRepository;
    private final CategoriaVeiculoServicoRepository categoriaVeiculoServicoRepository;

    @Transactional
    public Agendamento criar(UUID clienteId, AgendamentoRequest request) {
        clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado."));
        LavaJato lavaJato = lavaJatoRepository.findById(request.lavaJatoId())
                .orElseThrow(() -> new ResourceNotFoundException("Lava-jato não encontrado."));
        Veiculo veiculo = veiculoRepository.findById(request.veiculoId())
                .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado."));
        if (!veiculo.getCliente().getId().equals(clienteId)) {
            throw new ValidationException("O veículo informado não pertence ao cliente autenticado.");
        }

        if (request.data().atTime(request.hora()).isBefore(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")))) {
            throw new ValidationException("A data e hora do agendamento devem ser futuras.");
        }
        if (new HashSet<>(request.servicoIds()).size() != request.servicoIds().size()) {
            throw new ValidationException("Não é permitido informar serviços duplicados.");
        }

        List<Servico> servicos = servicoRepository.findAllById(request.servicoIds());
        if (servicos.size() != request.servicoIds().size()) {
            throw new ResourceNotFoundException("Um ou mais serviços não foram encontrados.");
        }
        BigDecimal total = BigDecimal.ZERO;
        long duracaoMinutos = 0;
        for (Servico servico : servicos) {
            if (!servico.isAtivo() || !servico.getLavaJato().getId().equals(lavaJato.getId())) {
                throw new ValidationException("Todos os serviços devem estar ativos e pertencer ao lava-jato informado.");
            }
            CategoriaVeiculoServico precoTempo = categoriaVeiculoServicoRepository
                    .findByCategoriaVeiculoAndServico(veiculo.getCategoriaVeiculo(), servico)
                    .orElseThrow(() -> new ValidationException("Há serviço indisponível para a categoria deste veículo."));
            total = total.add(precoTempo.getPreco());
            duracaoMinutos = Math.addExact(duracaoMinutos, Duration.between(LocalTime.MIDNIGHT, precoTempo.getDuracao()).toMinutes());
        }
        if (duracaoMinutos < 1 || duracaoMinutos >= 24 * 60) {
            throw new ValidationException("A duração total calculada para o agendamento é inválida.");
        }

        LocalDateTime inicio = request.data().atTime(request.hora());
        LocalDateTime fim = inicio.plusMinutes(duracaoMinutos);
        List<UUID> idsAtivos = agendamentoRepository.findAtivosDoLavaJatoNaData(
                lavaJato.getId(), request.data(), StatusAgendamento.AGENDADO.name());
        List<Agendamento> ativos = agendamentoRepository.findAllById(idsAtivos);
        long simultaneos = ativos.stream().filter(a -> sobrepoe(inicio, fim, a)).count();
        if (simultaneos >= lavaJato.getFluxoSimultaneo()) {
            throw new BusinessException("Não há capacidade disponível para o horário solicitado.");
        }

        Agendamento agendamento = new Agendamento();
        agendamento.setVeiculo(veiculo);
        agendamento.setData(request.data());
        agendamento.setHora(request.hora());
        agendamento.setPrecoTotal(total);
        agendamento.setDuracaoTotal(LocalTime.MIDNIGHT.plusMinutes(duracaoMinutos));
        agendamento.setStatusAgendamento(StatusAgendamento.AGENDADO);
        Agendamento salvo = agendamentoRepository.save(agendamento);
        agendamentoServicoRepository.saveAll(servicos.stream()
                .map(servico -> new AgendamentoServico(null, salvo, servico, null)).toList());
        return salvo;
    }

    private boolean sobrepoe(LocalDateTime inicio, LocalDateTime fim, Agendamento existente) {
        long minutos = agendamentoServicoRepository.findByAgendamento(existente).stream()
                .map(AgendamentoServico::getServico)
                .map(s -> categoriaVeiculoServicoRepository.findByCategoriaVeiculoAndServico(
                        existente.getVeiculo().getCategoriaVeiculo(), s).orElse(null))
                .filter(Objects::nonNull)
                .mapToLong(p -> Duration.between(LocalTime.MIDNIGHT, p.getDuracao()).toMinutes()).sum();
        LocalDateTime inicioExistente = existente.getData().atTime(existente.getHora());
        LocalDateTime fimExistente = inicioExistente.plusMinutes(minutos);
        return inicio.isBefore(fimExistente) && inicioExistente.isBefore(fim);
    }

    @Transactional
    public void cancelarPeloCliente(UUID agendamentoId, UUID clienteId) {
        Agendamento agendamento = buscar(agendamentoId);
        if (!agendamento.getVeiculo().getCliente().getId().equals(clienteId)) {
            throw new ValidationException("O agendamento não pertence ao cliente autenticado.");
        }
        if (agendamento.getStatusAgendamento() != StatusAgendamento.AGENDADO) {
            throw new BusinessException("Somente agendamentos ainda agendados podem ser cancelados pelo cliente.");
        }
        agendamento.setStatusAgendamento(StatusAgendamento.CANCELADO);
    }

    @Transactional
    public void atualizarStatus(UUID agendamentoId, UUID lavaJatoId, StatusAgendamento novoStatus) {
        Agendamento agendamento = buscar(agendamentoId);
        boolean pertence = agendamentoServicoRepository.findByAgendamento(agendamento).stream()
                .anyMatch(as -> as.getServico().getLavaJato().getId().equals(lavaJatoId));
        if (!pertence) throw new ValidationException("O agendamento não pertence ao lava-jato autenticado.");
        if (novoStatus == StatusAgendamento.AGENDADO) {
            throw new ValidationException("O status AGENDADO é atribuído somente na criação.");
        }
        boolean transicaoValida = agendamento.getStatusAgendamento() == StatusAgendamento.AGENDADO
                && (novoStatus == StatusAgendamento.CONCLUIDO || novoStatus == StatusAgendamento.CANCELADO);
        if (!transicaoValida) throw new BusinessException("Transição de status não permitida.");
        agendamento.setStatusAgendamento(novoStatus);
    }

    private Agendamento buscar(UUID id) {
        return agendamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado."));
    }
}
