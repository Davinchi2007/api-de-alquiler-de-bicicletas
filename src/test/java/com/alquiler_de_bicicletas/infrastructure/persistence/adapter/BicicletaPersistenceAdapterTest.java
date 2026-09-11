package com.alquiler_de_bicicletas.infrastructure.persistence.adapter;

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

import com.alquiler_de_bicicletas.domain.model.Bicicleta;
import com.alquiler_de_bicicletas.domain.model.EstadoBicicleta;
import com.alquiler_de_bicicletas.domain.model.TipoBicicleta;
import com.alquiler_de_bicicletas.infrastructure.persistence.entity.BicicletaJpaEntity;
import com.alquiler_de_bicicletas.infrastructure.persistence.repository.BicicletaJpaRepository;

@ExtendWith(MockitoExtension.class)
class BicicletaPersistenceAdapterTest {

    @Mock
    private BicicletaJpaRepository bicicletaJpaRepository;

    private BicicletaPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new BicicletaPersistenceAdapter(bicicletaJpaRepository);
    }

    @Test
    void should_GuardarBicicleta_YConvertirEntityADominio() {
        Bicicleta bicicleta = bicicleta("BIC-001", TipoBicicleta.URBANA, EstadoBicicleta.DISPONIBLE);
        BicicletaJpaEntity entityGuardada = entity("BIC-001", TipoBicicleta.URBANA, EstadoBicicleta.DISPONIBLE);
        when(bicicletaJpaRepository.save(any(BicicletaJpaEntity.class))).thenReturn(entityGuardada);

        Bicicleta resultado = adapter.guardar(bicicleta);

        ArgumentCaptor<BicicletaJpaEntity> captor = ArgumentCaptor.forClass(BicicletaJpaEntity.class);
        verify(bicicletaJpaRepository).save(captor.capture());
        assertEquals("BIC-001", captor.getValue().getCodigo());
        assertEquals(TipoBicicleta.URBANA, captor.getValue().getTipo());
        assertEquals(EstadoBicicleta.DISPONIBLE, captor.getValue().getEstado());
        assertEquals("BIC-001", resultado.getCodigo());
        assertEquals(TipoBicicleta.URBANA, resultado.getTipo());
        assertEquals(EstadoBicicleta.DISPONIBLE, resultado.getEstado());
    }

    @Test
    void should_BuscarBicicletaPorCodigo_YConvertirEntityADominio() {
        BicicletaJpaEntity entity = entity("BIC-002", TipoBicicleta.MONTAÑA, EstadoBicicleta.ALQUILADA);
        when(bicicletaJpaRepository.findByCodigo("BIC-002")).thenReturn(Optional.of(entity));

        Optional<Bicicleta> resultado = adapter.buscarPorCodigo("BIC-002");

        verify(bicicletaJpaRepository).findByCodigo("BIC-002");
        assertTrue(resultado.isPresent());
        assertEquals("BIC-002", resultado.get().getCodigo());
        assertEquals(TipoBicicleta.MONTAÑA, resultado.get().getTipo());
        assertEquals(EstadoBicicleta.ALQUILADA, resultado.get().getEstado());
    }

    @Test
    void should_DevolverVacio_CuandoBicicletaNoExiste() {
        when(bicicletaJpaRepository.findByCodigo("BIC-999")).thenReturn(Optional.empty());

        Optional<Bicicleta> resultado = adapter.buscarPorCodigo("BIC-999");

        verify(bicicletaJpaRepository).findByCodigo("BIC-999");
        assertTrue(resultado.isEmpty());
    }

    @Test
    void should_ObtenerBicicletasDisponibles_YFiltrarPorEstado() {
        when(bicicletaJpaRepository.findByEstado(EstadoBicicleta.DISPONIBLE))
                .thenReturn(List.of(entity("BIC-001", TipoBicicleta.URBANA, EstadoBicicleta.DISPONIBLE)));

        List<Bicicleta> resultado = adapter.obtenerDisponibles();

        verify(bicicletaJpaRepository).findByEstado(EstadoBicicleta.DISPONIBLE);
        assertEquals(1, resultado.size());
        assertEquals("BIC-001", resultado.get(0).getCodigo());
        assertEquals(EstadoBicicleta.DISPONIBLE, resultado.get(0).getEstado());
    }

    @Test
    void should_ObtenerBicicletasDisponiblesPorTipo_YFiltrarPorEstadoYTipo() {
        when(bicicletaJpaRepository.findByEstadoAndTipo(EstadoBicicleta.DISPONIBLE, TipoBicicleta.ELÉCTRICA))
                .thenReturn(List.of(entity("BIC-003", TipoBicicleta.ELÉCTRICA, EstadoBicicleta.DISPONIBLE)));

        List<Bicicleta> resultado = adapter.obtenerDisponiblesPorTipo(TipoBicicleta.ELÉCTRICA);

        verify(bicicletaJpaRepository).findByEstadoAndTipo(EstadoBicicleta.DISPONIBLE, TipoBicicleta.ELÉCTRICA);
        assertEquals(1, resultado.size());
        assertEquals(TipoBicicleta.ELÉCTRICA, resultado.get(0).getTipo());
    }

    @Test
    void should_DevolverVerdadero_CuandoExisteCodigo() {
        when(bicicletaJpaRepository.existsByCodigo("BIC-001")).thenReturn(true);

        boolean resultado = adapter.existePorCodigo("BIC-001");

        verify(bicicletaJpaRepository).existsByCodigo("BIC-001");
        assertTrue(resultado);
    }

    @Test
    void should_DevolverFalso_CuandoNoExisteCodigo() {
        when(bicicletaJpaRepository.existsByCodigo("BIC-999")).thenReturn(false);

        boolean resultado = adapter.existePorCodigo("BIC-999");

        verify(bicicletaJpaRepository).existsByCodigo("BIC-999");
        assertFalse(resultado);
    }

    private Bicicleta bicicleta(String codigo, TipoBicicleta tipo, EstadoBicicleta estado) {
        return new Bicicleta(codigo, tipo, estado);
    }

    private BicicletaJpaEntity entity(String codigo, TipoBicicleta tipo, EstadoBicicleta estado) {
        return new BicicletaJpaEntity(codigo, tipo, estado);
    }
}