package com.alquiler_de_bicicletas.infraestructura.controlador.dto;

import com.alquiler_de_bicicletas.dominio.modelo.TipoBicicleta;
import com.alquiler_de_bicicletas.dominio.modelo.EstadoBicicleta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SolicitudCrearBicicletaDTO(
        @NotBlank(message = "El código de bicicleta es obligatorio")
        @Size(max = 50, message = "El código de bicicleta no puede superar 50 caracteres")
        String codigo,
        @NotNull(message = "El tipo de bicicleta es obligatorio")
        TipoBicicleta tipo,
        @NotNull(message = "El estado de la bicicleta es obligatorio")
        EstadoBicicleta estado) {
}