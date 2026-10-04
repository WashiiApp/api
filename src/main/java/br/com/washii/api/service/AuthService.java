package br.com.washii.api.service;

import br.com.washii.api.dto.request.CadastroClienteRequest;
import br.com.washii.api.dto.request.CadastroLavaJatoRequest;
import br.com.washii.api.dto.request.LoginRequest;
import br.com.washii.api.dto.response.LoginResponse;
import br.com.washii.api.model.Cliente;
import br.com.washii.api.model.LavaJato;
import br.com.washii.api.model.TipoUsuario;
import br.com.washii.api.repository.ClienteRepository;
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
public class AuthService {

    private final AuthProvider authProvider;
    private final ClienteRepository clienteRepository;
    private final LavaJatoRepository lavaJatoRepository;

    public LoginResponse login(LoginRequest request) {
        return authProvider.autenticar(
                request.email(),
                request.senha()
        );
    }

    @Transactional
    public void cadastrarCliente(CadastroClienteRequest request) {

        UUID id = null;

        try {
            id = cadastrarUsuarioAuthProvider(request.email(), request.senha());

            Cliente cliente = criarCliente(id, request);

            // Flush faz o commit imediatamente, o que garante que se houver algum
            // erro, seja capturado no catch
            clienteRepository.saveAndFlush(cliente);

        } catch (Exception e) {

            reverterCriacaoUsuarioAuthProvider(id);

            throw e;
        }
    }

    private Cliente criarCliente(
            UUID id,
            CadastroClienteRequest request
    ) {
        Cliente cliente = new Cliente();

        cliente.setId(id);
        cliente.setAtivo(true);
        cliente.setEmail(request.email());
        cliente.setCidade(request.cidade());
        cliente.setEstado(request.estado());
        cliente.setCpf(request.cpf());
        cliente.setPrimeiroNome(request.nome());
        cliente.setSobreNome(request.sobrenome());
        cliente.setTipoUsuario(TipoUsuario.CLIENTE);

        return cliente;
    }

    @Transactional
    public void cadastrarLavaJato(CadastroLavaJatoRequest request) {

        UUID id = null;

        try {
            id = cadastrarUsuarioAuthProvider(request.email(), request.senha());

            LavaJato lavaJato = criarLavaJato(id, request);

            // Flush faz o commit imediatamente, o que garante que se houver algum
            // erro, seja capturado no catch
            lavaJatoRepository.saveAndFlush(lavaJato);

        } catch (Exception e) {

            reverterCriacaoUsuarioAuthProvider(id);

            // Faz o rollback do PostgreSQL
            throw e;
        }
    }

    private static LavaJato criarLavaJato(
            UUID id,
            CadastroLavaJatoRequest request
    ) {
        LavaJato lavaJato = new LavaJato();

        lavaJato.setId(id);
        lavaJato.setAtivo(true);
        lavaJato.setEmail(request.email());
        lavaJato.setCidade(request.cidade());
        lavaJato.setEstado(request.estado());
        lavaJato.setCep(request.cep());
        lavaJato.setBairro(request.bairro());
        lavaJato.setLogradouro(request.logradouro());
        lavaJato.setNumero(request.numero());
        lavaJato.setCnpj(request.cnpj());
        lavaJato.setFluxoSimultaneo(request.fluxoSimultaneo());
        lavaJato.setNomeFantasia(request.nomeFantasia());
        lavaJato.setRazaoSocial(request.razaoSocial());
        lavaJato.setTipoUsuario(TipoUsuario.LAVA_JATO);

        if (request.coordenadas() != null) {
            Point point = converterCoordenadasParaPoint(
                    request.coordenadas().longitude(),
                    request.coordenadas().latitude()
            );

            lavaJato.setCoordenadas(point);
        }

        return lavaJato;
    }

    private static Point converterCoordenadasParaPoint(
            Double longitude,
            Double latitude
    ) {
        GeometryFactory geometryFactory = new GeometryFactory(
                new PrecisionModel(),
                4326
        );

        return geometryFactory.createPoint(
                new Coordinate(longitude, latitude)
        );
    }

    private UUID cadastrarUsuarioAuthProvider(String email, String senha) {
        return authProvider.cadastrar(
                email,
                senha
        );
    }

    private void reverterCriacaoUsuarioAuthProvider(UUID id) {
        if (id == null) return;

        try {
            authProvider.excluir(id);
        } catch (Exception rollbackException) {
            System.err.println(
                    "Falha ao excluir usuário do Supabase: "
                            + rollbackException.getMessage()
            );
        }
    }


    public void logout() {
    }
}