package br.com.washii.api.repository;


import br.com.washii.api.model.Notificacao;
import br.com.washii.api.model.TipoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;
import java.util.List;

public interface NotificacaoRepository extends JpaRepository<Notificacao, UUID> {

    @Query("""
        SELECT n FROM Notificacao n
        WHERE n.agendamento.veiculo.cliente.id = :clienteId
          AND n.tipoUsuario = :tipoUsuario
        ORDER BY n.createdAt DESC
    """)
    List<Notificacao> buscarPorCliente(
            @Param("clienteId") UUID clienteId,
            @Param("tipoUsuario") TipoUsuario tipoUsuario
    );

    @Query("""
        SELECT DISTINCT n FROM Notificacao n
        JOIN n.agendamento.agendamentoServicos ags
        WHERE ags.servico.lavaJato.id = :lavaJatoId
          AND n.tipoUsuario = :tipoUsuario
        ORDER BY n.createdAt DESC
    """)
    List<Notificacao> buscarPorLavaJato(
            @Param("lavaJatoId") UUID lavaJatoId,
            @Param("tipoUsuario") TipoUsuario tipoUsuario
    );
}