package com.gymapp.ms_gamificacion.service;

import com.gymapp.ms_gamificacion.client.MiembroClient;
import com.gymapp.ms_gamificacion.client.NotificacionClient;
import com.gymapp.ms_gamificacion.dto.EventoGamificacionDTO;
import com.gymapp.ms_gamificacion.dto.PerfilGamificacionDTO;
import com.gymapp.ms_gamificacion.exception.BusinessException;
import com.gymapp.ms_gamificacion.model.PerfilGamificacion;
import com.gymapp.ms_gamificacion.repository.PerfilGamificacionRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GamificacionServiceImpl implements GamificacionService {

    private final PerfilGamificacionRepository perfilRepo;


    private final MiembroClient miembroClient;
    private final NotificacionClient notificacionClient;

    @Override
    @Transactional
    public PerfilGamificacionDTO procesarEvento(EventoGamificacionDTO dto) {
        log.info("Procesando evento de gamificación '{}' para el miembro ID: {}", dto.getAccion(), dto.getMiembroId());

        PerfilGamificacion perfil = perfilRepo.findByMiembroId(dto.getMiembroId())
                .orElseGet(() -> crearPerfilNuevo(dto.getMiembroId()));

        double multiplicador = obtenerMultiplicadorPorPlan(dto.getMiembroId());
        int puntosGanados = (int) (dto.getPuntosBase() * multiplicador);

        perfil.setPuntosTotales(perfil.getPuntosTotales() + puntosGanados);
        log.info("Miembro {} ganó {} puntos (Multiplicador: {}x). Total acumulado: {}",
                dto.getMiembroId(), puntosGanados, multiplicador, perfil.getPuntosTotales());


        int nuevoNivel = (perfil.getPuntosTotales() / 500) + 1;

        if (nuevoNivel > perfil.getNivel()) {
            perfil.setNivel(nuevoNivel);
            log.info("¡Level Up! El miembro {} subió al nivel {}", dto.getMiembroId(), nuevoNivel);
            enviarNotificacion(dto.getMiembroId(), "¡Subiste de Nivel!",
                    "Felicidades, ahora eres nivel " + nuevoNivel);
        }

        procesarInsignias(perfil, dto.getAccion());
        perfil.setUltimaActividad(LocalDateTime.now());
        perfilRepo.save(perfil);

        return construirDTOCompleto(perfil);
    }

    @Override
    @Transactional(readOnly = true)
    public PerfilGamificacionDTO obtenerPerfil(Long miembroId) {
        log.info("Consultando perfil de gamificación para el miembro ID: {}", miembroId);
        PerfilGamificacion perfil = perfilRepo.findByMiembroId(miembroId)
                .orElseThrow(() -> new BusinessException("Perfil de gamificación no encontrado para el miembro especificado."));
        return construirDTOCompleto(perfil);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerfilGamificacionDTO> obtenerRankingTop10() {
        log.info("Generando ranking Top 10 de miembros");
        return perfilRepo.findTop10ByOrderByPuntosTotalesDesc().stream()
                .map(this::construirDTOCompleto)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> calcularProgreso(Long miembroId) {
        log.info("Calculando progreso detallado para el miembro ID: {}", miembroId);
        return new HashMap<>();
    }

    @Override
    public List<Map<String, String>> obtenerCatalogoInsignias() {
        return List.of(Map.of("codigo", "COMPRADOR", "nombre", "Comprador"));
    }

    @Override
    @Transactional
    public PerfilGamificacionDTO ajusteManual(Long miembroId, int cantidadPuntos) {
        log.info("Ajuste manual de puntos para el miembro ID: {}. Variación: {}", miembroId, cantidadPuntos);
        PerfilGamificacion perfil = perfilRepo.findByMiembroId(miembroId)
                .orElseThrow(() -> new BusinessException("No se puede ajustar puntos: Perfil no encontrado."));

        perfil.setPuntosTotales(perfil.getPuntosTotales() + cantidadPuntos);
        return construirDTOCompleto(perfilRepo.save(perfil));
    }



    private PerfilGamificacionDTO construirDTOCompleto(PerfilGamificacion perfil) {
        int puntos = perfil.getPuntosTotales();
        int nivel = perfil.getNivel();
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
            log.info("Nueva insignia desbloqueada para el miembro {}: {}", perfil.getMiembroId(), nuevaInsignia);
            enviarNotificacion(perfil.getMiembroId(), "Nueva Insignia", "Ganaste: " + nuevaInsignia);
        }
    }

    private void enviarNotificacion(Long miembroId, String titulo, String mensaje) {
        try {
            Map<String, Object> req = Map.of("miembroId", miembroId, "titulo", titulo, "mensaje", mensaje);
            notificacionClient.enviarNotificacion(req);
        } catch (Exception e) {
            log.error("Fallo al enviar notificación de gamificación al miembro {}. Detalle: {}", miembroId, e.getMessage());
        }
    }

    private PerfilGamificacion crearPerfilNuevo(Long miembroId) {
        log.info("Creando nuevo perfil de gamificación base para el miembro ID: {}", miembroId);
        PerfilGamificacion p = new PerfilGamificacion();
        p.setMiembroId(miembroId);
        p.setNivel(1);
        p.setPuntosTotales(0);
        p.setInsigniasCodigos("");
        return perfilRepo.save(p);
    }

    private double obtenerMultiplicadorPorPlan(Long miembroId) {
        try {
            String plan = miembroClient.obtenerPlan(miembroId);
            return "VIP".equalsIgnoreCase(plan) ? 1.5 : 1.0;
        } catch (FeignException e) {
            log.warn("No se pudo obtener el plan del miembro {} desde MS-MIEMBROS. Se aplicará multiplicador base 1.0", miembroId);
            return 1.0;
        }
    }
}