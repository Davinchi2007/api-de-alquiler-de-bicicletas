package com.alquiler_de_bicicletas.infraestructura.controlador.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

public record SolicitudFinalizarAlquilerDTO(
        @NotNull(message = "La fecha de devolución es obligatoria")
        LocalDateTime fechaHoraDevolucion) {
}