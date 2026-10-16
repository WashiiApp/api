package br.com.washii.api.repository;

import br.com.washii.api.model.DiasSemana;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DiasSemanaRepository extends JpaRepository<DiasSemana, UUID> {
}
