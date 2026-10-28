package br.com.washii.api.service;

import br.com.washii.api.exception.BusinessException;
import br.com.washii.api.model.Agendamento;
import br.com.washii.api.model.Cliente;
import br.com.washii.api.model.StatusAgendamento;
import br.com.washii.api.model.Veiculo;
import br.com.washii.api.repository.*;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class AgendamentoServiceTest {

    private final AgendamentoRepository agendamentoRepository = mock(AgendamentoRepository.class);
    private final AgendamentoService service = new AgendamentoService(
            agendamentoRepository,
            mock(AgendamentoServicoRepository.class),
            mock(ClienteRepository.class),
            mock(LavaJatoRepository.class),
            mock(VeiculoRepository.class),
            mock(ServicoRepository.class),
            mock(CategoriaVeiculoServicoRepository.class)
    );

    @Test
    void naoDeveCancelarAgendamentoConcluidoPeloCliente() {
        UUID agendamentoId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        Cliente cliente = new Cliente();
        cliente.setId(clienteId);
        Veiculo veiculo = new Veiculo();
        veiculo.setCliente(cliente);
        Agendamento agendamento = new Agendamento();
        agendamento.setVeiculo(veiculo);
        agendamento.setStatusAgendamento(StatusAgendamento.CONCLUIDO);
        when(agendamentoRepository.findById(agendamentoId)).thenReturn(Optional.of(agendamento));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> service.cancelarPeloCliente(agendamentoId, clienteId)
        );

        assertEquals("Agendamentos concluídos não podem ser cancelados.", exception.getMessage());
        assertEquals(StatusAgendamento.CONCLUIDO, agendamento.getStatusAgendamento());
        verify(agendamentoRepository).findById(agendamentoId);
        verifyNoMoreInteractions(agendamentoRepository);
    }
}
