package com.alquiler_de_bicicletas.infraestructura.filtro;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.slf4j.MDC;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.alquiler_de_bicicletas.aplicacion.puerto.entrada.PuertoEntradaGestionBicicletas;
import com.alquiler_de_bicicletas.infraestructura.configuracion.SeguridadConfiguracion;
import com.alquiler_de_bicicletas.infraestructura.controlador.ControladorBicicletas;
import com.alquiler_de_bicicletas.infraestructura.controlador.mapeador.MapeadorBicicletaRespuesta;
import com.alquiler_de_bicicletas.infraestructura.excepcion.ManejadorGlobalExcepciones;

@WebMvcTest(ControladorBicicletas.class)
@Import({SeguridadConfiguracion.class, FiltroIdCorrelacion.class,
        MapeadorBicicletaRespuesta.class, ManejadorGlobalExcepciones.class})
@TestPropertySource(properties = "app.seguridad.cors.origenes-permitidos=http://localhost:3000")
class FiltroIdCorrelacionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PuertoEntradaGestionBicicletas puertoEntradaBicicletas;

    @Test
    void deberiaGenerarIdDeCorrelacionYLimpiarElMdc() throws Exception {
        mockMvc.perform(get("/api/bicicletas/disponibles"))
                .andExpect(status().isOk())
                .andExpect(header().exists(FiltroIdCorrelacion.NOMBRE_HEADER));

        assertNull(MDC.get(FiltroIdCorrelacion.CLAVE_MDC));
    }

    @Test
    void deberiaPropagarIdDeCorrelacionRecibido() throws Exception {
        String idCorrelacion = "correlacion-prueba-001";

        mockMvc.perform(get("/api/bicicletas/disponibles")
                        .header(FiltroIdCorrelacion.NOMBRE_HEADER, idCorrelacion))
                .andExpect(status().isOk())
                .andExpect(header().string(FiltroIdCorrelacion.NOMBRE_HEADER, idCorrelacion));
    }
}