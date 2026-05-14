package com.gymapp.ms_gamificacion.repository;

import com.gymapp.ms_gamificacion.model.PerfilGamificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PerfilGamificacionRepository extends JpaRepository<PerfilGamificacion, Long> {
    Optional<PerfilGamificacion> findByMiembroId(Long miembroId);
    List<PerfilGamificacion> findTop10ByOrderByPuntosTotalesDesc();
}