package com.alquiler_de_bicicletas.aplicacion.servicio;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.alquiler_de_bicicletas.aplicacion.excepcion.BicicletaNoEncontradaException;
import com.alquiler_de_bicicletas.aplicacion.excepcion.BicicletaYaExisteException;
import com.alquiler_de_bicicletas.aplicacion.puerto.salida.PuertoSalidaRepositorioBicicletas;
import com.alquiler_de_bicicletas.dominio.modelo.Bicicleta;
import com.alquiler_de_bicicletas.dominio.modelo.EstadoBicicleta;
import com.alquiler_de_bicicletas.dominio.modelo.TipoBicicleta;

@ExtendWith(MockitoExtension.class)
class ServicioGestionBicicletasTest {

    @Mock
    private PuertoSalidaRepositorioBicicletas repositorioBicicletas;

    private ServicioGestionBicicletas servicioGestionBicicletas;

    @BeforeEach
    void setUp() {
        servicioGestionBicicletas = new ServicioGestionBicicletas(repositorioBicicletas);
    }

    @Test
    void deberiaRegistrarBicicletaCuandoElCodigoNoExiste() {
        when(repositorioBicicletas.existePorCodigo("BIC-001")).thenReturn(false);
        Bicicleta bicicletaGuardada = bicicleta("BIC-001", TipoBicicleta.URBANA, EstadoBicicleta.DISPONIBLE);
        when(repositorioBicicletas.guardar(any(Bicicleta.class))).thenReturn(bicicletaGuardada);

        Bicicleta resultado = servicioGestionBicicletas.registrar(
            "BIC-001", TipoBicicleta.URBANA, EstadoBicicleta.DISPONIBLE);

        ArgumentCaptor<Bicicleta> captor = ArgumentCaptor.forClass(Bicicleta.class);
        verify(repositorioBicicletas).existePorCodigo("BIC-001");
        verify(repositorioBicicletas).guardar(captor.capture());
        assertEquals("BIC-001", captor.getValue().getCodigo());
        assertEquals(TipoBicicleta.URBANA, captor.getValue().getTipo());
        assertEquals(EstadoBicicleta.DISPONIBLE, captor.getValue().getEstado());
        assertEquals(bicicletaGuardada, resultado);
    }

        @Test
        void deberiaRegistrarBicicletaEnMantenimiento() {
        when(repositorioBicicletas.existePorCodigo("BIC-004")).thenReturn(false);
        when(repositorioBicicletas.guardar(any(Bicicleta.class)))
            .thenAnswer(invocacion -> invocacion.getArgument(0));

        Bicicleta resultado = servicioGestionBicicletas.registrar(
            "BIC-004", TipoBicicleta.MONTAÑA, EstadoBicicleta.EN_MANTENIMIENTO);

        assertEquals(EstadoBicicleta.EN_MANTENIMIENTO, resultado.getEstado());
        verify(repositorioBicicletas).guardar(any(Bicicleta.class));
        }

    @Test
    void deberiaRechazarRegistroCuandoElCodigoYaExiste() {
        when(repositorioBicicletas.existePorCodigo("BIC-001")).thenReturn(true);

        BicicletaYaExisteException exception = assertThrows(
                BicicletaYaExisteException.class,
                () -> servicioGestionBicicletas.registrar(
                    "BIC-001", TipoBicicleta.URBANA, EstadoBicicleta.DISPONIBLE));

        verify(repositorioBicicletas).existePorCodigo("BIC-001");
        verify(repositorioBicicletas, never()).guardar(any(Bicicleta.class));
        assertEquals("Ya existe una bicicleta registrada con el código BIC-001", exception.getMessage());
    }

    @Test
    void deberiaRetornarBicicletaCuandoElCodigoExiste() {
        Bicicleta bicicleta = bicicleta("BIC-001", TipoBicicleta.URBANA, EstadoBicicleta.DISPONIBLE);
        when(repositorioBicicletas.buscarPorCodigo("BIC-001")).thenReturn(Optional.of(bicicleta));

        Bicicleta resultado = servicioGestionBicicletas.buscarPorCodigo("BIC-001");

        verify(repositorioBicicletas).buscarPorCodigo("BIC-001");
        assertEquals(bicicleta, resultado);
    }

    @Test
    void deberiaLanzarExcepcionCuandoElCodigoNoExiste() {
        when(repositorioBicicletas.buscarPorCodigo("BIC-999")).thenReturn(Optional.empty());

        BicicletaNoEncontradaException exception = assertThrows(
                BicicletaNoEncontradaException.class,
                () -> servicioGestionBicicletas.buscarPorCodigo("BIC-999"));

        verify(repositorioBicicletas).buscarPorCodigo("BIC-999");
        assertEquals("No se encontró una bicicleta con el código BIC-999", exception.getMessage());
    }

    @Test
    void deberiaRetornarBicicletasDisponibles() {
        List<Bicicleta> bicicletas = List.of(
                bicicleta("BIC-001", TipoBicicleta.URBANA, EstadoBicicleta.DISPONIBLE));
        when(repositorioBicicletas.obtenerDisponibles()).thenReturn(bicicletas);

        List<Bicicleta> resultado = servicioGestionBicicletas.obtenerDisponibles();

        verify(repositorioBicicletas).obtenerDisponibles();
        assertEquals(bicicletas, resultado);
    }

    @Test
    void deberiaRetornarBicicletasDisponiblesPorTipo() {
        List<Bicicleta> bicicletas = List.of(
                bicicleta("BIC-003", TipoBicicleta.ELÉCTRICA, EstadoBicicleta.DISPONIBLE));
        when(repositorioBicicletas.obtenerDisponiblesPorTipo(TipoBicicleta.ELÉCTRICA)).thenReturn(bicicletas);

        List<Bicicleta> resultado = servicioGestionBicicletas.obtenerDisponiblesPorTipo(TipoBicicleta.ELÉCTRICA);

        verify(repositorioBicicletas).obtenerDisponiblesPorTipo(TipoBicicleta.ELÉCTRICA);
        assertEquals(bicicletas, resultado);
    }

    private Bicicleta bicicleta(String codigo, TipoBicicleta tipo, EstadoBicicleta estado) {
        return new Bicicleta(codigo, tipo, estado);
    }
}