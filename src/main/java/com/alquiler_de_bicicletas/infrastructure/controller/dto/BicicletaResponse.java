package com.alquiler_de_bicicletas.infrastructure.controller.dto;

import com.alquiler_de_bicicletas.domain.model.EstadoBicicleta;
import com.alquiler_de_bicicletas.domain.model.TipoBicicleta;

public record BicicletaResponse(
        String codigo,
        TipoBicicleta tipo,
        EstadoBicicleta estado) {
}