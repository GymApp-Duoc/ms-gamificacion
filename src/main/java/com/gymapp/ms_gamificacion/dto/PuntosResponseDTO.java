package com.gymapp.ms_gamificacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PuntosResponseDTO {
    private Long miembroId;
    private Integer puntosGanados;
    private Integer puntosTotalesActuales;
    private String mensaje;
    private LocalDateTime fechaProcesamiento;
}