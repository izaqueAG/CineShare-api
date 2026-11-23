package com.cineShare.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.cineShare.model.User;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void dadoUsuariosPersistidos_quandoBuscarPorNomeIgnorandoMaiusculas_entaoRetornaCorrespondencia() {
        User usuario = new User();
        usuario.setNome("Ana Silva");
        usuario.setEmail("ana@cine.test");
        userRepository.save(usuario);

        assertThat(userRepository.findByNomeContainingIgnoreCase("ANA"))
                .extracting(User::getEmail)
                .containsExactly("ana@cine.test");
    }

}
