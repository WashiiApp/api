package br.com.washii.api.service;

import br.com.washii.api.dto.request.AgendamentoRequest;
import br.com.washii.api.dto.response.HorariosDisponiveisResponse;
import br.com.washii.api.exception.BusinessException;
import br.com.washii.api.exception.ResourceNotFoundException;
import br.com.washii.api.exception.ValidationException;
import br.com.washii.api.model.*;
import br.com.washii.api.repository.*;
import br.com.washii.api.repository.specs.AgendamentoSpecs;
import br.com.washii.api.service.cache.ExpedienteRedisService;
import br.com.washii.api.service.cache.HorarioDisponivelRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.resilience.annotation.Retryable;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
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
    private final LavaJatoService lavaJatoService;
    // cache
    private final ExpedienteRedisService expedienteRedisService;
    private final HorarioDisponivelRedisService horarioDisponivelCache;

    @Transactional
    public Agendamento criar(UUID clienteId, AgendamentoRequest request) {

        clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado."));

        LavaJato lavaJato = lavaJatoRepository.findById(request.lavaJatoId())
                .orElseThrow(() -> new ResourceNotFoundException("Lava-jato não encontrado."));

        Veiculo veiculo = veiculoRepository.findById(request.veiculoId())
                .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado."));

        UUID cliente = veiculo.getCliente().getId();

        if (cliente == null || !cliente.equals(clienteId)) {
            throw new ValidationException("O veículo informado não pertence ao cliente autenticado.");
        }

        if (request.data().atTime(request.hora()).isBefore(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")))) {
            throw new ValidationException("A data e hora do agendamento devem ser futuras.");
        }

        if (request.servicoIds() == null || request.servicoIds().isEmpty()) {
            throw new ValidationException("Informe pelo menos um serviço.");
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
            if (!servico.isAtivo()) {
                throw new ValidationException("O serviço informado está inativo.");
            }

            if (servico.getLavaJato() == null) {
                throw new ValidationException("O serviço não possui um lava-jato associado.");
            }

            if (!Objects.equals(servico.getLavaJato().getId(), lavaJato.getId())) {
                throw new ValidationException("O serviço não pertence ao lava-jato informado.");
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

        HorariosDisponiveisResponse horariosDisponiveis = lavaJatoService.buscarHorariosDisponiveis(
                lavaJato.getId(),
                request.data(),
                request.servicoIds(),
                veiculo.getCategoriaVeiculo().getId()
        );

        // Verifica se o horário exato escolhido pelo cliente consta na lista gerada/cacheada
        if (!horariosDisponiveis.horariosDisponiveis().contains(request.hora())) {
            throw new BusinessException("O horário solicitado não está disponível ou excede a capacidade do estabelecimento.");
        }

        // Cria e salva o agendamento
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

        horarioDisponivelCache.invalidarCache(lavaJato.getId(), request.data());

        return salvo;
    }
    // Repete a operação se outra transação alterou o mesmo agendamento (conflito de versão).
    // Cada tentativa abre uma transação NOVA e relê o agendamento já atualizado, então a
    // regra de negócio é reavaliada com o estado real (ex.: "já concluído, não pode cancelar").
    // includes: só o conflito de versão é repetido; erros de negócio/validação NÃO são repetidos.
    @Retryable(
            includes = OptimisticLockingFailureException.class,
            maxRetries = 2,   // 1 tentativa + até 2 repetições = 3 no total
            delay = 50,       // ms de espera antes de repetir
            jitter = 25       // variação aleatória (ms), para duas requisições não repetirem juntas
    )
    @Transactional
    public void cancelarPeloCliente(UUID agendamentoId, UUID clienteId) {
        Agendamento agendamento = buscarAgendamentoOuLancar(agendamentoId);

        UUID id = agendamento.getVeiculo().getCliente().getId();

        if (id == null || !id.equals(clienteId)) {
            throw new ValidationException("O agendamento não pertence ao cliente autenticado.");
        }

        if (agendamento.getStatusAgendamento() == StatusAgendamento.CONCLUIDO) {
            throw new BusinessException("Agendamentos concluídos não podem ser cancelados.");
        }

        if (agendamento.getStatusAgendamento() != StatusAgendamento.AGENDADO) {
            throw new BusinessException("Somente agendamentos ainda agendados podem ser cancelados pelo cliente.");
        }
        agendamento.setStatusAgendamento(StatusAgendamento.CANCELADO);

        UUID lavaJatoId = agendamento.getAgendamentoServicos()
                .getFirst()
                .getServico()
                .getLavaJato()
                .getId();

        horarioDisponivelCache.invalidarCache(lavaJatoId, agendamento.getData());
    }

    @Transactional(readOnly = true)
    public Agendamento buscarAgendamento(UUID usuarioId, UUID agendamentoId) {
        // Busca o agendamento ou lança exceção se não existir
        Agendamento agendamento = buscarAgendamentoOuLancar(agendamentoId);

        // Valida se o usuario informado (Cliente ou Lava Jato) realmente tem permissão para ver este agendamento
        boolean pertenceAoUsuario = validarSeAgendamentoPertenceAoUsuario(agendamento, usuarioId);

        if (!pertenceAoUsuario) {
            throw new IllegalArgumentException("O agendamento informado não pertence ao usuário especificado.");
        }

        return agendamento;
    }


    @Retryable(
            includes = OptimisticLockingFailureException.class,
            maxRetries = 2,
            delay = 50,
            jitter = 25
    )
    @Transactional
    public void atualizarStatus(UUID agendamentoId, UUID lavaJatoId, StatusAgendamento novoStatus) {
        Agendamento agendamento = buscarAgendamentoOuLancar(agendamentoId);
        boolean pertence = agendamentoServicoRepository.findByAgendamento(agendamento).stream()
                .anyMatch(as -> {

                    UUID idLavaJato = as.getServico().getLavaJato().getId();

                    return idLavaJato != null && idLavaJato.equals(lavaJatoId);
                });
        if (!pertence) throw new ValidationException("O agendamento não pertence ao lava-jato autenticado.");
        if (novoStatus == StatusAgendamento.AGENDADO) {
            throw new ValidationException("O status AGENDADO é atribuído somente na criação.");
        }
        boolean transicaoValida = agendamento.getStatusAgendamento() == StatusAgendamento.AGENDADO
                && (novoStatus == StatusAgendamento.CONCLUIDO || novoStatus == StatusAgendamento.CANCELADO);
        if (!transicaoValida) throw new BusinessException("Transição de status não permitida.");
        agendamento.setStatusAgendamento(novoStatus);

        horarioDisponivelCache.invalidarCache(lavaJatoId, agendamento.getData());
    }

    @Transactional(readOnly = true)
    public List<Agendamento> filtrar(
            UUID usuarioId,
            String placaVeiculo,
            StatusAgendamento status,
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            UUID categoriaVeiculoId
    ) {
        Specification<Agendamento> specs = Specification
                .where(AgendamentoSpecs.idUsuarioEquals(usuarioId))
                .and(AgendamentoSpecs.placaVeiculoEqual(placaVeiculo))
                .and(AgendamentoSpecs.statusEquals(status))
                .and(AgendamentoSpecs.categoriaVeiculoIdEquals(categoriaVeiculoId))
                .and(AgendamentoSpecs.dataBetween(dataInicio, dataFim));

        return agendamentoRepository.findAll(specs);
    }

    private Agendamento buscarAgendamentoOuLancar(UUID id) {
        return agendamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado."));
    }

    // Método auxiliar privado para reutilizar a validação de vínculo (Cliente ou Lava Jato)
    private boolean validarSeAgendamentoPertenceAoUsuario(Agendamento agendamento, UUID usuarioId) {

        // Verifica se é o Cliente dono do veículo do agendamento
        boolean pertenceAoCliente = Optional.ofNullable(agendamento.getVeiculo())
                .map(Veiculo::getCliente)
                .map(Cliente::getId)
                .map(id -> id.equals(usuarioId))
                .orElse(false);

        if (pertenceAoCliente) {
            return true;
        }

        // Verifica se é um dos Lava Jatos prestadores de serviço do agendamento
        return Optional.ofNullable(agendamento.getAgendamentoServicos())
                .orElse(Collections.emptyList())
                .stream()
                .anyMatch(as -> Optional.ofNullable(as.getServico())
                        .map(Servico::getLavaJato)
                        .map(LavaJato::getId)
                        .map(id -> id.equals(usuarioId))
                        .orElse(false));
    }
}
