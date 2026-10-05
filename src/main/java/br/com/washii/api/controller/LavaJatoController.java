package br.com.washii.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("lavajatos")
public class LavaJatoController {

    // Gestão cadastral do sistema
    @GetMapping
    public void buscar(){

    }

    // Expediente
    public int somar(int a, int b) {
        return  a + b;
    }


    // Serviços


    // Operacionais e Consultas

}
