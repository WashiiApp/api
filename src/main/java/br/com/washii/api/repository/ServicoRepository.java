package br.com.washii.api.repository;

import br.com.washii.api.model.LavaJato;
import br.com.washii.api.model.Servico;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ServicoRepository extends JpaRepository<Servico, UUID> {
    List<Servico> findAllByLavaJato(LavaJato lavaJato);
    Boolean existsByIdAndLavaJato(UUID id, LavaJato lavaJato);

    @Modifying
    @Query(value = "update servico set ativo = :ativo where id = :servicoId and id_lavajato = :lavaJatoId", nativeQuery = true)
    int atualizarStatus(@Param("servicoId") UUID servicoId,
                        @Param("lavaJatoId") UUID lavaJatoId,
                        @Param("ativo") boolean ativo);
}
