package com.alquiler_de_bicicletas.dominio.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import com.alquiler_de_bicicletas.dominio.excepcion.TransicionEstadoBicicletaInvalidaException;

class PruebaBicicleta {

    @Test
    void deberiaCrearBicicletaCuandoLosDatosSonValidos() {
        Bicicleta bicicleta = Bicicleta.crear(
            "BIC-001", TipoBicicleta.URBANA, EstadoBicicleta.DISPONIBLE);

        assertEquals("BIC-001", bicicleta.getCodigo());
        assertEquals(TipoBicicleta.URBANA, bicicleta.getTipo());
        assertEquals(EstadoBicicleta.DISPONIBLE, bicicleta.getEstado());
    }

    @Test
    void deberiaRechazarBicicletaCuandoElCodigoEsNulo() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Bicicleta(null, TipoBicicleta.URBANA, EstadoBicicleta.DISPONIBLE));

        assertEquals("El código de bicicleta es obligatorio y no puede estar vacío", exception.getMessage());
    }

    @Test
    void deberiaRechazarBicicletaCuandoElCodigoEstaVacio() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Bicicleta("   ", TipoBicicleta.URBANA, EstadoBicicleta.DISPONIBLE));

        assertEquals("El código de bicicleta es obligatorio y no puede estar vacío", exception.getMessage());
    }

    @Test
    void deberiaRechazarBicicletaCuandoElTipoEsNulo() {
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> new Bicicleta("BIC-001", null, EstadoBicicleta.DISPONIBLE));

        assertEquals("El tipo de bicicleta es obligatorio", exception.getMessage());
    }

    @Test
    void deberiaRechazarBicicletaCuandoElEstadoEsNulo() {
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> new Bicicleta("BIC-001", TipoBicicleta.URBANA, null));

        assertEquals("El estado de la bicicleta es obligatorio", exception.getMessage());
    }

    @Test
    void deberiaAlquilarBicicletaCuandoElEstadoEsDisponible() {
        Bicicleta bicicleta = bicicletaConEstado(EstadoBicicleta.DISPONIBLE);

        bicicleta.alquilar();

        assertEquals(EstadoBicicleta.ALQUILADA, bicicleta.getEstado());
    }

    @Test
    void deberiaRechazarAlquilerCuandoElEstadoEsAlquilada() {
        Bicicleta bicicleta = bicicletaConEstado(EstadoBicicleta.ALQUILADA);

        TransicionEstadoBicicletaInvalidaException exception =
            assertThrows(TransicionEstadoBicicletaInvalidaException.class, bicicleta::alquilar);

        assertEquals(
            "La bicicleta BIC-001 no puede alquilarse porque está ALQUILADA",
            exception.getMessage());
    }

    @Test
    void deberiaRechazarAlquilerCuandoElEstadoEsEnMantenimiento() {
        Bicicleta bicicleta = bicicletaConEstado(EstadoBicicleta.EN_MANTENIMIENTO);

        TransicionEstadoBicicletaInvalidaException exception =
            assertThrows(TransicionEstadoBicicletaInvalidaException.class, bicicleta::alquilar);

        assertEquals(
            "La bicicleta BIC-001 no puede alquilarse porque está EN_MANTENIMIENTO",
            exception.getMessage());
    }

    @Test
    void deberiaDevolverBicicletaCuandoElEstadoEsAlquilada() {
        Bicicleta bicicleta = bicicletaConEstado(EstadoBicicleta.ALQUILADA);

        bicicleta.devolver();

        assertEquals(EstadoBicicleta.DISPONIBLE, bicicleta.getEstado());
    }

    @Test
    void deberiaRechazarDevolucionCuandoElEstadoEsDisponible() {
        Bicicleta bicicleta = bicicletaConEstado(EstadoBicicleta.DISPONIBLE);

        TransicionEstadoBicicletaInvalidaException exception =
            assertThrows(TransicionEstadoBicicletaInvalidaException.class, bicicleta::devolver);

        assertEquals(
            "La bicicleta BIC-001 no puede devolverse porque está DISPONIBLE",
            exception.getMessage());
    }

    @Test
    void deberiaRechazarDevolucionCuandoElEstadoEsEnMantenimiento() {
        Bicicleta bicicleta = bicicletaConEstado(EstadoBicicleta.EN_MANTENIMIENTO);

        TransicionEstadoBicicletaInvalidaException exception =
            assertThrows(TransicionEstadoBicicletaInvalidaException.class, bicicleta::devolver);

        assertEquals(
            "La bicicleta BIC-001 no puede devolverse porque está EN_MANTENIMIENTO",
            exception.getMessage());
    }

    private Bicicleta bicicletaConEstado(EstadoBicicleta estado) {
        return new Bicicleta("BIC-001", TipoBicicleta.URBANA, estado);
    }
}