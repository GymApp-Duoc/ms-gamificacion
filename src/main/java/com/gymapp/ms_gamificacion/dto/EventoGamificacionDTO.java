package com.gymapp.ms_gamificacion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Objeto para registrar una acción y otorgar puntos a un miembro")
public class EventoGamificacionDTO {

    @NotNull(message = "El ID del miembro es obligatorio")
    @Positive(message = "El ID del miembro debe ser un número positivo")
    @Schema(description = "ID del usuario que realizó la acción", example = "105")
    private Long miembroId;

    @NotBlank(message = "La acción es obligatoria")
    @Size(min = 5, max = 50, message = "La acción debe tener entre 5 y 50 caracteres")
    @Schema(description = "Código de la acción realizada", example = "COMPRA_TIENDA")
    private String accion;

    @NotNull(message = "Los puntos base son obligatorios")
    @Min(value = 1, message = "El evento debe otorgar al menos 1 punto")
    @Max(value = 1000, message = "No se pueden otorgar más de 1000 puntos por evento (protección anti-fraude)")
    @Schema(description = "Puntos netos ganados antes de multiplicadores", example = "150")
    private Integer puntosBase;
}