package br.com.washii.api.controller;

import br.com.washii.api.service.AgendamentoService;
import br.com.washii.api.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/agendamentos")
@RequiredArgsConstructor
public class AgendamentoController {

    AgendamentoService agendamentoService;

}
