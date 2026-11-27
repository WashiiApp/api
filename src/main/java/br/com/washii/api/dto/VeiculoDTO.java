package br.com.washii.api.dto;

import br.com.washii.api.model.Veiculo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VeiculoDTO(
        @NotBlank(message = "A placa é obrigatória.")
        @Size(max = 8, message = "A placa deve ter no máximo 8 caracteres.")
        String placa,
        @NotBlank(message = "A marca é obrigatória.")
        @Size(max = 50, message = "A marca deve ter no máximo 50 caracteres.")
        String marca,
        @NotBlank(message = "O modelo é obrigatório.")
        @Size(max = 100, message = "O modelo deve ter no máximo 100 caracteres.")
        String modelo,
        @NotBlank(message = "A cor é obrigatória.")
        @Size(max = 30, message = "A cor deve ter no máximo 30 caracteres.")
        String cor,
        @NotBlank(message = "A categoria é obrigatória.")
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
