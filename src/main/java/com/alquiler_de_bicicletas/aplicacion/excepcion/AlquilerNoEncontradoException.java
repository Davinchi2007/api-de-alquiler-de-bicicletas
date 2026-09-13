package com.alquiler_de_bicicletas.aplicacion.excepcion;

public class AlquilerNoEncontradoException extends RuntimeException {

    public AlquilerNoEncontradoException(String codigoBicicleta) {
        super("No se encontró un alquiler activo para la bicicleta " + codigoBicicleta);
    }
}