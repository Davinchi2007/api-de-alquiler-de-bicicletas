package com.alquiler_de_bicicletas.infraestructura.controlador.dto;

import java.time.LocalDateTime;

public record RespuestaHistorialAlquilerDTO(
        String nombreCliente,
        LocalDateTime fechaHoraInicio,
        LocalDateTime fechaHoraDevolucion,
        Long duracionRealMinutos,
        Long costoTotal,
        boolean tuvoMulta) {
}