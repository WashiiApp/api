package br.com.washii.api.controller;

import br.com.washii.api.dto.response.CategoriaServicoResponse;
import br.com.washii.api.service.CategoriaServicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categorias-servicos")
@RequiredArgsConstructor
public class CategoriaServicoController {
    private final CategoriaServicoService categoriaServicoService;

    @GetMapping
    public ResponseEntity<List<CategoriaServicoResponse>> listar() {
        return ResponseEntity.ok(categoriaServicoService.listar());
    }
}
