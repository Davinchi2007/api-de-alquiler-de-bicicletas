package com.alquiler_de_bicicletas.application.exception;

public class BicicletaNoEncontradaException extends RuntimeException {

    public BicicletaNoEncontradaException(String codigo) {
        super("No se encontró una bicicleta con el código " + codigo);
    }
}