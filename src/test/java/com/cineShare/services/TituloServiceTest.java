package com.cineShare.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.cineShare.model.Titulo;
import com.cineShare.repository.TituloRepository;

@ExtendWith(MockitoExtension.class)
class TituloServiceTest {

    @Mock
    private TituloRepository tituloRepository;

    @Test
    void dadoRespostaTmdbValida_quandoBuscar_entaoPersisteTituloMapeado() throws Exception {
        TituloService service = service();
        HttpClient client = mock(HttpClient.class);
        HttpResponse<String> response = response(200, "{\"results\":[{\"title\":\"Matrix\",\"release_date\":\"1999-03-31\"}]}");
        doReturn(response).when(client).send(any(), any());
        Titulo salvo = titulo("Matrix");
        salvo.setAnoLancamento(LocalDate.of(1999, 3, 31));
        when(tituloRepository.findByNome("Matrix")).thenReturn(List.of());
        when(tituloRepository.save(any(Titulo.class))).thenReturn(salvo);

        try (MockedStatic<HttpClient> httpClient = mockStatic(HttpClient.class)) {
            httpClient.when(HttpClient::newHttpClient).thenReturn(client);

            Titulo resultado = service.getTitulo("Matrix");

            assertThat(resultado).isSameAs(salvo);
            assertThat(resultado.getNome()).isEqualTo("Matrix");
            assertThat(resultado.getAnoLancamento()).isEqualTo(LocalDate.of(1999, 3, 31));
        }
    }

    @Test
    void dadoTituloJaPersistido_quandoBuscar_entaoRetornaRegistroExistente() throws Exception {
        TituloService service = service();
        HttpClient client = mock(HttpClient.class);
        doReturn(response(200, "{\"results\":[{\"title\":\"Matrix\",\"release_date\":\"1999-03-31\"}]}" )).when(client).send(any(), any());
        Titulo existente = titulo("Matrix");
        when(tituloRepository.findByNome("Matrix")).thenReturn(List.of(existente));

        try (MockedStatic<HttpClient> httpClient = mockStatic(HttpClient.class)) {
            httpClient.when(HttpClient::newHttpClient).thenReturn(client);

            Titulo resultado = service.getTitulo("Matrix");

            assertThat(resultado).isSameAs(existente);
        }
    }

    @Test
    void dadoTmdbComStatusNaoSucesso_quandoBuscar_entaoInformaErro() throws Exception {
        TituloService service = service();
        HttpClient client = mock(HttpClient.class);
        doReturn(response(401, "{}")).when(client).send(any(), any());

        try (MockedStatic<HttpClient> httpClient = mockStatic(HttpClient.class)) {
            httpClient.when(HttpClient::newHttpClient).thenReturn(client);

            assertThatThrownBy(() -> service.getTitulo("Matrix"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Erro na API TMDB: 401");
        }
    }

    @Test
    void dadoTmdbSemResultados_quandoBuscar_entaoInformaTituloNaoEncontrado() throws Exception {
        TituloService service = service();
        HttpClient client = mock(HttpClient.class);
        doReturn(response(200, "{\"results\":[]}")).when(client).send(any(), any());

        try (MockedStatic<HttpClient> httpClient = mockStatic(HttpClient.class)) {
            httpClient.when(HttpClient::newHttpClient).thenReturn(client);

            assertThatThrownBy(() -> service.getTitulo("Inexistente"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Título não encontrado: Inexistente");
        }
    }

    @Test
    void dadoTitulosPersistidos_quandoListarTodos_entaoRetornaTitulosDoRepositorio() {
        Titulo titulo = titulo("Matrix");
        when(tituloRepository.findAll()).thenReturn(List.of(titulo));

        assertThat(service().findAll()).containsExactly(titulo);
    }

    @Test
    void dadoTituloParaSalvar_quandoSalvar_entaoRetornaTituloPersistido() {
        Titulo titulo = titulo("Matrix");
        when(tituloRepository.save(titulo)).thenReturn(titulo);

        assertThat(service().salvar(titulo)).isSameAs(titulo);
    }

    @Test
    void dadoRespostaTmdbSemData_quandoBuscar_entaoPersisteTituloSemData() throws Exception {
        TituloService service = service();
        HttpClient client = mock(HttpClient.class);
        doReturn(response(200, "{\"results\":[{\"title\":\"Matrix\",\"release_date\":\"\"}]}" )).when(client).send(any(), any());
        when(tituloRepository.findByNome("Matrix")).thenReturn(List.of());
        when(tituloRepository.save(any(Titulo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        try (MockedStatic<HttpClient> httpClient = mockStatic(HttpClient.class)) {
            httpClient.when(HttpClient::newHttpClient).thenReturn(client);

            assertThat(service.getTitulo("Matrix").getAnoLancamento()).isNull();
        }
    }

    @Test
    void dadoRespostaTmdbComResultado_quandoBuscarComEspacos_entaoRetornaResultado() throws Exception {
        TituloService service = service();
        HttpClient client = mock(HttpClient.class);
        doReturn(response(200, "{\"results\":[{\"title\":\"Matrix\",\"release_date\":\"1999-03-31\"}]}" )).when(client).send(any(), any());
        when(tituloRepository.findByNome("Matrix")).thenReturn(List.of());
        when(tituloRepository.save(any(Titulo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        try (MockedStatic<HttpClient> httpClient = mockStatic(HttpClient.class)) {
            httpClient.when(HttpClient::newHttpClient).thenReturn(client);

            assertThat(service.getTitulo("  Matrix  ").getNome()).isEqualTo("Matrix");
        }
    }

    @Test
    void dadoTmdbComRespostaNula_quandoBuscar_entaoInformaTituloNaoEncontrado() throws Exception {
        TituloService service = service();
        HttpClient client = mock(HttpClient.class);
        doReturn(response(200, "{\"results\":null}")).when(client).send(any(), any());

        try (MockedStatic<HttpClient> httpClient = mockStatic(HttpClient.class)) {
            httpClient.when(HttpClient::newHttpClient).thenReturn(client);

            assertThatThrownBy(() -> service.getTitulo("Matrix"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Título não encontrado: Matrix");
        }
    }

    @Test
    void dadoTmdbComTituloSemData_quandoBuscar_entaoDefinePlataformaPadrao() throws Exception {
        TituloService service = service();
        HttpClient client = mock(HttpClient.class);
        doReturn(response(200, "{\"results\":[{\"title\":\"Matrix\",\"release_date\":\"\"}]}" )).when(client).send(any(), any());
        when(tituloRepository.findByNome("Matrix")).thenReturn(List.of());
        when(tituloRepository.save(any(Titulo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        try (MockedStatic<HttpClient> httpClient = mockStatic(HttpClient.class)) {
            httpClient.when(HttpClient::newHttpClient).thenReturn(client);

            assertThat(service.getTitulo("Matrix").getPlataforma()).isEqualTo("Em desenvolvimento");
        }
    }

    @Test
    void dadoTmdbComRespostaValida_quandoBuscar_entaoConsultaTituloPeloNomeRetornado() throws Exception {
        TituloService service = service();
        HttpClient client = mock(HttpClient.class);
        doReturn(response(200, "{\"results\":[{\"title\":\"Matrix\",\"release_date\":\"1999-03-31\"}]}" )).when(client).send(any(), any());
        when(tituloRepository.findByNome("Matrix")).thenReturn(List.of());
        when(tituloRepository.save(any(Titulo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        try (MockedStatic<HttpClient> httpClient = mockStatic(HttpClient.class)) {
            httpClient.when(HttpClient::newHttpClient).thenReturn(client);

            service.getTitulo("consulta");

            org.mockito.Mockito.verify(tituloRepository).findByNome("Matrix");
        }
    }

    private TituloService service() {
        TituloService service = new TituloService(tituloRepository);
        ReflectionTestUtils.setField(service, "token", "token-de-teste");
        return service;
    }

    @SuppressWarnings("unchecked")
    private HttpResponse<String> response(int status, String body) {
        HttpResponse<String> response = mock(HttpResponse.class);
        lenient().when(response.statusCode()).thenReturn(status);
        lenient().when(response.body()).thenReturn(body);
        return response;
    }

    private Titulo titulo(String nome) {
        Titulo titulo = new Titulo();
        titulo.setNome(nome);
        return titulo;
    }
}
