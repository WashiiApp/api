package br.com.washii.api.repository.specs;

import br.com.washii.api.model.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.UUID;

public class AgendamentoSpecs {

    public static Specification<Agendamento> idUsuarioEquals(UUID usuarioId) {
        return (root, query, cb) -> {
            if (usuarioId == null) return cb.conjunction();

            // Caminho 1: Cliente (Agendamento -> Veiculo -> Cliente)
            Join<Agendamento, Veiculo> veiculoJoin = root.join("veiculo", JoinType.LEFT);
            Join<Veiculo, Cliente> clienteJoin = veiculoJoin.join("cliente", JoinType.LEFT);
    
            // O ID do cliente é a própria PK/FK que aponta para o usuário (`id_usuario` na tabela cliente)
            Predicate clientePredicate = cb.equal(clienteJoin.get("id"), usuarioId);

            // Caminho 2: Lava Jato (Agendamento -> AgendamentoServico -> Servico -> LavaJato)
            Join<Agendamento, AgendamentoServico> agendamentoServicoJoin = root.join("agendamentoServicos", JoinType.LEFT);
            Join<AgendamentoServico, Servico> servicoJoin = agendamentoServicoJoin.join("servico", JoinType.LEFT);
            Join<Servico, LavaJato> lavaJatoJoin = servicoJoin.join("lavaJato", JoinType.LEFT);

            // O ID do lava jato é a própria PK/FK que aponta para o usuário (`id_usuario` na tabela lava_jato)
            Predicate lavaJatoPredicate = cb.equal(lavaJatoJoin.get("id"), usuarioId);

            // Como pode ser um ou outro dependendo de quem está logado, usamos OR
            query.distinct(true); // Necessário por causa dos joins de coleção (agendamentoServicos)
            return cb.or(clientePredicate, lavaJatoPredicate);
        };
    }

    public static Specification<Agendamento> placaVeiculoEqual(String placaVeiculo) {
        return (root, query, cb) -> {
            if (placaVeiculo == null || placaVeiculo.isBlank()) return cb.conjunction();

            Join<Agendamento, Veiculo> veiculoJoin = root.join("veiculo", JoinType.INNER);
            return cb.equal(cb.lower(veiculoJoin.get("placa")), placaVeiculo.toLowerCase());
        };
    }

    public static Specification<Agendamento> statusEquals(StatusAgendamento status) {
        return (root, query, cb) -> {
            if (status == null) return cb.conjunction();

            return cb.equal(root.get("statusAgendamento"), status);
        };
    }

    public static Specification<Agendamento> categoriaVeiculoIdEquals(UUID categoriaVeiculoId) {
        return (root, query, cb) -> {
            if (categoriaVeiculoId == null) return cb.conjunction();

            // Caminho: Agendamento -> Veiculo -> CategoriaVeiculo
            Join<Agendamento, Veiculo> veiculoJoin = root.join("veiculo", JoinType.INNER);
            Join<Veiculo, CategoriaVeiculo> categoriaJoin = veiculoJoin.join("categoriaVeiculo", JoinType.INNER);

            return cb.equal(categoriaJoin.get("id"), categoriaVeiculoId);
        };
    }

    public static Specification<Agendamento> dataBetween(LocalDateTime inicio, LocalDateTime fim) {
        return (root, query, cb) -> {
            if (inicio == null && fim == null) return cb.conjunction();

            if (inicio != null && fim != null) {
                return cb.between(root.get("data"), inicio.toLocalDate(), fim.toLocalDate());
            } else if (inicio != null) {
                return cb.greaterThanOrEqualTo(root.get("data"), inicio.toLocalDate());
            } else {
                return cb.lessThanOrEqualTo(root.get("data"), fim.toLocalDate());
            }
        };
    }
}