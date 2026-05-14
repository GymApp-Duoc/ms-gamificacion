package com.gymapp.ms_gamificacion.service;

import com.gymapp.ms_gamificacion.dto.EventoGamificacionDTO;
import com.gymapp.ms_gamificacion.dto.PerfilGamificacionDTO;
import com.gymapp.ms_gamificacion.exception.BusinessException;
import com.gymapp.ms_gamificacion.model.PerfilGamificacion;
import com.gymapp.ms_gamificacion.repository.PerfilGamificacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GamificacionServiceImpl implements GamificacionService {

    private final PerfilGamificacionRepository perfilRepo;
    private final RestTemplate restTemplate;

    @Value("${ms.miembros.url}")
    private String miembrosUrl;

    @Value("${ms.notificaciones.url}")
    private String notificacionesUrl;

    @Override
    @Transactional
    public PerfilGamificacionDTO procesarEvento(EventoGamificacionDTO dto) {
        PerfilGamificacion perfil = perfilRepo.findByMiembroId(dto.getMiembroId())
                .orElseGet(() -> crearPerfilNuevo(dto.getMiembroId()));

        double multiplicador = obtenerMultiplicadorPorPlan(dto.getMiembroId());
        int puntosGanados = (int) (dto.getPuntosBase() * multiplicador);

        perfil.setPuntosTotales(perfil.getPuntosTotales() + puntosGanados);

        // Lógica de Nivel: 1 nivel cada 500 puntos
        int nuevoNivel = (perfil.getPuntosTotales() / 500) + 1;

        if (nuevoNivel > perfil.getNivel()) {
            perfil.setNivel(nuevoNivel);
            enviarNotificacion(dto.getMiembroId(), "¡Subiste de Nivel!",
                    "Felicidades, ahora eres nivel " + nuevoNivel);
        }

        procesarInsignias(perfil, dto.getAccion());
        perfil.setUltimaActividad(LocalDateTime.now());
        perfilRepo.save(perfil);

        return construirDTOCompleto(perfil);
    }

    @Override
    public PerfilGamificacionDTO obtenerPerfil(Long miembroId) {
        PerfilGamificacion perfil = perfilRepo.findByMiembroId(miembroId)
                .orElseThrow(() -> new BusinessException("Perfil no encontrado"));
        return construirDTOCompleto(perfil);
    }

    @Override
    public List<PerfilGamificacionDTO> obtenerRankingTop10() {
        return perfilRepo.findTop10ByOrderByPuntosTotalesDesc().stream()
                .map(this::construirDTOCompleto)
                .collect(Collectors.toList());
    }



    private PerfilGamificacionDTO construirDTOCompleto(PerfilGamificacion perfil) {
        int puntos = perfil.getPuntosTotales();
        int nivel = perfil.getNivel();
        int puntosSiguienteNivel = nivel * 500;
        int puntosEnNivelActual = puntos % 500;
        double progreso = (puntosEnNivelActual / 500.0) * 100;

        List<String> insignias = Arrays.stream(Optional.ofNullable(perfil.getInsigniasCodigos()).orElse("").split(","))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        return PerfilGamificacionDTO.builder()
                .miembroId(perfil.getMiembroId())
                .puntosTotales(puntos)
                .nivelActual(nivel)
                .nombreRango(asignarRango(nivel))
                .mensajeProgreso("¡Estás a " + (500 - puntosEnNivelActual) + " puntos del nivel " + (nivel + 1) + "!")
                .puntosParaSiguienteNivel(500 - puntosEnNivelActual)
                .porcentajeProgreso(Math.round(progreso * 100.0) / 100.0)
                .insigniasObtenidas(insignias)
                .build();
    }

    private String asignarRango(int nivel) {
        if (nivel < 5) return "Novato";
        if (nivel < 15) return "Guerrero";
        if (nivel < 30) return "Élite";
        return "Leyenda";
    }

    private void procesarInsignias(PerfilGamificacion perfil, String accion) {
        String codigos = Optional.ofNullable(perfil.getInsigniasCodigos()).orElse("");
        String nuevaInsignia = "";

        if (accion.equals("COMPRA_TIENDA") && !codigos.contains("COMPRADOR")) {
            nuevaInsignia = "COMPRADOR_COMPULSIVO";
        } else if (accion.equals("ASISTENCIA_CLASE") && !codigos.contains("MADRUGADOR")) {
            nuevaInsignia = "GUERRERO_MADRUGADOR";
        }

        if (!nuevaInsignia.isEmpty()) {
            perfil.setInsigniasCodigos(codigos + nuevaInsignia + ",");
            enviarNotificacion(perfil.getMiembroId(), "Nueva Insignia", "Ganaste: " + nuevaInsignia);
        }
    }



    @Override
    public Map<String, Object> calcularProgreso(Long miembroId) {
        return new HashMap<>();
    }

    @Override
    public List<Map<String, String>> obtenerCatalogoInsignias() {
        return List.of(Map.of("codigo", "COMPRADOR", "nombre", "Comprador"));
    }

    @Override
    @Transactional
    public PerfilGamificacionDTO ajusteManual(Long miembroId, int cantidadPuntos) {
        PerfilGamificacion perfil = perfilRepo.findByMiembroId(miembroId).orElseThrow();
        perfil.setPuntosTotales(perfil.getPuntosTotales() + cantidadPuntos);
        return construirDTOCompleto(perfilRepo.save(perfil));
    }

    private void enviarNotificacion(Long miembroId, String titulo, String mensaje) {
        try {
            Map<String, Object> req = Map.of("miembroId", miembroId, "titulo", titulo, "mensaje", mensaje);
            restTemplate.postForObject(notificacionesUrl + "/api/notificaciones", req, Object.class);
        } catch (Exception e) { log.error("Error notificaciones"); }
    }

    private PerfilGamificacion crearPerfilNuevo(Long miembroId) {
        PerfilGamificacion p = new PerfilGamificacion();
        p.setMiembroId(miembroId);
        p.setNivel(1);
        p.setPuntosTotales(0);
        p.setInsigniasCodigos("");
        return perfilRepo.save(p);
    }

    private double obtenerMultiplicadorPorPlan(Long miembroId) {
        try {
            String plan = restTemplate.getForObject(miembrosUrl + "/api/miembros/plan/" + miembroId, String.class);
            return "VIP".equalsIgnoreCase(plan) ? 1.5 : 1.0;
        } catch (Exception e) { return 1.0; }
    }
}