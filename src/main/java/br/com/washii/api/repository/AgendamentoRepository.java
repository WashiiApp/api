package br.com.washii.api.repository;

import br.com.washii.api.model.Agendamento;
import br.com.washii.api.model.StatusAgendamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AgendamentoRepository extends JpaRepository<Agendamento, UUID>, JpaSpecificationExecutor<Agendamento> {
    @Query(value = "select distinct a.id from agendamento a " +
            "join agendamento_servico ags on ags.id_agendamento = a.id " +
            "join servico s on s.id = ags.id_servico " +
            "where s.id_lavajato = :lavaJatoId and a.data = :data " +
            "and a.status_agendamento = :statusAgendado", nativeQuery = true)
    List<UUID> findAtivosDoLavaJatoNaData(@Param("lavaJatoId") UUID lavaJatoId,
                                           @Param("data") LocalDate data,
                                           @Param("statusAgendado") String statusAgendado);

    @Query("""
       select distinct a
       from Agendamento a
       join a.agendamentoServicos ags
       join ags.servico s
       where s.lavaJato.id = :lavaJatoId
         and a.data = :data
         and a.statusAgendamento = :status
       """)
    List<Agendamento> buscarAgendamentosAtivosDoLavaJatoNaData(
            @Param("lavaJatoId") UUID lavaJatoId,
            @Param("data") LocalDate data,
            @Param("status") StatusAgendamento status
    );
}
