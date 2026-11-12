package br.com.washii.api.repository;

import br.com.washii.api.model.Avaliacao;
import br.com.washii.api.model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, UUID> {
    List<Avaliacao> findAllByServico(Servico servico);
}
