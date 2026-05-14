package com.gymapp.ms_gamificacion.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventoGamificacionDTO {

    @NotNull(message = "El ID del miembro es obligatorio")
    @Positive(message = "El ID del miembro debe ser un número positivo")
    private Long miembroId;

    @NotBlank(message = "La acción es obligatoria")
    @Size(min = 5, max = 50, message = "La acción debe tener entre 5 y 50 caracteres (Ej: COMPRA_TIENDA)")
    private String accion;

    @NotNull(message = "Los puntos base son obligatorios")
    @Min(value = 1, message = "El evento debe otorgar al menos 1 punto")
    @Max(value = 1000, message = "No se pueden otorgar más de 1000 puntos por evento (protección anti-fraude)")
    private Integer puntosBase;
}