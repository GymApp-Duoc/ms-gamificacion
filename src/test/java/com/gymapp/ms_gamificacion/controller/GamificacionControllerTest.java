package com.gymapp.ms_gamificacion.controller;

import com.gymapp.ms_gamificacion.assembler.PerfilGamificacionModelAssembler;
import com.gymapp.ms_gamificacion.dto.PerfilGamificacionDTO;
import com.gymapp.ms_gamificacion.service.GamificacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GamificacionController.class)
@AutoConfigureMockMvc(addFilters = false)
class GamificacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GamificacionService service;

    @MockitoBean
    private PerfilGamificacionModelAssembler assembler;

    private PerfilGamificacionDTO dto;

    @BeforeEach
    void setUp() {
        dto = new PerfilGamificacionDTO(100L, 500, 2, "Guerrero", "Mensaje", 500, 0.0, List.of("COMPRADOR"));
        when(assembler.toModel(any(PerfilGamificacionDTO.class)))
                .thenAnswer(invocation -> EntityModel.of((PerfilGamificacionDTO) invocation.getArgument(0)));
    }

    @Test
    void obtenerPerfil_Retorna200() throws Exception {
        when(service.obtenerPerfil(100L)).thenReturn(dto);

        mockMvc.perform(get("/api/gamificacion/perfil/100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.puntosTotales").value(500))
                .andExpect(jsonPath("$.nivelActual").value(2));
    }
}
