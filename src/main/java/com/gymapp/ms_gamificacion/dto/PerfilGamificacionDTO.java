package com.gymapp.ms_gamificacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerfilGamificacionDTO {

    private Long miembroId;


    private Integer puntosTotales;

    private Integer nivelActual;


    private String nombreRango;


    private String mensajeProgreso;


    private Integer puntosParaSiguienteNivel;


    private Double porcentajeProgreso;

    private List<String> insigniasObtenidas;
}