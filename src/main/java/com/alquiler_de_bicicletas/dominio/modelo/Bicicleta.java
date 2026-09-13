package com.alquiler_de_bicicletas.dominio.modelo;

import java.util.Objects;
import com.alquiler_de_bicicletas.dominio.excepcion.TransicionEstadoBicicletaInvalidaException;

public class Bicicleta {

    private static final String MSG_CODIGO_OBLIGATORIO = "El código de bicicleta es obligatorio y no puede estar vacío";
    private static final String MSG_TIPO_OBLIGATORIO = "El tipo de bicicleta es obligatorio";
    private static final String MSG_ESTADO_OBLIGATORIO = "El estado de la bicicleta es obligatorio";
    private static final String MSG_ERROR_ALQUILAR_FORMATO = "La bicicleta %s no puede alquilarse porque está %s";
    private static final String MSG_ERROR_DEVOLVER_FORMATO = "La bicicleta %s no puede devolverse porque está %s";

    private final String codigo;
    private final TipoBicicleta tipo;
    private EstadoBicicleta estado;

    public Bicicleta(String codigo, TipoBicicleta tipo, EstadoBicicleta estado) {
        this.codigo = validarCodigo(codigo);
        this.tipo = Objects.requireNonNull(tipo, MSG_TIPO_OBLIGATORIO);
        this.estado = Objects.requireNonNull(estado, MSG_ESTADO_OBLIGATORIO);
    }

    public static Bicicleta crear(String codigo, TipoBicicleta tipo, EstadoBicicleta estado) {
        return new Bicicleta(codigo, tipo, estado);
    }

    public void alquilar() {
        if (estado != EstadoBicicleta.DISPONIBLE) {
            throw new TransicionEstadoBicicletaInvalidaException(
                    String.format(MSG_ERROR_ALQUILAR_FORMATO, codigo, estado));
        }
        estado = EstadoBicicleta.ALQUILADA;
    }

    public void devolver() {
        if (estado != EstadoBicicleta.ALQUILADA) {
            throw new TransicionEstadoBicicletaInvalidaException(
                    String.format(MSG_ERROR_DEVOLVER_FORMATO, codigo, estado));
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
            throw new IllegalArgumentException(MSG_CODIGO_OBLIGATORIO);
        }
        return codigo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bicicleta bicicleta = (Bicicleta) o;
        return Objects.equals(codigo, bicicleta.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }
}