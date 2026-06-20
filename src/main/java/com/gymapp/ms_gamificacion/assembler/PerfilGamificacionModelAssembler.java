package com.gymapp.ms_gamificacion.assembler;

import com.gymapp.ms_gamificacion.controller.GamificacionController;
import com.gymapp.ms_gamificacion.dto.PerfilGamificacionDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PerfilGamificacionModelAssembler implements RepresentationModelAssembler<PerfilGamificacionDTO, EntityModel<PerfilGamificacionDTO>> {

    @Override
    public EntityModel<PerfilGamificacionDTO> toModel(PerfilGamificacionDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(GamificacionController.class).obtenerPerfil(dto.getMiembroId())).withSelfRel(),
                linkTo(methodOn(GamificacionController.class).verProgresoNivel(dto.getMiembroId())).withRel("progreso_detallado")
        );
    }
}