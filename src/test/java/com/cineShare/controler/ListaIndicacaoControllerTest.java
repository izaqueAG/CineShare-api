package com.cineShare.controler;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.cineShare.model.ListaIndicacao;
import com.cineShare.services.ListaIndicacaoService;

@WebMvcTest(controllers = ListaIndicacaoController.class)
class ListaIndicacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ListaIndicacaoService listaService;

    @Test
    void dadoListasExistentes_quandoConsultar_entaoRetorna200EJson() throws Exception {
        ListaIndicacao lista = new ListaIndicacao();
        lista.setId(1L);
        lista.setNome("Favoritos");
        when(listaService.listAll()).thenReturn(List.of(lista));

        mockMvc.perform(get("/listas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Favoritos"));
    }

    @Test
    void dadoJsonValido_quandoCriar_entaoRetorna200EId() throws Exception {
        ListaIndicacao lista = new ListaIndicacao();
        lista.setId(1L);
        lista.setNome("Favoritos");
        when(listaService.criarLista(org.mockito.ArgumentMatchers.any())).thenReturn(lista);

        mockMvc.perform(post("/listas/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Favoritos\",\"userId\":1,\"titulos\":[\"Matrix\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

}
