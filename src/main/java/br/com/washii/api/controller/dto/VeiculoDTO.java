package br.com.washii.api.controller.dto;

import br.com.washii.api.model.CategoriaVeiculo;
import br.com.washii.api.model.Veiculo;

public record VeiculoDTO(
        String placa,
        String marca,
        String modelo,
        String cor,
        String categoria // MOTO, CARRO, SUV, PICKUP, etc.
)
{
    public static VeiculoDTO from(Veiculo veiculo){
        return new VeiculoDTO(
                veiculo.getPlaca(),
                veiculo.getMarca(),
                veiculo.getModelo(),
                veiculo.getCor(),
                veiculo.getCategoriaVeiculo().getNome()
        );
    }
}