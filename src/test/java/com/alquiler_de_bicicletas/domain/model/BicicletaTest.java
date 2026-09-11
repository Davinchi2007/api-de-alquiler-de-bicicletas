package com.alquiler_de_bicicletas.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import com.alquiler_de_bicicletas.domain.exception.TransicionEstadoBicicletaInvalidaException;

class BicicletaTest {

    @Test
    void should_CrearBicicleta_When_DatosSonValidos() {
        Bicicleta bicicleta = new Bicicleta("BIC-001", TipoBicicleta.URBANA, EstadoBicicleta.DISPONIBLE);

        assertEquals("BIC-001", bicicleta.getCodigo());
        assertEquals(TipoBicicleta.URBANA, bicicleta.getTipo());
        assertEquals(EstadoBicicleta.DISPONIBLE, bicicleta.getEstado());
    }

    @Test
    void should_RechazarBicicleta_When_CodigoEsNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Bicicleta(null, TipoBicicleta.URBANA, EstadoBicicleta.DISPONIBLE));
    }

    @Test
    void should_RechazarBicicleta_When_CodigoEstaVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> new Bicicleta("   ", TipoBicicleta.URBANA, EstadoBicicleta.DISPONIBLE));
    }

    @Test
    void should_RechazarBicicleta_When_TipoEsNulo() {
        assertThrows(NullPointerException.class,
                () -> new Bicicleta("BIC-001", null, EstadoBicicleta.DISPONIBLE));
    }

    @Test
    void should_RechazarBicicleta_When_EstadoEsNulo() {
        assertThrows(NullPointerException.class,
                () -> new Bicicleta("BIC-001", TipoBicicleta.URBANA, null));
    }

    @Test
    void should_AlquilarBicicleta_When_EstadoEsDisponible() {
        Bicicleta bicicleta = bicicletaConEstado(EstadoBicicleta.DISPONIBLE);

        bicicleta.alquilar();

        assertEquals(EstadoBicicleta.ALQUILADA, bicicleta.getEstado());
    }

    @Test
    void should_RechazarAlquiler_When_EstadoEsAlquilada() {
        Bicicleta bicicleta = bicicletaConEstado(EstadoBicicleta.ALQUILADA);

        TransicionEstadoBicicletaInvalidaException exception =
            assertThrows(TransicionEstadoBicicletaInvalidaException.class, bicicleta::alquilar);

        assertEquals(
            "La bicicleta BIC-001 no puede alquilarse porque está ALQUILADA",
            exception.getMessage());
    }

    @Test
    void should_RechazarAlquiler_When_EstadoEsEnMantenimiento() {
        Bicicleta bicicleta = bicicletaConEstado(EstadoBicicleta.EN_MANTENIMIENTO);

        TransicionEstadoBicicletaInvalidaException exception =
            assertThrows(TransicionEstadoBicicletaInvalidaException.class, bicicleta::alquilar);

        assertEquals(
            "La bicicleta BIC-001 no puede alquilarse porque está EN_MANTENIMIENTO",
            exception.getMessage());
    }

    @Test
    void should_DevolverBicicleta_When_EstadoEsAlquilada() {
        Bicicleta bicicleta = bicicletaConEstado(EstadoBicicleta.ALQUILADA);

        bicicleta.devolver();

        assertEquals(EstadoBicicleta.DISPONIBLE, bicicleta.getEstado());
    }

    @Test
    void should_RechazarDevolucion_When_EstadoEsDisponible() {
        Bicicleta bicicleta = bicicletaConEstado(EstadoBicicleta.DISPONIBLE);

        TransicionEstadoBicicletaInvalidaException exception =
            assertThrows(TransicionEstadoBicicletaInvalidaException.class, bicicleta::devolver);

        assertEquals(
            "La bicicleta BIC-001 no puede devolverse porque está DISPONIBLE",
            exception.getMessage());
    }

    @Test
    void should_RechazarDevolucion_When_EstadoEsEnMantenimiento() {
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