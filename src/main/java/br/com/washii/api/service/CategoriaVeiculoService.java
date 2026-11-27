package br.com.washii.api.service;

import br.com.washii.api.dto.response.CategoriaVeiculoResponse;
import br.com.washii.api.repository.CategoriaVeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaVeiculoService {
    private final CategoriaVeiculoRepository categoriaVeiculoRepository;

    @Transactional(readOnly = true)
    public List<CategoriaVeiculoResponse> listar() {
        return categoriaVeiculoRepository.findAll(Sort.by(Sort.Direction.ASC, "nome")).stream()
                .map(CategoriaVeiculoResponse::fromEntity)
                .toList();
    }
}
