package com.gymapp.ms_gamificacion.controller;

import com.gymapp.ms_gamificacion.dto.EventoGamificacionDTO;
import com.gymapp.ms_gamificacion.dto.PerfilGamificacionDTO;
import com.gymapp.ms_gamificacion.service.GamificacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gamificacion")
@RequiredArgsConstructor
public class GamificacionController {

    private final GamificacionService service;


    @GetMapping("/perfil/{miembroId}")
    public ResponseEntity<PerfilGamificacionDTO> obtenerPerfil(@PathVariable Long miembroId) {
        return ResponseEntity.ok(service.obtenerPerfil(miembroId));
    }


    @GetMapping("/ranking")
    public ResponseEntity<List<PerfilGamificacionDTO>> obtenerRanking() {
        return ResponseEntity.ok(service.obtenerRankingTop10());
    }


    @GetMapping("/insignias")
    public ResponseEntity<List<Map<String, String>>> listarInsignias() {
        return ResponseEntity.ok(service.obtenerCatalogoInsignias());
    }


    @PostMapping("/eventos")
    public ResponseEntity<PerfilGamificacionDTO> registrarEvento(@Valid @RequestBody EventoGamificacionDTO evento) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.procesarEvento(evento));
    }


    @PatchMapping("/admin/ajuste-puntos")
    public ResponseEntity<PerfilGamificacionDTO> ajustarPuntosManual(
            @RequestParam Long miembroId,
            @RequestParam int cantidad) {
        return ResponseEntity.ok(service.ajusteManual(miembroId, cantidad));
    }


    @GetMapping("/perfil/{miembroId}/progreso")
    public ResponseEntity<Map<String, Object>> verProgresoNivel(@PathVariable Long miembroId) {
        return ResponseEntity.ok(service.calcularProgreso(miembroId));
    }
}