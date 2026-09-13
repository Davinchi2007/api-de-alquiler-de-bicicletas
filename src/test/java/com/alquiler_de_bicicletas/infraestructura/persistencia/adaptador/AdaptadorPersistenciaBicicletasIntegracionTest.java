package com.alquiler_de_bicicletas.infraestructura.persistencia.adaptador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.alquiler_de_bicicletas.dominio.modelo.Bicicleta;
import com.alquiler_de_bicicletas.dominio.modelo.EstadoBicicleta;
import com.alquiler_de_bicicletas.dominio.modelo.TipoBicicleta;
import com.alquiler_de_bicicletas.infraestructura.persistencia.entidad.EntidadJpaBicicleta;
import com.alquiler_de_bicicletas.infraestructura.persistencia.repositorio.RepositorioJpaBicicletas;

@SpringBootTest
@Transactional
class AdaptadorPersistenciaBicicletasIntegracionTest {

    @Autowired
    private AdaptadorPersistenciaBicicletas adaptador;

    @Autowired
    private RepositorioJpaBicicletas repositorioJpaBicicletas;

    @Test
    void deberiaActualizarLaMismaFilaAlAlquilarYDevolverUnaBicicleta() {
        Bicicleta bicicletaDisponible = new Bicicleta(
                "BIC-INT-BIC-001", TipoBicicleta.URBANA, EstadoBicicleta.DISPONIBLE);

        adaptador.guardar(bicicletaDisponible);

        EntidadJpaBicicleta entidadInicial = buscarEntidad();
        Long idTecnico = entidadInicial.getId();
        assertNotNull(idTecnico);

        Bicicleta bicicletaAlquilada = new Bicicleta(
                "BIC-INT-BIC-001", TipoBicicleta.URBANA, EstadoBicicleta.ALQUILADA);
        adaptador.guardar(bicicletaAlquilada);

        EntidadJpaBicicleta entidadAlquilada = buscarEntidad();
        assertEquals(idTecnico, entidadAlquilada.getId());
        assertEquals(EstadoBicicleta.ALQUILADA, entidadAlquilada.getEstado());
        assertEquals(1, contarFilasDeLaBicicleta());

        Bicicleta bicicletaDevuelta = new Bicicleta(
                "BIC-INT-BIC-001", TipoBicicleta.URBANA, EstadoBicicleta.DISPONIBLE);
        adaptador.guardar(bicicletaDevuelta);

        EntidadJpaBicicleta entidadDevuelta = buscarEntidad();
        assertEquals(idTecnico, entidadDevuelta.getId());
        assertEquals(EstadoBicicleta.DISPONIBLE, entidadDevuelta.getEstado());
        assertEquals(1, contarFilasDeLaBicicleta());
    }

    private EntidadJpaBicicleta buscarEntidad() {
        return repositorioJpaBicicletas.buscarPorCodigo("BIC-INT-BIC-001").orElseThrow();
    }

    private long contarFilasDeLaBicicleta() {
        List<EntidadJpaBicicleta> entidades = repositorioJpaBicicletas.findAll();
        return entidades.stream()
                .filter(entidad -> "BIC-INT-BIC-001".equals(entidad.getCodigo()))
                .count();
    }
}