package com.cineShare.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cineShare.model.ListaIndicacao;
import com.cineShare.model.Titulo;
import com.cineShare.model.User;
import com.cineShare.repository.ListaIndicacaoRepository;
import com.cineShare.repository.UserRepository;

import DTO.ListaIndicacaoDTO;

@ExtendWith(MockitoExtension.class)
class ListaIndicacaoServiceTest {

    @Mock
    private ListaIndicacaoRepository listaRepository;

    @Mock
    private TituloService tituloService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ListaIndicacaoService listaService;

    @Test
    void dadoListaSemTitulos_quandoCriar_entaoRejeitaOperacao() {
        ListaIndicacaoDTO dto = dtoComTitulos(List.of());

        assertThatThrownBy(() -> listaService.criarLista(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Escolha pelo menos um título.");
    }

    @Test
    void dadoListaComQuatroTitulos_quandoCriar_entaoRejeitaOperacao() {
        ListaIndicacaoDTO dto = dtoComTitulos(List.of("A", "B", "C", "D"));

        assertThatThrownBy(() -> listaService.criarLista(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Escolha no máximo 3 títulos.");
    }

    @Test
    void dadoUsuarioInexistente_quandoCriar_entaoRejeitaOperacao() {
        ListaIndicacaoDTO dto = dtoComTitulos(List.of("A"));
        when(userRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> listaService.criarLista(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuário não encontrado.");
    }

    @Test
    void dadoUmTituloEUsuarioValido_quandoCriar_entaoAssociaAutorETitulo() {
        User usuario = usuario(10L);
        Titulo titulo = titulo("A");
        ListaIndicacaoDTO dto = dtoComTitulos(List.of("A"));
        when(userRepository.findById(10L)).thenReturn(Optional.of(usuario));
        when(tituloService.getTitulo("A")).thenReturn(titulo);
        when(listaRepository.save(any(ListaIndicacao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ListaIndicacao resultado = listaService.criarLista(dto);

        assertThat(resultado.getUser()).isSameAs(usuario);
        assertThat(resultado.getTitulos()).containsExactly(titulo);
        verify(listaRepository).save(any(ListaIndicacao.class));
    }

    @Test
    void dadoTresTitulos_quandoCriar_entaoMantemOsTresTitulos() {
        User usuario = usuario(10L);
        ListaIndicacaoDTO dto = dtoComTitulos(List.of("A", "B", "C"));
        Titulo primeiro = titulo("A");
        Titulo segundo = titulo("B");
        Titulo terceiro = titulo("C");
        when(userRepository.findById(10L)).thenReturn(Optional.of(usuario));
        when(tituloService.getTitulo("A")).thenReturn(primeiro);
        when(tituloService.getTitulo("B")).thenReturn(segundo);
        when(tituloService.getTitulo("C")).thenReturn(terceiro);
        when(listaRepository.save(any(ListaIndicacao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ListaIndicacao resultado = listaService.criarLista(dto);

        assertThat(resultado.getTitulos()).containsExactly(primeiro, segundo, terceiro);
    }

    @Test
    void dadoDoisTitulos_quandoCriar_entaoMantemAOrdemRecebida() {
        User usuario = usuario(10L);
        ListaIndicacaoDTO dto = dtoComTitulos(List.of("A", "B"));
        Titulo primeiro = titulo("A");
        Titulo segundo = titulo("B");
        when(userRepository.findById(10L)).thenReturn(Optional.of(usuario));
        when(tituloService.getTitulo("A")).thenReturn(primeiro);
        when(tituloService.getTitulo("B")).thenReturn(segundo);
        when(listaRepository.save(any(ListaIndicacao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThat(listaService.criarLista(dto).getTitulos()).containsExactly(primeiro, segundo);
    }

    @Test
    void dadoNomeDeLista_quandoCriar_entaoPreservaNome() {
        User usuario = usuario(10L);
        ListaIndicacaoDTO dto = dtoComTitulos(List.of("A"));
        when(userRepository.findById(10L)).thenReturn(Optional.of(usuario));
        when(tituloService.getTitulo("A")).thenReturn(titulo("A"));
        when(listaRepository.save(any(ListaIndicacao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThat(listaService.criarLista(dto).getNome()).isEqualTo("Favoritos");
    }

    @Test
    void dadoListasPersistidas_quandoListarTodos_entaoRetornaListasDoRepositorio() {
        ListaIndicacao lista = new ListaIndicacao();
        when(listaRepository.findAll()).thenReturn(List.of(lista));

        assertThat(listaService.listAll()).containsExactly(lista);
    }

    @Test
    void dadoTitulosNulos_quandoCriar_entaoRejeitaOperacao() {
        ListaIndicacaoDTO dto = dtoComTitulos(null);

        assertThatThrownBy(() -> listaService.criarLista(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Escolha pelo menos um título.");
    }

    @Test
    void dadoUsuarioValido_quandoCriar_entaoUsaIdInformadoParaAutor() {
        User usuario = usuario(10L);
        ListaIndicacaoDTO dto = dtoComTitulos(List.of("A"));
        when(userRepository.findById(10L)).thenReturn(Optional.of(usuario));
        when(tituloService.getTitulo("A")).thenReturn(titulo("A"));
        when(listaRepository.save(any(ListaIndicacao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThat(listaService.criarLista(dto).getUser().getId()).isEqualTo(10L);
    }

    @Test
    void dadoTituloResolvido_quandoCriar_entaoIncluiTituloNaLista() {
        User usuario = usuario(10L);
        Titulo titulo = titulo("A");
        ListaIndicacaoDTO dto = dtoComTitulos(List.of("A"));
        when(userRepository.findById(10L)).thenReturn(Optional.of(usuario));
        when(tituloService.getTitulo("A")).thenReturn(titulo);
        when(listaRepository.save(any(ListaIndicacao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThat(listaService.criarLista(dto).getTitulos()).containsExactly(titulo);
    }

    private ListaIndicacaoDTO dtoComTitulos(List<String> titulos) {
        ListaIndicacaoDTO dto = new ListaIndicacaoDTO();
        dto.setNome("Favoritos");
        dto.setUserId(10L);
        dto.setTitulos(titulos);
        return dto;
    }

    private User usuario(long id) {
        User usuario = new User();
        usuario.setId(id);
        usuario.setNome("Ana");
        return usuario;
    }

    private Titulo titulo(String nome) {
        Titulo titulo = new Titulo();
        titulo.setNome(nome);
        return titulo;
    }
}
