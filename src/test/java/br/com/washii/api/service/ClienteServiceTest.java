package br.com.washii.api.service;

import br.com.washii.api.dto.VeiculoDTO;
import br.com.washii.api.exception.ResourceNotFoundException;
import br.com.washii.api.exception.ValidationException;
import br.com.washii.api.model.CategoriaVeiculo;
import br.com.washii.api.model.Veiculo;
import br.com.washii.api.repository.CategoriaVeiculoRepository;
import br.com.washii.api.repository.ClienteRepository;
import br.com.washii.api.repository.TelefoneRepository;
import br.com.washii.api.repository.UsuarioTelefoneRepository;
import br.com.washii.api.repository.VeiculoRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ClienteServiceTest {

    private final VeiculoRepository veiculoRepository = mock(VeiculoRepository.class);
    private final ClienteService clienteService = new ClienteService(
            mock(ClienteRepository.class),
            veiculoRepository,
            mock(CategoriaVeiculoRepository.class),
            mock(TelefoneRepository.class),
            mock(UsuarioTelefoneRepository.class)
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
