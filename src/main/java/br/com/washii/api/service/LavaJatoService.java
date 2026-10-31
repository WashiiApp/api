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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @CacheEvict(value = "expedientesLavaJato", key = "#lavaJatoId")
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


    @Cacheable(value = "expedientesLavaJato", key = "#lavaJatoId")
    @Transactional(readOnly = true)
    public List<ExpedienteResponse> buscarExpediente(UUID lavaJatoId) {
        buscarLavaJato(lavaJatoId);
        return disponibilidadeRepository.findAllByLavaJato_IdOrderByDiasSemana_Id(lavaJatoId).stream()
                .map(ExpedienteResponse::fromEntity)
                .toList();
    }

    @CacheEvict(value = "expedientesLavaJato", key = "#lavaJatoId")
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

    @CacheEvict(value = "expedientesLavaJato", key = "#lavaJatoId")
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
}
