package com.alquiler_de_bicicletas.aplicacion.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.alquiler_de_bicicletas.aplicacion.puerto.salida.PuertoSalidaRepositorioAlquileres;
import com.alquiler_de_bicicletas.dominio.modelo.Bicicleta;
import com.alquiler_de_bicicletas.dominio.modelo.EstadoBicicleta;
import com.alquiler_de_bicicletas.dominio.modelo.TipoBicicleta;
import com.alquiler_de_bicicletas.infraestructura.persistencia.adaptador.AdaptadorPersistenciaBicicletas;
import com.alquiler_de_bicicletas.infraestructura.persistencia.repositorio.RepositorioJpaBicicletas;

@SpringBootTest
class ServicioGestionAlquileresIntegracionTest {

    @Autowired
    private ServicioGestionAlquileres servicioGestionAlquileres;

    @Autowired
    private PuertoSalidaRepositorioAlquileres repositorioAlquileres;

    @Autowired
    private RepositorioJpaBicicletas repositorioJpaBicicletas;

    @MockitoSpyBean
    private AdaptadorPersistenciaBicicletas adaptadorPersistenciaBicicletas;

    @Test
    void deberiaRevertirAlquilerYBicicletaCuandoFallaElSegundoGuardado() {
        String codigoBicicleta = "BIC-ROLLBACK-001";
        adaptadorPersistenciaBicicletas.guardar(
                new Bicicleta(codigoBicicleta, TipoBicicleta.URBANA, EstadoBicicleta.DISPONIBLE));

        doAnswer(invocacion -> {
            invocacion.callRealMethod();
            throw new IllegalStateException("Fallo controlado al guardar bicicleta");
        }).when(adaptadorPersistenciaBicicletas).guardar(argThat(bicicleta ->
                codigoBicicleta.equals(bicicleta.getCodigo())
                        && bicicleta.getEstado() == EstadoBicicleta.ALQUILADA));

        InvalidDataAccessApiUsageException exception = assertThrows(
                InvalidDataAccessApiUsageException.class,
                () -> servicioGestionAlquileres.iniciarAlquiler(
                codigoBicicleta,
                "Ana",
                java.time.LocalDateTime.of(2026, 9, 11, 10, 0),
                2));

        assertEquals(IllegalStateException.class, exception.getCause().getClass());
        assertEquals("Fallo controlado al guardar bicicleta", exception.getCause().getMessage());

        assertTrue(repositorioAlquileres.buscarActivoPorCodigoBicicleta(codigoBicicleta).isEmpty());
        assertEquals(EstadoBicicleta.DISPONIBLE,
                repositorioJpaBicicletas.buscarPorCodigo(codigoBicicleta).orElseThrow().getEstado());
    }
}