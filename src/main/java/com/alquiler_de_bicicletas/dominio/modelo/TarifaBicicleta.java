package com.alquiler_de_bicicletas.dominio.modelo;

public enum TarifaBicicleta {
    URBANA(3500),
    MONTAÑA(5000),
    ELÉCTRICA(7500);

    private final long valorPorHora;

    TarifaBicicleta(long valorPorHora) {
        this.valorPorHora = valorPorHora;
    }

    public long obtenerValorPorHora() {
        return valorPorHora;
    }
}