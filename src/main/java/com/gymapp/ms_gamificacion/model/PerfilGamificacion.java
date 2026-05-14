package com.gymapp.ms_gamificacion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "perfiles_gamificacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerfilGamificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long miembroId;

    private int puntosTotales = 0;
    private int nivel = 1;

    @Column(length = 500)
    private String insigniasCodigos = "";

    private LocalDateTime ultimaActividad = LocalDateTime.now();
}