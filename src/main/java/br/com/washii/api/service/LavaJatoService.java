package br.com.washii.api.service;

import br.com.washii.api.dto.request.AtualizacaoLavaJatoRequest;
import br.com.washii.api.dto.response.LavaJatoResponse;
import br.com.washii.api.model.LavaJato;
import br.com.washii.api.repository.LavaJatoRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LavaJatoService {

    private final LavaJatoRepository lavaJatoRepository;

    // Gestão cadastral do sistema
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

        if (request.latitude() != null && request.longitude() != null) {
            GeometryFactory geometryFactory = new GeometryFactory(
                    new PrecisionModel(),
                    4326
            );

            Point point = geometryFactory.createPoint(
                    new Coordinate(request.longitude(), request.latitude())
            );

            lavaJato.getCoordenadas().getCoordinate().setCoordinate(point.getCoordinate());
        }

        lavaJatoRepository.save(lavaJato);
    }

    public void deletar(UUID id) {
        LavaJato lavaJato = lavaJatoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lava jato não encontrado com o id = " + id));

        lavaJato.setAtivo(false);

        lavaJatoRepository.save(lavaJato);
    }

    // Expediente


    // Serviços


    // Operacionais e Consultas
}
