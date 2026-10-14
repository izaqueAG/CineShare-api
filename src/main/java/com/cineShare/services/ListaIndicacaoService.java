package com.cineShare.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.cineShare.model.ListaIndicacao;
import com.cineShare.model.Titulo;
import com.cineShare.model.User;
import com.cineShare.repository.ListaIndicacaoRepository;
import com.cineShare.repository.TituloRepository;
import com.cineShare.repository.UserRepository;

import DTO.ListaIndicacaoDTO;

@Service
public class ListaIndicacaoService {

    private final ListaIndicacaoRepository listaRepository;
    private final TituloService tituloService;
    private final UserRepository userRepository;

    public ListaIndicacaoService(
            ListaIndicacaoRepository listaRepository,
            TituloService tituloService,
            UserRepository userRepository) {

        this.listaRepository = listaRepository;
        this.tituloService = tituloService;
        this.userRepository = userRepository;
    }

    public ListaIndicacao criarLista(ListaIndicacaoDTO dto) {

        if (dto.getTitulos() == null || dto.getTitulos().isEmpty()) {
            throw new RuntimeException("Escolha pelo menos um título.");
        }

        if (dto.getTitulos().size() > 3) {
            throw new RuntimeException("Escolha no máximo 3 títulos.");
        }
        System.out.println("userId recebido: " + dto.getUserId());

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado.")
                );

        List<Titulo> titulos = new ArrayList<>();

        for (String nomeTitulo : dto.getTitulos()) {
            Titulo titulo = tituloService.getTitulo(nomeTitulo);
            titulos.add(titulo);
        }

        ListaIndicacao lista = new ListaIndicacao();

        lista.setNome(dto.getNome());
        lista.setTitulos(titulos);
        lista.setUser(user);

        return listaRepository.save(lista);
    }
}