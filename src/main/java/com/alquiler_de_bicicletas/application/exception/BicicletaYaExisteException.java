package com.alquiler_de_bicicletas.application.exception;

public class BicicletaYaExisteException extends RuntimeException {

    public BicicletaYaExisteException(String codigo) {
        super("Ya existe una bicicleta registrada con el código " + codigo);
    }
}