package com.gymapp.ms_gamificacion.controller;

import com.gymapp.ms_gamificacion.dto.EventoGamificacionDTO;
import com.gymapp.ms_gamificacion.dto.PerfilGamificacionDTO;
import com.gymapp.ms_gamificacion.service.GamificacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/gamificacion")
@RequiredArgsConstructor
public class GamificacionController {

    private final GamificacionService service;

    @GetMapping("/perfil/{miembroId}")
    public ResponseEntity<PerfilGamificacionDTO> obtenerPerfil(@PathVariable Long miembroId) {
        log.info("Petición REST: Obtener perfil de gamificación para el miembro ID {}", miembroId);
        return ResponseEntity.ok(service.obtenerPerfil(miembroId));
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<PerfilGamificacionDTO>> obtenerRanking() {
        log.info("Petición REST: Consultar ranking Top 10 de gamificación");
        return ResponseEntity.ok(service.obtenerRankingTop10());
    }

    @GetMapping("/insignias")
    public ResponseEntity<List<Map<String, String>>> listarInsignias() {
        log.info("Petición REST: Listar catálogo completo de insignias");
        return ResponseEntity.ok(service.obtenerCatalogoInsignias());
    }

    @PostMapping("/eventos")
    public ResponseEntity<PerfilGamificacionDTO> registrarEvento(@Valid @RequestBody EventoGamificacionDTO evento) {
        log.info("Petición REST: Registrar evento '{}' para el miembro ID {}", evento.getAccion(), evento.getMiembroId());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.procesarEvento(evento));
    }

    @PatchMapping("/admin/ajuste-puntos")
    public ResponseEntity<PerfilGamificacionDTO> ajustarPuntosManual(
            @RequestParam Long miembroId,
            @RequestParam int cantidad) {
        log.info("Petición REST (Admin): Ajuste manual de {} puntos para el miembro ID {}", cantidad, miembroId);
        return ResponseEntity.ok(service.ajusteManual(miembroId, cantidad));
    }

    @GetMapping("/perfil/{miembroId}/progreso")
    public ResponseEntity<Map<String, Object>> verProgresoNivel(@PathVariable Long miembroId) {
        log.info("Petición REST: Consultar progreso de nivel para el miembro ID {}", miembroId);
        return ResponseEntity.ok(service.calcularProgreso(miembroId));
    }
}