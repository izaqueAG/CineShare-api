package com.cineShare.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.cineShare.model.ListaIndicacao;
import com.cineShare.model.Titulo;
import com.cineShare.repository.ListaIndicacaoRepository;
import com.cineShare.repository.TituloRepository;

import DTO.ListaIndicacaoDTO;

@Service
public class ListaIndicacaoService {

    private final ListaIndicacaoRepository listaRepository;
    private final TituloService tituloService;

    public ListaIndicacaoService(
            ListaIndicacaoRepository listaRepository,
            TituloService tituloService) {

        this.listaRepository = listaRepository;
        this.tituloService = tituloService;
    }

    public ListaIndicacao criarLista(ListaIndicacaoDTO dto) {

        if (dto.getTitulos() == null || dto.getTitulos().isEmpty()) {
            throw new RuntimeException(
                    "Escolha pelo menos um título."
            );
        }

        if (dto.getTitulos().size() > 3) {
            throw new RuntimeException(
                    "Escolha ate no máximo 3 títulos."
            );
        }

        List<Titulo> titulos = new ArrayList<>();

        for (String nomeTitulo : dto.getTitulos()) {

            Titulo titulo = tituloService.getTitulo(nomeTitulo);

            titulos.add(titulo);
        }

        ListaIndicacao lista = new ListaIndicacao();

        lista.setNome(dto.getNome());
        lista.setTitulos(titulos);

        return listaRepository.save(lista);
    }
}