package com.cineShare.controler;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cineShare.model.Titulo;
import com.cineShare.services.TituloService;

@RestController
public class TituloController {

    private final TituloService tituloService;

    public TituloController(TituloService tituloService) {
        this.tituloService = tituloService;
    }

    @GetMapping("/titulos/buscar")
    public Titulo getTitulo(
            @RequestParam String nome) {

        return tituloService.getTitulo(nome);
    }
}