package com.alquiler_de_bicicletas.infraestructura.persistencia.adaptador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.alquiler_de_bicicletas.dominio.modelo.Alquiler;
import com.alquiler_de_bicicletas.dominio.modelo.EstadoBicicleta;
import com.alquiler_de_bicicletas.dominio.modelo.EstadoAlquiler;
import com.alquiler_de_bicicletas.dominio.modelo.TarifaBicicleta;
import com.alquiler_de_bicicletas.dominio.modelo.TipoBicicleta;
import com.alquiler_de_bicicletas.infraestructura.persistencia.entidad.EntidadJpaBicicleta;
import com.alquiler_de_bicicletas.infraestructura.persistencia.entidad.EntidadJpaAlquiler;
import com.alquiler_de_bicicletas.infraestructura.persistencia.repositorio.RepositorioJpaBicicletas;
import com.alquiler_de_bicicletas.infraestructura.persistencia.repositorio.RepositorioJpaAlquileres;

@SpringBootTest
@Transactional
class AdaptadorPersistenciaAlquileresIntegracionTest {

    private static final LocalDateTime INICIO = LocalDateTime.of(2026, 1, 1, 10, 0);

    @Autowired
    private AdaptadorPersistenciaAlquileres adaptador;

    @Autowired
    private RepositorioJpaAlquileres repositorioJpaAlquileres;

        @Autowired
        private RepositorioJpaBicicletas repositorioJpaBicicletas;

    @Test
    void deberiaInsertarYActualizarConservandoElIdTecnico() {
                registrarBicicleta("BIC-INT-001", TipoBicicleta.MONTAÑA);
        Alquiler alquilerActivo = new Alquiler(
                "BIC-INT-001", "Ana", INICIO, 2, TarifaBicicleta.MONTAÑA);

        Alquiler resultadoInicial = adaptador.guardar(alquilerActivo);

        EntidadJpaAlquiler entidadInicial = repositorioJpaAlquileres
                .buscarPorCodigoBicicletaYFechaHoraInicio("BIC-INT-001", INICIO)
                .orElseThrow();
        Long idTecnico = entidadInicial.getId();
        assertNotNull(idTecnico);
        assertEquals(EstadoAlquiler.ACTIVO, resultadoInicial.getEstado());

        alquilerActivo.finalizar(INICIO.plusHours(3).plusMinutes(20));
        Alquiler resultadoFinal = adaptador.guardar(alquilerActivo);

        EntidadJpaAlquiler entidadFinal = repositorioJpaAlquileres
                .buscarPorCodigoBicicletaYFechaHoraInicio("BIC-INT-001", INICIO)
                .orElseThrow();
        assertEquals(idTecnico, entidadFinal.getId());
        assertEquals(EstadoAlquiler.FINALIZADO, entidadFinal.getEstado());
        assertEquals(4, entidadFinal.getHorasFacturables());
        assertEquals(20000L, entidadFinal.getCostoBase());
        assertEquals(5000L, entidadFinal.getMulta());
        assertEquals(25000L, entidadFinal.getTotal());
        assertEquals(EstadoAlquiler.FINALIZADO, resultadoFinal.getEstado());
        assertTrue(repositorioJpaAlquileres.buscarActivoPorCodigoBicicleta("BIC-INT-001").isEmpty());
    }

    @Test
    void deberiaPersistirYRecuperarMultiplesAlquileresOrdenadosPorFechaDeInicio() {
                registrarBicicleta("BIC-INT-002", TipoBicicleta.URBANA);
        LocalDateTime inicioAntiguo = INICIO;
        LocalDateTime inicioReciente = INICIO.plusDays(1);
        Alquiler alquilerReciente = new Alquiler(
                "BIC-INT-002", "Luis", inicioReciente, 2, TarifaBicicleta.URBANA);
        Alquiler alquilerAntiguo = new Alquiler(
                "BIC-INT-002", "Ana", inicioAntiguo, 2, TarifaBicicleta.URBANA);

        adaptador.guardar(alquilerAntiguo);
        alquilerAntiguo.finalizar(inicioAntiguo.plusHours(1));
        adaptador.guardar(alquilerAntiguo);
        repositorioJpaAlquileres.flush();
        adaptador.guardar(alquilerReciente);

        List<Alquiler> historial = adaptador.buscarPorCodigoBicicleta("BIC-INT-002");

        assertEquals(2, historial.size());
        assertEquals("Ana", historial.get(0).getNombreCliente());
        assertEquals(inicioAntiguo, historial.get(0).getFechaHoraInicio());
        assertEquals("Luis", historial.get(1).getNombreCliente());
        assertEquals(inicioReciente, historial.get(1).getFechaHoraInicio());
    }

        private void registrarBicicleta(String codigo, TipoBicicleta tipo) {
                repositorioJpaBicicletas.save(
                                new EntidadJpaBicicleta(codigo, tipo, EstadoBicicleta.DISPONIBLE));
        }

}