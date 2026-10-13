package br.com.washii.api.repository.specs;

import br.com.washii.api.model.Agendamento;
import br.com.washii.api.model.CategoriaVeiculo;
import br.com.washii.api.model.StatusAgendamento;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.UUID;

public class AgendamentoSpecs {
    public static Specification<Agendamento> idEquals(UUID usuarioId) {
        return ((root, query, cb) -> {
            if (usuarioId == null) return null;

            return null;
        });
    }

    public static Specification<Agendamento> placaVeiculoEqual(String placaVeiculo) {
        return ((root, query, cb) -> {
            if (placaVeiculo == null) return null;

            return null;
        });
    }

    public static Specification<Agendamento> statusEquals(StatusAgendamento status) {
        return ((root, query, cb) -> {
            if (status == null) return null;

            return null;
        });
    }

    public static Specification<Agendamento> categoriaVeiculoEquals(CategoriaVeiculo categoriaVeiculo) {
        return ((root, query, cb) -> {
            if (categoriaVeiculo == null) return null;

            return null;
        });
    }

    public static Specification<Agendamento> dataBetween(LocalDateTime inicio, LocalDateTime fim) {
        return (root, query, cb) -> {
            if (inicio == null && fim == null) return null;

            return null;
        };
    }



}
