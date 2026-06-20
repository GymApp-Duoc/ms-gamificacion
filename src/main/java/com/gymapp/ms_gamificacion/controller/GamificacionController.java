package com.gymapp.ms_gamificacion.controller;

import com.gymapp.ms_gamificacion.assembler.PerfilGamificacionModelAssembler;
import com.gymapp.ms_gamificacion.dto.EventoGamificacionDTO;
import com.gymapp.ms_gamificacion.dto.PerfilGamificacionDTO;
import com.gymapp.ms_gamificacion.service.GamificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequestMapping("/api/gamificacion")
@RequiredArgsConstructor
@Tag(name = "Gamificación", description = "API para gestión de puntos, niveles y rankings")
public class GamificacionController {

    private final GamificacionService service;
    private final PerfilGamificacionModelAssembler assembler;

    @GetMapping("/perfil/{miembroId}")
    @Operation(summary = "Obtener Perfil", description = "Retorna puntos y nivel actual de un miembro")
    public ResponseEntity<EntityModel<PerfilGamificacionDTO>> obtenerPerfil(@PathVariable Long miembroId) {
        return ResponseEntity.ok(assembler.toModel(service.obtenerPerfil(miembroId)));
    }

    @GetMapping("/ranking")
    @Operation(summary = "Ver Top 10 Global")
    public ResponseEntity<CollectionModel<EntityModel<PerfilGamificacionDTO>>> obtenerRanking() {
        List<EntityModel<PerfilGamificacionDTO>> lista = service.obtenerRankingTop10().stream()
                .map(assembler::toModel).collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(lista, linkTo(methodOn(GamificacionController.class).obtenerRanking()).withSelfRel()));
    }

    @GetMapping("/insignias")
    @Operation(summary = "Catálogo de Insignias")
    public ResponseEntity<List<Map<String, String>>> listarInsignias() {
        return ResponseEntity.ok(service.obtenerCatalogoInsignias());
    }

    @PostMapping("/eventos")
    @Operation(summary = "Registrar Puntos", description = "Procesa un evento y suma puntos (Valida VIP en ms-miembros)")
    public ResponseEntity<EntityModel<PerfilGamificacionDTO>> registrarEvento(@Valid @RequestBody EventoGamificacionDTO evento) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(service.procesarEvento(evento)));
    }

    @PatchMapping("/admin/ajuste-puntos")
    @Operation(summary = "Ajuste manual de puntos (Admin)")
    public ResponseEntity<EntityModel<PerfilGamificacionDTO>> ajustarPuntosManual(
            @RequestParam Long miembroId, @RequestParam int cantidad) {
        return ResponseEntity.ok(assembler.toModel(service.ajusteManual(miembroId, cantidad)));
    }

    @GetMapping("/perfil/{miembroId}/progreso")
    public ResponseEntity<Map<String, Object>> verProgresoNivel(@PathVariable Long miembroId) {
        return ResponseEntity.ok(service.calcularProgreso(miembroId));
    }



    @GetMapping("/reportes/nivel/{nivel}")
    @Operation(summary = "Reporte 1: Buscar jugadores por Nivel")
    public ResponseEntity<CollectionModel<EntityModel<PerfilGamificacionDTO>>> reportePorNivel(@PathVariable int nivel) {
        List<EntityModel<PerfilGamificacionDTO>> lista = service.reportePorNivel(nivel).stream()
                .map(assembler::toModel).collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(lista));
    }

    @GetMapping("/reportes/nivel/{nivel}/conteo")
    @Operation(summary = "Reporte 2: Contar jugadores en un Nivel")
    public ResponseEntity<Long> reporteConteoNivel(@PathVariable int nivel) {
        return ResponseEntity.ok(service.reporteConteoPorNivel(nivel));
    }

    @GetMapping("/reportes/actividad-reciente")
    @Operation(summary = "Reporte 3: Jugadores activos (últimos 7 días)")
    public ResponseEntity<CollectionModel<EntityModel<PerfilGamificacionDTO>>> reporteActividad() {
        List<EntityModel<PerfilGamificacionDTO>> lista = service.reporteActividadReciente().stream()
                .map(assembler::toModel).collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(lista));
    }

    @GetMapping("/reportes/insignia/{insignia}")
    @Operation(summary = "Reporte 4: Filtrar por Insignia obtenida")
    public ResponseEntity<CollectionModel<EntityModel<PerfilGamificacionDTO>>> reporteInsignia(@PathVariable String insignia) {
        List<EntityModel<PerfilGamificacionDTO>> lista = service.reportePorInsignia(insignia).stream()
                .map(assembler::toModel).collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(lista));
    }

    @GetMapping("/reportes/promedio-sistema")
    @Operation(summary = "Reporte 5: Promedio global de puntos")
    public ResponseEntity<Double> reportePromedio() {
        return ResponseEntity.ok(service.reportePromedioPuntos());
    }
}