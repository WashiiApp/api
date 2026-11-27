package br.com.washii.api.service;

import br.com.washii.api.dto.response.CategoriaServicoResponse;
import br.com.washii.api.repository.CategoriaServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaServicoService {
    private final CategoriaServicoRepository categoriaServicoRepository;

    @Transactional(readOnly = true)
    public List<CategoriaServicoResponse> listar() {
        return categoriaServicoRepository.findAll(Sort.by(Sort.Direction.ASC, "nome")).stream()
                .map(CategoriaServicoResponse::fromEntity)
                .toList();
    }
}
