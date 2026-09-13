package com.alquiler_de_bicicletas.dominio.excepcion;

public class AlquilerYaFinalizadoException extends RuntimeException {

    public AlquilerYaFinalizadoException(String mensaje) {
        super(mensaje);
    }
}