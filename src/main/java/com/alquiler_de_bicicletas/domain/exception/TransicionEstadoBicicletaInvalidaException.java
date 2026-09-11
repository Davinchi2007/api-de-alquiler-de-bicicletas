package com.alquiler_de_bicicletas.domain.exception;

public class TransicionEstadoBicicletaInvalidaException extends RuntimeException {

    public TransicionEstadoBicicletaInvalidaException(String message) {
        super(message);
    }
}