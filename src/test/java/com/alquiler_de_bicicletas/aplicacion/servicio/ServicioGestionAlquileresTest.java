package com.alquiler_de_bicicletas.aplicacion.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.alquiler_de_bicicletas.aplicacion.excepcion.AlquilerNoEncontradoException;
import com.alquiler_de_bicicletas.aplicacion.excepcion.BicicletaNoEncontradaException;
import com.alquiler_de_bicicletas.aplicacion.puerto.salida.PuertoSalidaRepositorioAlquileres;
import com.alquiler_de_bicicletas.aplicacion.puerto.salida.PuertoSalidaRepositorioBicicletas;
import com.alquiler_de_bicicletas.dominio.excepcion.TransicionEstadoBicicletaInvalidaException;
import com.alquiler_de_bicicletas.dominio.modelo.Alquiler;
import com.alquiler_de_bicicletas.dominio.modelo.Bicicleta;
import com.alquiler_de_bicicletas.dominio.modelo.EstadoAlquiler;
import com.alquiler_de_bicicletas.dominio.modelo.EstadoBicicleta;
import com.alquiler_de_bicicletas.dominio.modelo.TarifaBicicleta;
import com.alquiler_de_bicicletas.dominio.modelo.TipoBicicleta;

@ExtendWith(MockitoExtension.class)
class ServicioGestionAlquileresTest {

    private static final LocalDateTime INICIO = LocalDateTime.of(2026, 9, 11, 14, 0);

    @Mock
    private PuertoSalidaRepositorioBicicletas repositorioBicicletas;

    @Mock
    private PuertoSalidaRepositorioAlquileres repositorioAlquileres;

    private ServicioGestionAlquileres servicio;

    @BeforeEach
    void establecerServicio() {
        servicio = new ServicioGestionAlquileres(repositorioBicicletas, repositorioAlquileres);
    }

    @Test
    void deberiaIniciarAlquilerUrbanoYGuardarBicicletaAlquilada() {
        Bicicleta bicicleta = bicicletaDisponible(TipoBicicleta.URBANA);
        configurarGuardadoDeAlquiler();
        when(repositorioBicicletas.buscarPorCodigo("BIC-001")).thenReturn(Optional.of(bicicleta));

        Alquiler resultado = servicio.iniciarAlquiler("BIC-001", "Ana", INICIO, 2);

        assertEquals(TarifaBicicleta.URBANA, resultado.getTarifaBicicleta());
        assertEquals(EstadoBicicleta.ALQUILADA, bicicleta.getEstado());
        verify(repositorioAlquileres).guardar(any(Alquiler.class));
        verify(repositorioBicicletas).guardar(bicicleta);
    }

    @Test
    void deberiaIniciarAlquilerDeMontanaConSuTarifa() {
        Bicicleta bicicleta = bicicletaDisponible(TipoBicicleta.MONTAÑA);
        configurarGuardadoDeAlquiler();
        when(repositorioBicicletas.buscarPorCodigo("BIC-001")).thenReturn(Optional.of(bicicleta));

        Alquiler resultado = servicio.iniciarAlquiler("BIC-001", "Ana", INICIO, 2);

        assertEquals(5000, resultado.getTarifaBicicleta().obtenerValorPorHora());
    }

    @Test
    void deberiaIniciarAlquilerElectricoConSuTarifa() {
        Bicicleta bicicleta = bicicletaDisponible(TipoBicicleta.ELÉCTRICA);
        configurarGuardadoDeAlquiler();
        when(repositorioBicicletas.buscarPorCodigo("BIC-001")).thenReturn(Optional.of(bicicleta));

        Alquiler resultado = servicio.iniciarAlquiler("BIC-001", "Ana", INICIO, 2);

        assertEquals(7500, resultado.getTarifaBicicleta().obtenerValorPorHora());
    }

    @Test
    void deberiaRechazarInicioCuandoLaBicicletaNoExiste() {
        when(repositorioBicicletas.buscarPorCodigo("BIC-999")).thenReturn(Optional.empty());

        assertThrows(BicicletaNoEncontradaException.class,
                () -> servicio.iniciarAlquiler("BIC-999", "Ana", INICIO, 2));

        verify(repositorioAlquileres, never()).guardar(any(Alquiler.class));
    }

    @Test
    void deberiaPropagarRechazoCuandoLaBicicletaNoEstaDisponible() {
        Bicicleta bicicleta = new Bicicleta("BIC-001", TipoBicicleta.URBANA, EstadoBicicleta.ALQUILADA);
        when(repositorioBicicletas.buscarPorCodigo("BIC-001")).thenReturn(Optional.of(bicicleta));

        assertThrows(TransicionEstadoBicicletaInvalidaException.class,
                () -> servicio.iniciarAlquiler("BIC-001", "Ana", INICIO, 2));

        verify(repositorioAlquileres, never()).guardar(any(Alquiler.class));
        verify(repositorioBicicletas, never()).guardar(any(Bicicleta.class));
    }

    @Test
    void deberiaFinalizarAlquilerCalcularTotalYDejarBicicletaDisponible() {
        Bicicleta bicicleta = new Bicicleta("BIC-001", TipoBicicleta.MONTAÑA, EstadoBicicleta.ALQUILADA);
        Alquiler alquiler = new Alquiler("BIC-001", "Ana", INICIO, 2,
            TarifaBicicleta.MONTAÑA);
        when(repositorioAlquileres.buscarActivoPorCodigoBicicleta("BIC-001")).thenReturn(Optional.of(alquiler));
        when(repositorioBicicletas.buscarPorCodigo("BIC-001")).thenReturn(Optional.of(bicicleta));
        when(repositorioAlquileres.guardar(alquiler)).thenReturn(alquiler);

        Alquiler resultado = servicio.finalizarAlquiler("BIC-001", INICIO.plusHours(3).plusMinutes(20));

        assertEquals(EstadoAlquiler.FINALIZADO, resultado.getEstado());
        assertEquals(25000, resultado.getTotal());
        assertEquals(EstadoBicicleta.DISPONIBLE, bicicleta.getEstado());
        verify(repositorioAlquileres).guardar(alquiler);
        verify(repositorioBicicletas).guardar(bicicleta);
    }

    @Test
    void deberiaRechazarFinalizacionCuandoNoExisteAlquilerActivo() {
        when(repositorioAlquileres.buscarActivoPorCodigoBicicleta("BIC-999")).thenReturn(Optional.empty());

        assertThrows(AlquilerNoEncontradoException.class,
                () -> servicio.finalizarAlquiler("BIC-999", INICIO.plusHours(1)));

        verify(repositorioBicicletas, never()).buscarPorCodigo("BIC-999");
    }

    private Bicicleta bicicletaDisponible(TipoBicicleta tipo) {
        return new Bicicleta("BIC-001", tipo, EstadoBicicleta.DISPONIBLE);
    }

    private void configurarGuardadoDeAlquiler() {
        when(repositorioAlquileres.guardar(any(Alquiler.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));
    }
}