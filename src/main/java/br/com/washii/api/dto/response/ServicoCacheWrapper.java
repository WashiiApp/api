package br.com.washii.api.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public record ServicoCacheWrapper(
        List<ServicoResponse> servicos
) {}