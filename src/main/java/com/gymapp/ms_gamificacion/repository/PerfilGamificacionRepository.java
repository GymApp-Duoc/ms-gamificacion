package com.gymapp.ms_gamificacion.repository;

import com.gymapp.ms_gamificacion.model.PerfilGamificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PerfilGamificacionRepository extends JpaRepository<PerfilGamificacion, Long> {

    Optional<PerfilGamificacion> findByMiembroId(Long miembroId);
    List<PerfilGamificacion> findTop10ByOrderByPuntosTotalesDesc();



    @Query("SELECT p FROM PerfilGamificacion p WHERE p.nivel = :nivel")
    List<PerfilGamificacion> findByNivel(@Param("nivel") int nivel);

    @Query("SELECT COUNT(p) FROM PerfilGamificacion p WHERE p.nivel = :nivel")
    long countByNivel(@Param("nivel") int nivel);

    @Query("SELECT p FROM PerfilGamificacion p WHERE p.ultimaActividad >= :fechaLimite")
    List<PerfilGamificacion> findActivosRecientemente(@Param("fechaLimite") LocalDateTime fechaLimite);

    @Query("SELECT p FROM PerfilGamificacion p WHERE p.insigniasCodigos LIKE %:insignia%")
    List<PerfilGamificacion> buscarPorInsignia(@Param("insignia") String insignia);

    @Query("SELECT AVG(p.puntosTotales) FROM PerfilGamificacion p")
    Double calcularPromedioPuntosGlobal();
}