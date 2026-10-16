package br.com.washii.api.repository;

import br.com.washii.api.model.Agendamento;
import br.com.washii.api.model.AgendamentoServico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AgendamentoServicoRepository extends JpaRepository<AgendamentoServico, UUID> {
    List<AgendamentoServico> findByAgendamento(Agendamento agendamento);
}
