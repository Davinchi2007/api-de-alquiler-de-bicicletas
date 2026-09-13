package com.alquiler_de_bicicletas.aplicacion.puerto.entrada;

import java.time.LocalDateTime;

import com.alquiler_de_bicicletas.dominio.modelo.Alquiler;

public interface PuertoEntradaIniciarAlquiler {

    Alquiler iniciarAlquiler(
            String codigoBicicleta,
            String nombreCliente,
            LocalDateTime fechaHoraInicio,
            long duracionEstimadaHoras);
}