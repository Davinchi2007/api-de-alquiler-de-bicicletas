package com.alquiler_de_bicicletas.infraestructura.persistencia.adaptador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.alquiler_de_bicicletas.dominio.modelo.Bicicleta;
import com.alquiler_de_bicicletas.dominio.modelo.EstadoBicicleta;
import com.alquiler_de_bicicletas.dominio.modelo.TipoBicicleta;
import com.alquiler_de_bicicletas.infraestructura.persistencia.entidad.EntidadJpaBicicleta;
import com.alquiler_de_bicicletas.infraestructura.persistencia.repositorio.RepositorioJpaBicicletas;

@ExtendWith(MockitoExtension.class)
class AdaptadorPersistenciaBicicletasTest {

    @Mock
    private RepositorioJpaBicicletas repositorioJpaBicicletas;

    private AdaptadorPersistenciaBicicletas adaptadorPersistenciaBicicletas;

    @BeforeEach
    void setUp() {
        adaptadorPersistenciaBicicletas = new AdaptadorPersistenciaBicicletas(repositorioJpaBicicletas);
    }

    @Test
    void deberiaGuardarBicicletaYConvertirEntidadADominio() {
        Bicicleta bicicleta = bicicleta("BIC-001", TipoBicicleta.URBANA, EstadoBicicleta.DISPONIBLE);
        EntidadJpaBicicleta entidadGuardada = entidad("BIC-001", TipoBicicleta.URBANA, EstadoBicicleta.DISPONIBLE);
        when(repositorioJpaBicicletas.save(any(EntidadJpaBicicleta.class))).thenReturn(entidadGuardada);

        Bicicleta resultado = adaptadorPersistenciaBicicletas.guardar(bicicleta);

        ArgumentCaptor<EntidadJpaBicicleta> captor = ArgumentCaptor.forClass(EntidadJpaBicicleta.class);
        verify(repositorioJpaBicicletas).save(captor.capture());
        assertEquals("BIC-001", captor.getValue().getCodigo());
        assertEquals(TipoBicicleta.URBANA, captor.getValue().getTipo());
        assertEquals(EstadoBicicleta.DISPONIBLE, captor.getValue().getEstado());
        assertEquals("BIC-001", resultado.getCodigo());
        assertEquals(TipoBicicleta.URBANA, resultado.getTipo());
        assertEquals(EstadoBicicleta.DISPONIBLE, resultado.getEstado());
    }

    @Test
    void deberiaBuscarBicicletaPorCodigoYConvertirEntidadADominio() {
        EntidadJpaBicicleta entidad = entidad("BIC-002", TipoBicicleta.MONTAÑA, EstadoBicicleta.ALQUILADA);
        when(repositorioJpaBicicletas.buscarPorCodigo("BIC-002")).thenReturn(Optional.of(entidad));

        Optional<Bicicleta> resultado = adaptadorPersistenciaBicicletas.buscarPorCodigo("BIC-002");

        verify(repositorioJpaBicicletas).buscarPorCodigo("BIC-002");
        assertTrue(resultado.isPresent());
        assertEquals("BIC-002", resultado.get().getCodigo());
        assertEquals(TipoBicicleta.MONTAÑA, resultado.get().getTipo());
        assertEquals(EstadoBicicleta.ALQUILADA, resultado.get().getEstado());
    }

    @Test
    void deberiaDevolverVacioCuandoLaBicicletaNoExiste() {
        when(repositorioJpaBicicletas.buscarPorCodigo("BIC-999")).thenReturn(Optional.empty());

        Optional<Bicicleta> resultado = adaptadorPersistenciaBicicletas.buscarPorCodigo("BIC-999");

        verify(repositorioJpaBicicletas).buscarPorCodigo("BIC-999");
        assertTrue(resultado.isEmpty());
    }

    @Test
    void deberiaObtenerBicicletasDisponiblesYFiltrarPorEstado() {
        when(repositorioJpaBicicletas.buscarPorEstado(EstadoBicicleta.DISPONIBLE))
            .thenReturn(List.of(entidad("BIC-001", TipoBicicleta.URBANA, EstadoBicicleta.DISPONIBLE)));

        List<Bicicleta> resultado = adaptadorPersistenciaBicicletas.obtenerDisponibles();

        verify(repositorioJpaBicicletas).buscarPorEstado(EstadoBicicleta.DISPONIBLE);
        assertEquals(1, resultado.size());
        assertEquals("BIC-001", resultado.get(0).getCodigo());
        assertEquals(EstadoBicicleta.DISPONIBLE, resultado.get(0).getEstado());
    }

    @Test
    void deberiaObtenerBicicletasDisponiblesPorTipoYFiltrarPorEstadoYTipo() {
        when(repositorioJpaBicicletas.buscarPorEstadoYTipo(EstadoBicicleta.DISPONIBLE, TipoBicicleta.ELÉCTRICA))
            .thenReturn(List.of(entidad("BIC-003", TipoBicicleta.ELÉCTRICA, EstadoBicicleta.DISPONIBLE)));

        List<Bicicleta> resultado = adaptadorPersistenciaBicicletas.obtenerDisponiblesPorTipo(TipoBicicleta.ELÉCTRICA);

        verify(repositorioJpaBicicletas).buscarPorEstadoYTipo(EstadoBicicleta.DISPONIBLE, TipoBicicleta.ELÉCTRICA);
        assertEquals(1, resultado.size());
        assertEquals(TipoBicicleta.ELÉCTRICA, resultado.get(0).getTipo());
    }

    @Test
    void deberiaDevolverVerdaderoCuandoExisteElCodigo() {
        when(repositorioJpaBicicletas.existePorCodigo("BIC-001")).thenReturn(true);

        boolean resultado = adaptadorPersistenciaBicicletas.existePorCodigo("BIC-001");

        verify(repositorioJpaBicicletas).existePorCodigo("BIC-001");
        assertTrue(resultado);
    }

    @Test
    void deberiaDevolverFalsoCuandoNoExisteElCodigo() {
        when(repositorioJpaBicicletas.existePorCodigo("BIC-999")).thenReturn(false);

        boolean resultado = adaptadorPersistenciaBicicletas.existePorCodigo("BIC-999");

        verify(repositorioJpaBicicletas).existePorCodigo("BIC-999");
        assertFalse(resultado);
    }

    private Bicicleta bicicleta(String codigo, TipoBicicleta tipo, EstadoBicicleta estado) {
        return new Bicicleta(codigo, tipo, estado);
    }

    private EntidadJpaBicicleta entidad(String codigo, TipoBicicleta tipo, EstadoBicicleta estado) {
        return new EntidadJpaBicicleta(codigo, tipo, estado);
    }
}