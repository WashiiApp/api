package br.com.washii.api.controller;

import br.com.washii.api.dto.response.CategoriaVeiculoResponse;
import br.com.washii.api.service.CategoriaVeiculoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categorias-veiculos")
@RequiredArgsConstructor
public class CategoriaVeiculoController {
    private final CategoriaVeiculoService categoriaVeiculoService;

    @GetMapping
    public ResponseEntity<List<CategoriaVeiculoResponse>> listar() {
        return ResponseEntity.ok(categoriaVeiculoService.listar());
    }
}
