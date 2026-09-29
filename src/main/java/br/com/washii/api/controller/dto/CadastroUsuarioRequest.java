package br.com.washii.api.controller.dto;

import br.com.washii.api.model.TipoUsuario;

public record CadastroUsuarioRequest(
        String email,
        String senha,
        String cidade,
        String estado,
        TipoUsuario tipoUsuario
) {}
