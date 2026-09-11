package com.alquiler_de_bicicletas.application.service;

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

import com.alquiler_de_bicicletas.application.exception.BicicletaNoEncontradaException;
import com.alquiler_de_bicicletas.application.exception.BicicletaYaExisteException;
import com.alquiler_de_bicicletas.application.port.out.BicicletaRepositoryPort;
import com.alquiler_de_bicicletas.domain.model.Bicicleta;
import com.alquiler_de_bicicletas.domain.model.EstadoBicicleta;
import com.alquiler_de_bicicletas.domain.model.TipoBicicleta;

@ExtendWith(MockitoExtension.class)
class BicicletaServiceTest {

    @Mock
    private BicicletaRepositoryPort bicicletaRepositoryPort;

    private BicicletaService bicicletaService;

    @BeforeEach
    void setUp() {
        bicicletaService = new BicicletaService(bicicletaRepositoryPort);
    }

    @Test
    void should_RegisterBicicleta_When_CodigoDoesNotExist() {
        when(bicicletaRepositoryPort.existePorCodigo("BIC-001")).thenReturn(false);
        Bicicleta bicicletaGuardada = bicicleta("BIC-001", TipoBicicleta.URBANA, EstadoBicicleta.DISPONIBLE);
        when(bicicletaRepositoryPort.guardar(any(Bicicleta.class))).thenReturn(bicicletaGuardada);

        Bicicleta resultado = bicicletaService.registrar("BIC-001", TipoBicicleta.URBANA);

        ArgumentCaptor<Bicicleta> captor = ArgumentCaptor.forClass(Bicicleta.class);
        verify(bicicletaRepositoryPort).existePorCodigo("BIC-001");
        verify(bicicletaRepositoryPort).guardar(captor.capture());
        assertEquals("BIC-001", captor.getValue().getCodigo());
        assertEquals(TipoBicicleta.URBANA, captor.getValue().getTipo());
        assertEquals(EstadoBicicleta.DISPONIBLE, captor.getValue().getEstado());
        assertEquals(bicicletaGuardada, resultado);
    }

    @Test
    void should_RejectRegistration_When_CodigoAlreadyExists() {
        when(bicicletaRepositoryPort.existePorCodigo("BIC-001")).thenReturn(true);

        BicicletaYaExisteException exception = assertThrows(
                BicicletaYaExisteException.class,
                () -> bicicletaService.registrar("BIC-001", TipoBicicleta.URBANA));

        verify(bicicletaRepositoryPort).existePorCodigo("BIC-001");
        verify(bicicletaRepositoryPort, never()).guardar(any(Bicicleta.class));
        assertEquals("Ya existe una bicicleta registrada con el código BIC-001", exception.getMessage());
    }

    @Test
    void should_ReturnBicicleta_When_CodigoExists() {
        Bicicleta bicicleta = bicicleta("BIC-001", TipoBicicleta.URBANA, EstadoBicicleta.DISPONIBLE);
        when(bicicletaRepositoryPort.buscarPorCodigo("BIC-001")).thenReturn(Optional.of(bicicleta));

        Bicicleta resultado = bicicletaService.buscarPorCodigo("BIC-001");

        verify(bicicletaRepositoryPort).buscarPorCodigo("BIC-001");
        assertEquals(bicicleta, resultado);
    }

    @Test
    void should_ThrowException_When_CodigoDoesNotExist() {
        when(bicicletaRepositoryPort.buscarPorCodigo("BIC-999")).thenReturn(Optional.empty());

        BicicletaNoEncontradaException exception = assertThrows(
                BicicletaNoEncontradaException.class,
                () -> bicicletaService.buscarPorCodigo("BIC-999"));

        verify(bicicletaRepositoryPort).buscarPorCodigo("BIC-999");
        assertEquals("No se encontró una bicicleta con el código BIC-999", exception.getMessage());
    }

    @Test
    void should_ReturnAvailableBicicletas() {
        List<Bicicleta> bicicletas = List.of(
                bicicleta("BIC-001", TipoBicicleta.URBANA, EstadoBicicleta.DISPONIBLE));
        when(bicicletaRepositoryPort.obtenerDisponibles()).thenReturn(bicicletas);

        List<Bicicleta> resultado = bicicletaService.obtenerDisponibles();

        verify(bicicletaRepositoryPort).obtenerDisponibles();
        assertEquals(bicicletas, resultado);
    }

    @Test
    void should_ReturnAvailableBicicletasByTipo() {
        List<Bicicleta> bicicletas = List.of(
                bicicleta("BIC-003", TipoBicicleta.ELÉCTRICA, EstadoBicicleta.DISPONIBLE));
        when(bicicletaRepositoryPort.obtenerDisponiblesPorTipo(TipoBicicleta.ELÉCTRICA)).thenReturn(bicicletas);

        List<Bicicleta> resultado = bicicletaService.obtenerDisponiblesPorTipo(TipoBicicleta.ELÉCTRICA);

        verify(bicicletaRepositoryPort).obtenerDisponiblesPorTipo(TipoBicicleta.ELÉCTRICA);
        assertEquals(bicicletas, resultado);
    }

    private Bicicleta bicicleta(String codigo, TipoBicicleta tipo, EstadoBicicleta estado) {
        return new Bicicleta(codigo, tipo, estado);
    }
}