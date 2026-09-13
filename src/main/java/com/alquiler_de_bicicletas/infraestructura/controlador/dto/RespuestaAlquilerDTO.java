package com.alquiler_de_bicicletas.infraestructura.controlador.dto;

import java.time.LocalDateTime;

import com.alquiler_de_bicicletas.dominio.modelo.EstadoAlquiler;
import com.alquiler_de_bicicletas.dominio.modelo.TarifaBicicleta;

public record RespuestaAlquilerDTO(
        String codigoBicicleta,
        String nombreCliente,
        LocalDateTime fechaHoraInicio,
        Long duracionEstimadaHoras,
        TarifaBicicleta tarifaBicicleta,
        EstadoAlquiler estado,
        LocalDateTime fechaHoraDevolucion,
        Long horasFacturables,
        Long costoBase,
        Long multa,
        Long total) {
}