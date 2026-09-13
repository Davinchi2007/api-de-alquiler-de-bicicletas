package com.alquiler_de_bicicletas.dominio.excepcion;

public class TransicionEstadoBicicletaInvalidaException extends RuntimeException {

    public TransicionEstadoBicicletaInvalidaException(String mensaje) {
        super(mensaje);
    }
}