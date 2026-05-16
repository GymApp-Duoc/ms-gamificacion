package com.gymapp.ms_gamificacion.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-miembros", url = "${ms.miembros.url}")
public interface MiembroClient {
    @GetMapping("/api/miembros/plan/{id}")
    String obtenerPlan(@PathVariable("id") Long id);
}