package br.com.washii.api.service;

import br.com.washii.api.dto.request.AtualizacaoLavaJatoRequest;
import br.com.washii.api.dto.response.LavaJatoResponse;
import br.com.washii.api.dto.request.ExpedienteRequest;
import br.com.washii.api.dto.response.ExpedienteResponse;
import br.com.washii.api.exception.ResourceNotFoundException;
import br.com.washii.api.exception.ValidationException;
import br.com.washii.api.model.Disponibilidade;
import br.com.washii.api.model.DiasSemana;
import br.com.washii.api.model.LavaJato;
import br.com.washii.api.model.Telefone;
import br.com.washii.api.model.Usuario;
import br.com.washii.api.model.UsuarioTelefone;
import br.com.washii.api.repository.DisponibilidadeRepository;
import br.com.washii.api.repository.DiasSemanaRepository;
import br.com.washii.api.repository.LavaJatoRepository;
import br.com.washii.api.repository.TelefoneRepository;
import br.com.washii.api.repository.UsuarioTelefoneRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.washii.api.dto.response.HorariosDisponiveisResponse;
import br.com.washii.api.model.Agendamento;
import br.com.washii.api.model.CategoriaVeiculo;
import br.com.washii.api.model.CategoriaVeiculoServico;
import br.com.washii.api.model.Servico;
import br.com.washii.api.model.StatusAgendamento;
import br.com.washii.api.repository.AgendamentoRepository;
import br.com.washii.api.repository.CategoriaVeiculoRepository;
import br.com.washii.api.repository.CategoriaVeiculoServicoRepository;
import br.com.washii.api.repository.ServicoRepository;

import java.text.Normalizer;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;


import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LavaJatoService {

    private final LavaJatoRepository lavaJatoRepository;
    private final DisponibilidadeRepository disponibilidadeRepository;
    private final DiasSemanaRepository diasSemanaRepository;
    private final TelefoneRepository telefoneRepository;
    private final UsuarioTelefoneRepository usuarioTelefoneRepository;

    private static final int INTERVALO_SUGESTOES_MINUTOS = 30;
    private static final ZoneId ZONA_HORARIA = ZoneId.of("America/Sao_Paulo");

    private final AgendamentoRepository agendamentoRepository;
    private final ServicoRepository servicoRepository;
    private final CategoriaVeiculoRepository categoriaVeiculoRepository;
    private final CategoriaVeiculoServicoRepository categoriaVeiculoServicoRepository;

    // Gestão cadastral do sistema
    @Transactional
    public LavaJatoResponse buscarPorId(UUID id){
        LavaJato lavaJato = lavaJatoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lava jato não encontrado com o id = " + id));

       return LavaJatoResponse.fromEntity(lavaJato);
    }

    @Transactional
    public void atualizar(UUID id, AtualizacaoLavaJatoRequest request) {
        LavaJato lavaJato = lavaJatoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lava jato não encontrado com o id = " + id));

        lavaJato.setFotoPerfil(request.fotoPerfil());
        lavaJato.setRazaoSocial(request.razaoSocial());
        lavaJato.setNomeFantasia(request.nomeFantasia());
        lavaJato.setFluxoSimultaneo(request.fluxoSimultaneo());
        lavaJato.setCnpj(request.cnpj());
        lavaJato.setCidade(request.cidade());
        lavaJato.setEstado(request.estado());
        lavaJato.setLogradouro(request.logradouro());
        lavaJato.setNumero(request.numero());
        lavaJato.setBairro(request.bairro());
        lavaJato.setCep(request.cep());

        if (request.telefone() != null && !request.telefone().isBlank()) {
            atualizarTelefone(lavaJato, request.telefone());
        }

        if (request.latitude() != null && request.longitude() != null) {
            GeometryFactory geometryFactory = new GeometryFactory(
                    new PrecisionModel(),
                    4326
            );

            Point point = geometryFactory.createPoint(
                    new Coordinate(request.longitude(), request.latitude())
            );

            // Atribui a nova instância diretamente à entidade
            lavaJato.setCoordenadas(point);
        }

        lavaJatoRepository.save(lavaJato);
    }

    private void atualizarTelefone(Usuario usuario, String telefoneRaw) {
        if (telefoneRaw == null || telefoneRaw.isBlank()) {
            return;
        }

        String digitos = telefoneRaw.replaceAll("\\D", "");
        if (digitos.length() < 10 || digitos.length() > 11) {
            return;
        }

        String ddd = digitos.substring(0, 2);
        String numero = digitos.substring(2);

        Telefone telefone = telefoneRepository.findByDddAndNumero(ddd, numero)
                .orElseGet(() -> telefoneRepository.save(new Telefone(ddd, numero)));

        UsuarioTelefone usuarioTelefone = usuarioTelefoneRepository.findFirstByUsuario(usuario)
                .orElseGet(() -> new UsuarioTelefone(usuario, telefone));

        usuarioTelefone.setTelefone(telefone);
        usuarioTelefoneRepository.save(usuarioTelefone);

        if (usuario.getTelefones() != null) {
            usuario.getTelefones().clear();
            usuario.getTelefones().add(usuarioTelefone);
        }
    }

    @Transactional
    public void deletar(UUID id) {
        LavaJato lavaJato = lavaJatoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lava jato não encontrado com o id = " + id));

        lavaJato.setAtivo(false);

        lavaJatoRepository.save(lavaJato);
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
    @Transactional(readOnly = true)
    public HorariosDisponiveisResponse buscarHorariosDisponiveis(
            UUID lavaJatoId,
            LocalDate data,
            List<UUID> servicoIds,
            UUID categoriaVeiculoId
    ) {
        LavaJato lavaJato = buscarLavaJato(lavaJatoId);

        if (data == null || data.isBefore(LocalDate.now(ZONA_HORARIA))) {
            throw new ValidationException("A data consultada não pode estar no passado.");
        }

        if (servicoIds == null || servicoIds.isEmpty()) {
            throw new ValidationException("Informe pelo menos um serviço.");
        }

        if (new HashSet<>(servicoIds).size() != servicoIds.size()) {
            throw new ValidationException("Não informe serviços duplicados.");
        }

        CategoriaVeiculo categoria = categoriaVeiculoRepository.findById(categoriaVeiculoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Categoria de veículo não encontrada."
                ));

        List<Servico> servicos = servicoRepository.findAllById(servicoIds);

        if (servicos.size() != servicoIds.size()) {
            throw new ResourceNotFoundException(
                    "Um ou mais serviços não foram encontrados ou estão inativos."
            );
        }

        long duracaoTotalMinutos = 0;

        for (Servico servico : servicos) {
            if (servico.getLavaJato() == null
                    || !servico.getLavaJato().getId().equals(lavaJatoId)) {
                throw new ValidationException(
                        "Todos os serviços devem pertencer ao lava-jato informado."
                );
            }

            CategoriaVeiculoServico precoTempo =
                    categoriaVeiculoServicoRepository
                            .findByCategoriaVeiculoAndServico(categoria, servico)
                            .orElseThrow(() -> new ValidationException(
                                    "Há serviço indisponível para a categoria deste veículo."
                            ));

            duracaoTotalMinutos = Math.addExact(
                    duracaoTotalMinutos,
                    Duration.between(
                            LocalTime.MIDNIGHT,
                            precoTempo.getDuracao()
                    ).toMinutes()
            );
        }

        if (duracaoTotalMinutos < 1 || duracaoTotalMinutos >= 24 * 60) {
            throw new ValidationException("A duração total calculada é inválida.");
        }

        String diaCurto = nomeCurto(data.getDayOfWeek());

        List<Disponibilidade> expedientesDoDia =
                disponibilidadeRepository
                        .findAllByLavaJato_IdOrderByDiasSemana_Id(lavaJatoId)
                        .stream()
                        .filter(expediente ->
                                normalizar(expediente.getDiasSemana().getNomeCurto())
                                        .equals(normalizar(diaCurto)))
                        .toList();

        List<Agendamento> agendamentos =
                agendamentoRepository.buscarAgendamentosAtivosDoLavaJatoNaData(
                        lavaJatoId,
                        data,
                        StatusAgendamento.AGENDADO
                );

        List<LocalTime> horarios = new ArrayList<>();
        LocalDateTime agora = LocalDateTime.now(ZONA_HORARIA);

        for (Disponibilidade expediente : expedientesDoDia) {
            LocalTime ultimoInicio = expediente.getHrTermino()
                    .minusMinutes(duracaoTotalMinutos);

            for (LocalTime inicio = expediente.getHrInicio();
                 !inicio.isAfter(ultimoInicio);
                 inicio = inicio.plusMinutes(INTERVALO_SUGESTOES_MINUTOS)) {

                LocalDateTime inicioAtendimento = data.atTime(inicio);
                LocalDateTime fimAtendimento =
                        inicioAtendimento.plusMinutes(duracaoTotalMinutos);

                if (inicioAtendimento.isBefore(agora)) {
                    continue;
                }

                if (temCapacidade(
                        inicioAtendimento,
                        fimAtendimento,
                        agendamentos,
                        lavaJato.getFluxoSimultaneo()
                )) {
                    horarios.add(inicio);
                }
            }
        }

        List<LocalTime> horariosOrdenados = horarios.stream()
                .distinct()
                .sorted()
                .toList();

        return new HorariosDisponiveisResponse(
                lavaJatoId,
                data,
                duracaoTotalMinutos,
                horariosOrdenados
        );
    }

    private boolean temCapacidade(
            LocalDateTime inicioCandidato,
            LocalDateTime fimCandidato,
            List<Agendamento> agendamentos,
            int capacidadeMaxima
    ) {
        List<LocalDateTime> limites = new ArrayList<>();
        limites.add(inicioCandidato);
        limites.add(fimCandidato);

        for (Agendamento agendamento : agendamentos) {
            LocalDateTime inicioExistente = agendamento.getData().atTime(agendamento.getHora());
            long duracaoExistente = Duration.between(
                    LocalTime.MIDNIGHT,
                    agendamento.getDuracaoTotal()
            ).toMinutes();

            LocalDateTime fimExistente = inicioExistente.plusMinutes(duracaoExistente);

            boolean sobrepoe = inicioCandidato.isBefore(fimExistente)
                    && inicioExistente.isBefore(fimCandidato);

            if (!sobrepoe) {
                continue;
            }

            if (inicioExistente.isAfter(inicioCandidato)
                    && inicioExistente.isBefore(fimCandidato)) {
                limites.add(inicioExistente);
            }

            if (fimExistente.isAfter(inicioCandidato)
                    && fimExistente.isBefore(fimCandidato)) {
                limites.add(fimExistente);
            }
        }

        List<LocalDateTime> limitesOrdenados = limites.stream()
                .distinct()
                .sorted(Comparator.naturalOrder())
                .toList();

        for (int i = 0; i < limitesOrdenados.size() - 1; i++) {
            LocalDateTime inicioTrecho = limitesOrdenados.get(i);
            int ocupacao = 1; // atendimento que está sendo avaliado

            for (Agendamento agendamento : agendamentos) {
                LocalDateTime inicioExistente =
                        agendamento.getData().atTime(agendamento.getHora());

                long duracaoExistente = Duration.between(
                        LocalTime.MIDNIGHT,
                        agendamento.getDuracaoTotal()
                ).toMinutes();

                LocalDateTime fimExistente =
                        inicioExistente.plusMinutes(duracaoExistente);

                if (!inicioExistente.isAfter(inicioTrecho)
                        && fimExistente.isAfter(inicioTrecho)) {
                    ocupacao++;
                }
            }

            if (ocupacao > capacidadeMaxima) {
                return false;
            }
        }

        return true;
    }

    private String nomeCurto(DayOfWeek dia) {
        return switch (dia) {
            case MONDAY -> "seg";
            case TUESDAY -> "ter";
            case WEDNESDAY -> "qua";
            case THURSDAY -> "qui";
            case FRIDAY -> "sex";
            case SATURDAY -> "sab";
            case SUNDAY -> "dom";
        };
    }

    private String normalizar(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }
}
