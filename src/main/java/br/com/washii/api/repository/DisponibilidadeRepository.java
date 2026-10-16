package br.com.washii.api.repository;

import br.com.washii.api.model.Disponibilidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DisponibilidadeRepository extends JpaRepository<Disponibilidade, UUID> {
    List<Disponibilidade> findAllByLavaJato_IdOrderByDiasSemana_Id(UUID lavaJatoId);

    Optional<Disponibilidade> findByIdAndLavaJato_Id(UUID id, UUID lavaJatoId);

    void deleteAllByLavaJato_Id(UUID lavaJatoId);
}
