package com.alquiler_de_bicicletas.infrastructure.controller.mapper;

import org.springframework.stereotype.Component;

import com.alquiler_de_bicicletas.domain.model.Bicicleta;
import com.alquiler_de_bicicletas.infrastructure.controller.dto.BicicletaResponse;

@Component
public class BicicletaDtoMapper {

    public BicicletaResponse toResponse(Bicicleta bicicleta) {
        return new BicicletaResponse(bicicleta.getCodigo(), bicicleta.getTipo(), bicicleta.getEstado());
    }
}