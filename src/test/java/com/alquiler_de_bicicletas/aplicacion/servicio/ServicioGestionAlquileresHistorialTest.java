package com.alquiler_de_bicicletas.aplicacion.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.alquiler_de_bicicletas.aplicacion.excepcion.BicicletaNoEncontradaException;
import com.alquiler_de_bicicletas.aplicacion.puerto.salida.PuertoSalidaRepositorioAlquileres;
import com.alquiler_de_bicicletas.aplicacion.puerto.salida.PuertoSalidaRepositorioBicicletas;
import com.alquiler_de_bicicletas.dominio.modelo.Alquiler;
import com.alquiler_de_bicicletas.dominio.modelo.Bicicleta;
import com.alquiler_de_bicicletas.dominio.modelo.EstadoBicicleta;
import com.alquiler_de_bicicletas.dominio.modelo.TarifaBicicleta;
import com.alquiler_de_bicicletas.dominio.modelo.TipoBicicleta;

@ExtendWith(MockitoExtension.class)
class ServicioGestionAlquileresHistorialTest {

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
    void deberiaRetornarTodosLosAlquileresDeUnaBicicleta() {
        Bicicleta bicicleta = new Bicicleta("BIC-001", TipoBicicleta.URBANA, EstadoBicicleta.DISPONIBLE);
        List<Alquiler> historial = List.of(
                alquilerFinalizado("BIC-001", "Ana"),
                alquilerActivo("BIC-001", "Luis"));
        when(repositorioBicicletas.buscarPorCodigo("BIC-001")).thenReturn(Optional.of(bicicleta));
        when(repositorioAlquileres.buscarPorCodigoBicicleta("BIC-001")).thenReturn(historial);

        List<Alquiler> resultado = servicio.consultarHistorialAlquileres("BIC-001");

        assertEquals(historial, resultado);
        verify(repositorioAlquileres).buscarPorCodigoBicicleta("BIC-001");
    }

    @Test
    void deberiaRetornarListaVaciaCuandoLaBicicletaNoTieneHistorial() {
        Bicicleta bicicleta = new Bicicleta("BIC-001", TipoBicicleta.URBANA, EstadoBicicleta.DISPONIBLE);
        when(repositorioBicicletas.buscarPorCodigo("BIC-001")).thenReturn(Optional.of(bicicleta));
        when(repositorioAlquileres.buscarPorCodigoBicicleta("BIC-001")).thenReturn(List.of());

        assertEquals(List.of(), servicio.consultarHistorialAlquileres("BIC-001"));
    }

    @Test
    void deberiaRechazarHistorialCuandoLaBicicletaNoExiste() {
        when(repositorioBicicletas.buscarPorCodigo("BIC-999")).thenReturn(Optional.empty());

        assertThrows(BicicletaNoEncontradaException.class,
                () -> servicio.consultarHistorialAlquileres("BIC-999"));
    }

    private Alquiler alquilerActivo(String codigo, String cliente) {
        return new Alquiler(codigo, cliente, LocalDateTime.of(2026, 1, 1, 10, 0), 2, TarifaBicicleta.URBANA);
    }

    private Alquiler alquilerFinalizado(String codigo, String cliente) {
        Alquiler alquiler = alquilerActivo(codigo, cliente);
        alquiler.finalizar(alquiler.getFechaHoraInicio().plusHours(3).plusMinutes(20));
        return alquiler;
    }
}