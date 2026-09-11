package com.alquiler_de_bicicletas.dominio.modelo;

import java.util.Objects;

import com.alquiler_de_bicicletas.dominio.excepcion.TransicionEstadoBicicletaInvalidaException;

public class Bicicleta {

    private final String codigo;
    private final TipoBicicleta tipo;
    private EstadoBicicleta estado;

    public Bicicleta(String codigo, TipoBicicleta tipo, EstadoBicicleta estado) {
        this.codigo = validarCodigo(codigo);
        this.tipo = Objects.requireNonNull(tipo, "El tipo de bicicleta es obligatorio");
        this.estado = Objects.requireNonNull(estado, "El estado de bicicleta es obligatorio");
    }

    public void alquilar() {
        if (estado != EstadoBicicleta.DISPONIBLE) {
            throw new TransicionEstadoBicicletaInvalidaException(
                    "La bicicleta " + codigo + " no puede alquilarse porque está " + estado);
        }

        estado = EstadoBicicleta.ALQUILADA;
    }

    public void devolver() {
        if (estado != EstadoBicicleta.ALQUILADA) {
            throw new TransicionEstadoBicicletaInvalidaException(
                    "La bicicleta " + codigo + " no puede devolverse porque está " + estado);
        }

        estado = EstadoBicicleta.DISPONIBLE;
    }

    public String getCodigo() {
        return codigo;
    }

    public TipoBicicleta getTipo() {
        return tipo;
    }

    public EstadoBicicleta getEstado() {
        return estado;
    }

    private static String validarCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código de bicicleta es obligatorio y no puede estar vacío");
        }

        return codigo;
    }
}