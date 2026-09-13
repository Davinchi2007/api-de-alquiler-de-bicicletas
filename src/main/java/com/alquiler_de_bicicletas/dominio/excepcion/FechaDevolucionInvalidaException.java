package com.alquiler_de_bicicletas.dominio.excepcion;

public class FechaDevolucionInvalidaException extends RuntimeException {

    public FechaDevolucionInvalidaException(String mensaje) {
        super(mensaje);
    }
}