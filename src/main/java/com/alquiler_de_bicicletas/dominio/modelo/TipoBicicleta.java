package com.alquiler_de_bicicletas.dominio.modelo;

public enum TipoBicicleta {
    URBANA,
    MONTAÑA,
    ELÉCTRICA;

    public TarifaBicicleta obtenerTarifa() {
        return switch (this) {
            case URBANA -> TarifaBicicleta.URBANA;
            case MONTAÑA -> TarifaBicicleta.MONTAÑA;
            case ELÉCTRICA -> TarifaBicicleta.ELÉCTRICA;
        };
    }
}