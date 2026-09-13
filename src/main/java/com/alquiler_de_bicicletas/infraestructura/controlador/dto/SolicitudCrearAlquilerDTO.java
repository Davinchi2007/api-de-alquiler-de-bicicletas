package com.alquiler_de_bicicletas.infraestructura.controlador.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record SolicitudCrearAlquilerDTO(
        @NotBlank(message = "El código de bicicleta es obligatorio")
        @Size(max = 50, message = "El código de bicicleta no puede superar 50 caracteres")
        String codigoBicicleta,
        @NotBlank(message = "El nombre del cliente es obligatorio")
        @Size(max = 150, message = "El nombre del cliente no puede superar 150 caracteres")
        String nombreCliente,
        @NotNull(message = "La fecha de inicio es obligatoria")
        LocalDateTime fechaHoraInicio,
        @NotNull(message = "La duración estimada es obligatoria")
        @Positive(message = "La duración estimada debe ser mayor que cero")
        Long duracionEstimadaHoras) {
}