package com.alquiler_de_bicicletas.dominio.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.alquiler_de_bicicletas.dominio.excepcion.AlquilerYaFinalizadoException;
import com.alquiler_de_bicicletas.dominio.excepcion.FechaDevolucionInvalidaException;

class AlquilerTest {

    private static final LocalDateTime INICIO = LocalDateTime.of(2026, 9, 11, 14, 0);

    @Test
    void deberiaResolverTarifaUrbana() {
        assertEquals(3500, TarifaBicicleta.URBANA.obtenerValorPorHora());
    }

    @Test
    void deberiaResolverTarifaMontana() {
        assertEquals(5000, TarifaBicicleta.MONTAÑA.obtenerValorPorHora());
    }

    @Test
    void deberiaResolverTarifaElectrica() {
        assertEquals(7500, TarifaBicicleta.ELÉCTRICA.obtenerValorPorHora());
    }

    @Test
    void deberiaCalcularUnaHoraCuandoLaDuracionEsExacta() {
        Alquiler alquiler = crearAlquiler(2, TarifaBicicleta.URBANA);

        alquiler.finalizar(INICIO.plusHours(1));

        assertEquals(1, alquiler.getHorasFacturables());
    }

    @Test
    void deberiaCalcularDosHorasCuandoLaDuracionEsUnaHoraYDiezMinutos() {
        Alquiler alquiler = crearAlquiler(2, TarifaBicicleta.URBANA);

        alquiler.finalizar(INICIO.plusHours(1).plusMinutes(10));

        assertEquals(2, alquiler.getHorasFacturables());
    }

    @Test
    void deberiaCalcularDosHorasCuandoLaDuracionEsUnaHoraYUnMinuto() {
        Alquiler alquiler = crearAlquiler(10, TarifaBicicleta.URBANA);

        alquiler.finalizar(INICIO.plusHours(1).plusMinutes(1));

        assertEquals(2, alquiler.getHorasFacturables());
    }

    @Test
    void deberiaCalcularDosHorasCuandoLaDuracionEsUnaHoraYCincuentaYNueveMinutos() {
        Alquiler alquiler = crearAlquiler(10, TarifaBicicleta.URBANA);

        alquiler.finalizar(INICIO.plusHours(1).plusMinutes(59));

        assertEquals(2, alquiler.getHorasFacturables());
    }

    @Test
    void deberiaCalcularTresHorasCuandoLaDuracionEsDosHorasYUnMinuto() {
        Alquiler alquiler = crearAlquiler(10, TarifaBicicleta.URBANA);

        alquiler.finalizar(INICIO.plusHours(2).plusMinutes(1));

        assertEquals(3, alquiler.getHorasFacturables());
    }

    @Test
    void deberiaCalcularElCostoBase() {
        Alquiler alquiler = crearAlquiler(2, TarifaBicicleta.URBANA);

        alquiler.finalizar(INICIO.plusHours(2));

        assertEquals(7000, alquiler.getCostoBase());
    }

    @Test
    void deberiaFinalizarSinMultaCuandoNoExisteRetraso() {
        Alquiler alquiler = crearAlquiler(2, TarifaBicicleta.MONTAÑA);

        alquiler.finalizar(INICIO.plusHours(2));

        assertEquals(0, alquiler.getMulta());
        assertEquals(EstadoAlquiler.FINALIZADO, alquiler.getEstado());
    }

    @Test
    void deberiaCalcularMultaCeroCuandoLaDevolucionCoincideConLaDuracionEstimada() {
        Alquiler alquiler = crearAlquiler(2, TarifaBicicleta.MONTAÑA);

        alquiler.finalizar(INICIO.plusHours(2));

        assertEquals(0, alquiler.getMulta());
    }

    @Test
    void deberiaCalcularUnaHoraDeMultaCuandoElRetrasoEsDeUnMinuto() {
        Alquiler alquiler = crearAlquiler(2, TarifaBicicleta.MONTAÑA);

        alquiler.finalizar(INICIO.plusHours(2).plusMinutes(1));

        assertEquals(2500, alquiler.getMulta());
    }

    @Test
    void deberiaCalcularDosHorasDeMultaCuandoElRetrasoEsDeUnaHoraYUnMinuto() {
        Alquiler alquiler = crearAlquiler(2, TarifaBicicleta.MONTAÑA);

        alquiler.finalizar(INICIO.plusHours(3).plusMinutes(1));

        assertEquals(5000, alquiler.getMulta());
    }

    @Test
    void deberiaCalcularDosHorasDeMultaCuandoElRetrasoEsDeUnaHoraYCincuentaYNueveMinutos() {
        Alquiler alquiler = crearAlquiler(2, TarifaBicicleta.MONTAÑA);

        alquiler.finalizar(INICIO.plusHours(3).plusMinutes(59));

        assertEquals(5000, alquiler.getMulta());
    }

    @Test
    void deberiaCalcularDosHorasDeMultaCuandoElRetrasoEsDeDosHoras() {
        Alquiler alquiler = crearAlquiler(2, TarifaBicicleta.MONTAÑA);

        alquiler.finalizar(INICIO.plusHours(4));

        assertEquals(5000, alquiler.getMulta());
    }

    @Test
    void deberiaCalcularTresHorasDeMultaCuandoElRetrasoEsDeDosHorasYUnMinuto() {
        Alquiler alquiler = crearAlquiler(2, TarifaBicicleta.MONTAÑA);

        alquiler.finalizar(INICIO.plusHours(4).plusMinutes(1));

        assertEquals(7500, alquiler.getMulta());
    }

    @Test
    void deberiaCalcularMultaCuandoExisteRetraso() {
        Alquiler alquiler = crearAlquiler(2, TarifaBicicleta.MONTAÑA);

        alquiler.finalizar(INICIO.plusHours(3).plusMinutes(20));

        assertEquals(2, alquiler.getHorasFacturables() - alquiler.getDuracionEstimadaHoras());
        assertEquals(5000, alquiler.getMulta());
    }

    @Test
    void deberiaCalcularElTotalDelEjemploDeMontana() {
        Alquiler alquiler = crearAlquiler(2, TarifaBicicleta.MONTAÑA);

        alquiler.finalizar(INICIO.plusHours(3).plusMinutes(20));

        assertEquals(4, alquiler.getHorasFacturables());
        assertEquals(20000, alquiler.getCostoBase());
        assertEquals(5000, alquiler.getMulta());
        assertEquals(25000, alquiler.getTotal());
    }

    @Test
    void deberiaRechazarDevolucionAnteriorAlInicio() {
        Alquiler alquiler = crearAlquiler(2, TarifaBicicleta.URBANA);

        FechaDevolucionInvalidaException exception = assertThrows(FechaDevolucionInvalidaException.class,
                () -> alquiler.finalizar(INICIO.minusMinutes(1)));

        assertEquals("La fecha de devolución debe ser posterior a la fecha de inicio", exception.getMessage());
    }

    @Test
    void deberiaRechazarDevolucionEnLaMismaHoraDeInicio() {
        Alquiler alquiler = crearAlquiler(2, TarifaBicicleta.URBANA);

        FechaDevolucionInvalidaException exception = assertThrows(FechaDevolucionInvalidaException.class,
                () -> alquiler.finalizar(INICIO));

        assertEquals("La fecha de devolución debe ser posterior a la fecha de inicio", exception.getMessage());
    }

    @Test
    void deberiaRedondearHaciaArribaCuandoExisteUnNanosegundoAdicional() {
        Alquiler alquiler = crearAlquiler(2, TarifaBicicleta.URBANA);

        alquiler.finalizar(INICIO.plusHours(1).plusNanos(1));

        assertEquals(2, alquiler.getHorasFacturables());
    }

    @Test
    void deberiaRechazarFinalizacionDeUnAlquilerYaFinalizado() {
        Alquiler alquiler = crearAlquiler(2, TarifaBicicleta.URBANA);
        alquiler.finalizar(INICIO.plusHours(1));

        AlquilerYaFinalizadoException exception = assertThrows(AlquilerYaFinalizadoException.class,
                () -> alquiler.finalizar(INICIO.plusHours(2)));

        assertEquals("El alquiler ya fue finalizado", exception.getMessage());
    }

    private Alquiler crearAlquiler(long duracionEstimada, TarifaBicicleta tarifa) {
        return new Alquiler("BIC-001", "Juan Pérez", INICIO, duracionEstimada, tarifa);
    }
}