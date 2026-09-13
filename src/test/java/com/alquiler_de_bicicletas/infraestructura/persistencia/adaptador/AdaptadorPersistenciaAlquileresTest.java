package com.alquiler_de_bicicletas.infraestructura.persistencia.adaptador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.alquiler_de_bicicletas.dominio.modelo.Alquiler;
import com.alquiler_de_bicicletas.dominio.modelo.EstadoAlquiler;
import com.alquiler_de_bicicletas.dominio.modelo.TarifaBicicleta;
import com.alquiler_de_bicicletas.infraestructura.persistencia.entidad.EntidadJpaAlquiler;
import com.alquiler_de_bicicletas.infraestructura.persistencia.repositorio.RepositorioJpaAlquileres;

@ExtendWith(MockitoExtension.class)
class AdaptadorPersistenciaAlquileresTest {

    private static final LocalDateTime INICIO = LocalDateTime.of(2026, 9, 11, 14, 0);

    @Mock
    private RepositorioJpaAlquileres repositorioJpaAlquileres;

    private AdaptadorPersistenciaAlquileres adaptador;

    @BeforeEach
    void establecerAdaptador() {
        adaptador = new AdaptadorPersistenciaAlquileres(repositorioJpaAlquileres);
    }

    @Test
    void deberiaGuardarAlquilerActivoConResultadosEconomicosNulos() {
        Alquiler alquiler = new Alquiler("BIC-001", "Ana", INICIO, 2, TarifaBicicleta.URBANA);
        when(repositorioJpaAlquileres.save(any(EntidadJpaAlquiler.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        adaptador.guardar(alquiler);

        ArgumentCaptor<EntidadJpaAlquiler> captor = ArgumentCaptor.forClass(EntidadJpaAlquiler.class);
        verify(repositorioJpaAlquileres).save(captor.capture());
        assertEquals(EstadoAlquiler.ACTIVO, captor.getValue().getEstado());
        assertNull(captor.getValue().getHorasFacturables());
        assertNull(captor.getValue().getCostoBase());
        assertNull(captor.getValue().getMulta());
        assertNull(captor.getValue().getTotal());
    }

    @Test
    void deberiaActualizarEntidadExistenteAlGuardarAlquilerFinalizado() {
        Alquiler alquiler = new Alquiler("BIC-001", "Ana", INICIO, 2, TarifaBicicleta.MONTAÑA);
        alquiler.finalizar(INICIO.plusHours(3).plusMinutes(20));
        EntidadJpaAlquiler entidadExistente = new EntidadJpaAlquiler(
                "BIC-001", "Ana", INICIO, 2, 5000L, EstadoAlquiler.ACTIVO,
                null, null, null, null, null);
        when(repositorioJpaAlquileres.buscarActivoPorCodigoBicicleta("BIC-001"))
                .thenReturn(Optional.of(entidadExistente));
        when(repositorioJpaAlquileres.save(entidadExistente)).thenReturn(entidadExistente);

        adaptador.guardar(alquiler);

        verify(repositorioJpaAlquileres).save(entidadExistente);
        assertEquals(EstadoAlquiler.FINALIZADO, entidadExistente.getEstado());
        assertEquals(4, entidadExistente.getHorasFacturables());
        assertEquals(20000L, entidadExistente.getCostoBase());
        assertEquals(5000L, entidadExistente.getMulta());
        assertEquals(25000L, entidadExistente.getTotal());
    }

    @Test
    void deberiaConvertirEntidadActivaADominio() {
        EntidadJpaAlquiler entidad = new EntidadJpaAlquiler(
                "BIC-001", "Ana", INICIO, 2, 7500L, EstadoAlquiler.ACTIVO,
                null, null, null, null, null);
        when(repositorioJpaAlquileres.buscarActivoPorCodigoBicicleta("BIC-001"))
                .thenReturn(Optional.of(entidad));

        Alquiler resultado = adaptador.buscarActivoPorCodigoBicicleta("BIC-001").orElseThrow();

        assertEquals("BIC-001", resultado.getCodigoBicicleta());
        assertEquals(TarifaBicicleta.ELÉCTRICA, resultado.getTarifaBicicleta());
        assertEquals(EstadoAlquiler.ACTIVO, resultado.getEstado());
    }

    @Test
    void deberiaConvertirTodosLosAlquileresDeUnaBicicleta() {
        EntidadJpaAlquiler primerAlquiler = new EntidadJpaAlquiler(
                "BIC-001", "Ana", INICIO, 2, 3500L, EstadoAlquiler.FINALIZADO,
                INICIO.plusHours(1), 1, 3500L, 0L, 3500L);
        EntidadJpaAlquiler segundoAlquiler = new EntidadJpaAlquiler(
                "BIC-001", "Luis", INICIO.plusDays(1), 2, 5000L, EstadoAlquiler.ACTIVO,
                null, null, null, null, null);
        when(repositorioJpaAlquileres.buscarPorCodigoBicicleta("BIC-001"))
                .thenReturn(List.of(primerAlquiler, segundoAlquiler));

        List<Alquiler> resultado = adaptador.buscarPorCodigoBicicleta("BIC-001");

        assertEquals(2, resultado.size());
        assertEquals("Ana", resultado.get(0).getNombreCliente());
        assertEquals("Luis", resultado.get(1).getNombreCliente());
        }
}