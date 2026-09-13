package com.alquiler_de_bicicletas.infraestructura.controlador.mapeador;

import java.time.Duration;

import org.springframework.stereotype.Component;

import com.alquiler_de_bicicletas.dominio.modelo.Alquiler;
import com.alquiler_de_bicicletas.dominio.modelo.EstadoAlquiler;
import com.alquiler_de_bicicletas.infraestructura.controlador.dto.RespuestaHistorialAlquilerDTO;

@Component
public class MapeadorHistorialAlquiler {

    public RespuestaHistorialAlquilerDTO convertirARespuesta(Alquiler alquiler) {
        Long duracionRealMinutos = alquiler.getFechaHoraDevolucion() == null
                ? null
                : Duration.between(alquiler.getFechaHoraInicio(), alquiler.getFechaHoraDevolucion()).toMinutes();
        boolean tuvoMulta = alquiler.getMulta() > 0;
        boolean finalizado = alquiler.getEstado() == EstadoAlquiler.FINALIZADO;

        return new RespuestaHistorialAlquilerDTO(
                alquiler.getNombreCliente(),
                alquiler.getFechaHoraInicio(),
                alquiler.getFechaHoraDevolucion(),
                duracionRealMinutos,
                finalizado ? alquiler.getTotal() : null,
                tuvoMulta);
    }
}