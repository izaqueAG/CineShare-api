package com.cineShare.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cineShare.model.User;
import com.cineShare.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void dadoEmailExistente_quandoSalvar_entaoRetornaUsuarioExistente() {
        User existente = usuario(1L, "Ana", "ana@cine.test");
        User recebido = usuario(0L, "Outro nome", "ana@cine.test");
        when(userRepository.findByEmail(recebido.getEmail())).thenReturn(existente);

        User resultado = userService.salvar(recebido);

        assertThat(resultado).isSameAs(existente);
    }

    @Test
    void dadoEmailNovo_quandoSalvar_entaoPersisteUsuario() {
        User recebido = usuario(0L, "Ana", "ana@cine.test");
        when(userRepository.findByEmail(recebido.getEmail())).thenReturn(null);
        when(userRepository.save(recebido)).thenReturn(recebido);

        User resultado = userService.salvar(recebido);

        assertThat(resultado).isSameAs(recebido);
        verify(userRepository).save(recebido);
    }

    @Test
    void dadoIdNumericoExistente_quandoBuscar_entaoRetornaUsuario() {
        User usuario = usuario(7L, "Ana", "ana@cine.test");
        when(userRepository.findById(7L)).thenReturn(Optional.of(usuario));

        List<User> resultado = userService.findByNameOrId("7");

        assertThat(resultado).containsExactly(usuario);
    }

    @Test
    void dadoIdNumericoAusente_quandoBuscar_entaoRetornaListaVazia() {
        when(userRepository.findById(7L)).thenReturn(Optional.empty());

        List<User> resultado = userService.findByNameOrId("7");

        assertThat(resultado).isEmpty();
    }

    @Test
    void dadoNome_quandoBuscar_entaoRetornaCorrespondencias() {
        User usuario = usuario(7L, "Ana", "ana@cine.test");
        when(userRepository.findByNomeContainingIgnoreCase("ana")).thenReturn(List.of(usuario));

        List<User> resultado = userService.findByNameOrId("ana");

        assertThat(resultado).containsExactly(usuario);
    }

    @Test
    void dadoNomeSemCorrespondencia_quandoBuscar_entaoRetornaListaVazia() {
        when(userRepository.findByNomeContainingIgnoreCase("ausente")).thenReturn(List.of());

        assertThat(userService.findByNameOrId("ausente")).isEmpty();
    }

    @Test
    void dadoListaDeUsuarios_quandoListarTodos_entaoRetornaUsuariosDoRepositorio() {
        User usuario = usuario(1L, "Ana", "ana@cine.test");
        when(userRepository.findAll()).thenReturn(List.of(usuario));

        assertThat(userService.listAll()).containsExactly(usuario);
    }

    @Test
    void dadoEmailNovo_quandoSalvar_entaoPreservaDadosRecebidos() {
        User recebido = usuario(0L, "Bruno", "bruno@cine.test");
        when(userRepository.findByEmail("bruno@cine.test")).thenReturn(null);
        when(userRepository.save(recebido)).thenReturn(recebido);

        assertThat(userService.salvar(recebido).getNome()).isEqualTo("Bruno");
    }

    @Test
    void dadoIdComZeros_quandoBuscar_entaoInterpretaMesmoIdNumerico() {
        User usuario = usuario(7L, "Ana", "ana@cine.test");
        when(userRepository.findById(7L)).thenReturn(Optional.of(usuario));

        assertThat(userService.findByNameOrId("007")).containsExactly(usuario);
    }

    @Test
    void dadoNomeComMaiusculas_quandoBuscar_entaoEncaminhaConsulta() {
        User usuario = usuario(7L, "Ana", "ana@cine.test");
        when(userRepository.findByNomeContainingIgnoreCase("ANA")).thenReturn(List.of(usuario));

        assertThat(userService.findByNameOrId("ANA")).containsExactly(usuario);
    }

    private User usuario(long id, String nome, String email) {
        User usuario = new User();
        usuario.setId(id);
        usuario.setNome(nome);
        usuario.setEmail(email);
        return usuario;
    }
}
