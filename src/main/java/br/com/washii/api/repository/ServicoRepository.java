package br.com.washii.api.repository;

import br.com.washii.api.model.LavaJato;
import br.com.washii.api.model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ServicoRepository extends JpaRepository<Servico, UUID> {
    List<Servico> findAllByLavaJato(LavaJato lavaJato);
}
