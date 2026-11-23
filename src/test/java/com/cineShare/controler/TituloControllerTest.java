package com.cineShare.controler;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.cineShare.model.Titulo;
import com.cineShare.services.TituloService;

@WebMvcTest(controllers = TituloController.class)
class TituloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TituloService tituloService;

    @Test
    void dadoNomeValido_quandoBuscar_entaoRetorna200EId() throws Exception {
        Titulo titulo = new Titulo();
        titulo.setId(3L);
        titulo.setNome("Matrix");
        when(tituloService.getTitulo("Matrix")).thenReturn(titulo);

        mockMvc.perform(get("/titulos/buscar").param("nome", "Matrix"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Matrix"));
    }

    @Test
    void dadoParametroAusente_quandoBuscar_entaoRetorna400() throws Exception {
        mockMvc.perform(get("/titulos/buscar"))
                .andExpect(status().isBadRequest());
    }

}
