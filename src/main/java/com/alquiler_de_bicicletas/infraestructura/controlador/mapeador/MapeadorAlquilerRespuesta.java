package com.alquiler_de_bicicletas.infraestructura.controlador.mapeador;

import org.springframework.stereotype.Component;

import com.alquiler_de_bicicletas.dominio.modelo.Alquiler;
import com.alquiler_de_bicicletas.dominio.modelo.EstadoAlquiler;
import com.alquiler_de_bicicletas.infraestructura.controlador.dto.RespuestaAlquilerDTO;

@Component
public class MapeadorAlquilerRespuesta {

    public RespuestaAlquilerDTO convertirARespuesta(Alquiler alquiler) {
        boolean finalizado = alquiler.getEstado() == EstadoAlquiler.FINALIZADO;
        return new RespuestaAlquilerDTO(
                alquiler.getCodigoBicicleta(),
                alquiler.getNombreCliente(),
                alquiler.getFechaHoraInicio(),
                alquiler.getDuracionEstimadaHoras(),
                alquiler.getTarifaBicicleta(),
                alquiler.getEstado(),
                alquiler.getFechaHoraDevolucion(),
                finalizado ? alquiler.getHorasFacturables() : null,
                finalizado ? alquiler.getCostoBase() : null,
                finalizado ? alquiler.getMulta() : null,
                finalizado ? alquiler.getTotal() : null);
    }
}