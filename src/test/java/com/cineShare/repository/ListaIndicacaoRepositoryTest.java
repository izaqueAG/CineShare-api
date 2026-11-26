package com.cineShare.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.cineShare.model.ListaIndicacao;
import com.cineShare.model.Titulo;
import com.cineShare.model.User;

@DataJpaTest
class ListaIndicacaoRepositoryTest {

    @Autowired
    private ListaIndicacaoRepository listaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TituloRepository tituloRepository;

    @Test
    void dadoListaPersistida_quandoBuscarTodas_entaoRetornaListaComAutorETitulos() {
        User usuario = new User();
        usuario.setNome("Ana");
        usuario.setEmail("ana@cine.test");
        userRepository.save(usuario);

        Titulo titulo = new Titulo();
        titulo.setNome("Matrix");
        tituloRepository.save(titulo);

        ListaIndicacao lista = new ListaIndicacao();
        lista.setNome("Favoritos");
        lista.setUser(usuario);
        lista.setTitulos(List.of(titulo));
        listaRepository.save(lista);

        ListaIndicacao resultado = listaRepository.findAll().get(0);

        assertThat(resultado.getNome()).isEqualTo("Favoritos");
        assertThat(resultado.getUser().getEmail()).isEqualTo("ana@cine.test");
        assertThat(resultado.getTitulos()).extracting(Titulo::getNome).containsExactly("Matrix");
    }
}
