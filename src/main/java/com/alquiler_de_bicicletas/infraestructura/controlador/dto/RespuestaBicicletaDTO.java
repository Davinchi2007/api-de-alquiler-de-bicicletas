package com.alquiler_de_bicicletas.infraestructura.controlador.dto;

import com.alquiler_de_bicicletas.dominio.modelo.EstadoBicicleta;
import com.alquiler_de_bicicletas.dominio.modelo.TipoBicicleta;

public record RespuestaBicicletaDTO(
        String codigo,
        TipoBicicleta tipo,
        EstadoBicicleta estado) {
}