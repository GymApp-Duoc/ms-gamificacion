package com.gymapp.ms_gamificacion.service;

import com.gymapp.ms_gamificacion.client.MiembroClient;
import com.gymapp.ms_gamificacion.client.NotificacionClient;
import com.gymapp.ms_gamificacion.dto.EventoGamificacionDTO;
import com.gymapp.ms_gamificacion.dto.PerfilGamificacionDTO;
import com.gymapp.ms_gamificacion.model.PerfilGamificacion;
import com.gymapp.ms_gamificacion.repository.PerfilGamificacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GamificacionServiceTest {

    @Mock
    private PerfilGamificacionRepository perfilRepo;

    @Mock
    private MiembroClient miembroClient;

    @Mock
    private NotificacionClient notificacionClient;

    @InjectMocks
    private GamificacionServiceImpl service;

    private PerfilGamificacion perfilExistente;

    @BeforeEach
    void setUp() {
        perfilExistente = new PerfilGamificacion(1L, 100L, 100, 1, "", LocalDateTime.now());
    }

    @Test
    void procesarEvento_SumaPuntosCorrectamente() {
        EventoGamificacionDTO evento = new EventoGamificacionDTO(100L, "COMPRA_TIENDA", 200);
        when(perfilRepo.findByMiembroId(100L)).thenReturn(Optional.of(perfilExistente));
        when(miembroClient.obtenerPlan(anyLong())).thenReturn("ESTANDAR");
        when(perfilRepo.save(any(PerfilGamificacion.class))).thenReturn(perfilExistente);

        PerfilGamificacionDTO resultado = service.procesarEvento(evento);

        assertNotNull(resultado);
        assertEquals(300, resultado.getPuntosTotales());
        assertEquals(1, resultado.getNivelActual());
    }
}