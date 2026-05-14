package com.gymapp.ms_gamificacion.service;

import com.gymapp.ms_gamificacion.dto.EventoGamificacionDTO;
import com.gymapp.ms_gamificacion.dto.PerfilGamificacionDTO;
import java.util.List;
import java.util.Map;

public interface GamificacionService {
    PerfilGamificacionDTO procesarEvento(EventoGamificacionDTO dto);
    PerfilGamificacionDTO obtenerPerfil(Long miembroId);
    List<PerfilGamificacionDTO> obtenerRankingTop10();
    Map<String, Object> calcularProgreso(Long miembroId);
    List<Map<String, String>> obtenerCatalogoInsignias();
    PerfilGamificacionDTO ajusteManual(Long miembroId, int cantidadPuntos);
}