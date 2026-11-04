package br.com.washii.api.service;

import br.com.washii.api.dto.VeiculoDTO;
import br.com.washii.api.exception.ResourceNotFoundException;
import br.com.washii.api.exception.ValidationException;
import br.com.washii.api.model.CategoriaVeiculo;
import br.com.washii.api.model.Cliente;
import br.com.washii.api.model.Veiculo;
import br.com.washii.api.repository.CategoriaVeiculoRepository;
import br.com.washii.api.repository.ClienteRepository;
import br.com.washii.api.repository.TelefoneRepository;
import br.com.washii.api.repository.UsuarioTelefoneRepository;
import br.com.washii.api.repository.VeiculoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ClienteServiceTest {

    private final ClienteRepository clienteRepository = mock(ClienteRepository.class);
    private final VeiculoRepository veiculoRepository = mock(VeiculoRepository.class);
    private final StorageProvider storageProvider = mock(StorageProvider.class);
    private final ClienteService clienteService = new ClienteService(
            clienteRepository,
            veiculoRepository,
            mock(CategoriaVeiculoRepository.class),
            mock(TelefoneRepository.class),
            mock(UsuarioTelefoneRepository.class),
            storageProvider
    );

    @Test
    void deveRetornarVeiculoAoBuscarPorPlacaNormalizada() {
        Veiculo veiculo = criarVeiculo("BRA2E19");
        when(veiculoRepository.findByPlaca("BRA2E19")).thenReturn(Optional.of(veiculo));

        VeiculoDTO resultado = clienteService.buscarVeiculoPorPlaca("bra-2e19");

        assertEquals(new VeiculoDTO("BRA2E19", "Honda", "Sport", "Preta", "MOTO"), resultado);
        verify(veiculoRepository).findByPlaca("BRA2E19");
    }

    @Test
    void deveRejeitarPlacaEmFormatoInvalidoSemConsultarRepositorio() {
        assertThrows(ValidationException.class,
                () -> clienteService.buscarVeiculoPorPlaca("ABC123"));

        verifyNoInteractions(veiculoRepository);
    }

    @Test
    void deveInformarQuandoVeiculoNaoForEncontrado() {
        when(veiculoRepository.findByPlaca("BRA2E19")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> clienteService.buscarVeiculoPorPlaca("BRA2E19")
        );

        assertEquals("Veículo não encontrado com a placa = BRA2E19", exception.getMessage());
        verify(veiculoRepository).findByPlaca("BRA2E19");
    }

    @Test
    void deveFazerUploadDaFotoDePerfilESalvarUrlNoCliente() {
        UUID id = UUID.randomUUID();
        Cliente cliente = new Cliente();
        cliente.setId(id);
        byte[] bytesImagem = new byte[]{1, 2, 3};
        MockMultipartFile arquivo = new MockMultipartFile("arquivo", "foto.png", "image/png", bytesImagem);
        String caminhoEsperado = "clientes/" + id + "/perfil.png";
        String urlEsperada = "https://projeto.supabase.co/storage/v1/object/public/fotos-perfil/" + caminhoEsperado;

        when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));
        when(storageProvider.upload(caminhoEsperado, bytesImagem, "image/png")).thenReturn(urlEsperada);

        String urlRetornada = clienteService.atualizarFotoPerfil(id, arquivo);

        assertEquals(urlEsperada, urlRetornada);
        assertEquals(urlEsperada, cliente.getFotoPerfil());
        verify(storageProvider).upload(caminhoEsperado, bytesImagem, "image/png");
        verify(clienteRepository).save(cliente);
    }

    private Veiculo criarVeiculo(String placa) {
        CategoriaVeiculo categoria = new CategoriaVeiculo();
        categoria.setNome("MOTO");

        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca(placa);
        veiculo.setMarca("Honda");
        veiculo.setModelo("Sport");
        veiculo.setCor("Preta");
        veiculo.setCategoriaVeiculo(categoria);
        return veiculo;
    }
}
