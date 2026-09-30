package br.com.washii.api.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("clientes")
public class ClienteController {

    @GetMapping
    @PreAuthorize("authenticated()")
    public String salvar(){
        return "ok";
    }
}
