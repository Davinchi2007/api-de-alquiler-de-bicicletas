package com.alquiler_de_bicicletas.infraestructura.controlador.mapeador;

import org.springframework.stereotype.Component;

import com.alquiler_de_bicicletas.dominio.modelo.Bicicleta;
import com.alquiler_de_bicicletas.infraestructura.controlador.dto.RespuestaBicicletaDTO;

@Component
public class MapeadorBicicletaRespuesta {

    public RespuestaBicicletaDTO convertirARespuesta(Bicicleta bicicleta) {
        return new RespuestaBicicletaDTO(bicicleta.getCodigo(), bicicleta.getTipo(), bicicleta.getEstado());
    }
}