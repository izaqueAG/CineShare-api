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

import com.cineShare.model.User;
import com.cineShare.services.UserService;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void dadoUsuarioValido_quandoConsultarTodos_entaoRetorna200EJson() throws Exception {
        User usuario = usuario(1L, "Ana", "ana@cine.test");
        when(userService.listAll()).thenReturn(List.of(usuario));

        mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Ana"));
    }

    @Test
    void dadoRotaInexistente_quandoConsultar_entaoRetorna404() throws Exception {
        mockMvc.perform(get("/rota-inexistente"))
                .andExpect(status().isNotFound());
    }

    private User usuario(long id, String nome, String email) {
        User usuario = new User();
        usuario.setId(id);
        usuario.setNome(nome);
        usuario.setEmail(email);
        return usuario;
    }
}
