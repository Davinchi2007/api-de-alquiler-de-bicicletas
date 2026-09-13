package com.alquiler_de_bicicletas.infraestructura.configuracion;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.alquiler_de_bicicletas.aplicacion.puerto.entrada.PuertoEntradaGestionBicicletas;
import com.alquiler_de_bicicletas.infraestructura.controlador.ControladorBicicletas;
import com.alquiler_de_bicicletas.infraestructura.controlador.mapeador.MapeadorBicicletaRespuesta;
import com.alquiler_de_bicicletas.infraestructura.excepcion.ManejadorGlobalExcepciones;

@WebMvcTest(ControladorBicicletas.class)
@Import({SeguridadConfiguracion.class,
        MapeadorBicicletaRespuesta.class, ManejadorGlobalExcepciones.class})
@TestPropertySource(properties = "app.seguridad.cors.origenes-permitidos=http://localhost:3000")
class SeguridadConfiguracionTest {

    @Autowired
    private MockMvc mockMvc;

        @MockitoBean
        private PuertoEntradaGestionBicicletas puertoEntradaBicicletas;

    @Test
    void deberiaPermitirOrigenConfiguradoYEnviarHeadersDeSeguridad() throws Exception {
        mockMvc.perform(get("/api/bicicletas/disponibles")
                        .header(HttpHeaders.ORIGIN, "http://localhost:3000"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
                        "http://localhost:3000"))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().string("Content-Security-Policy",
                        "default-src 'none'; frame-ancestors 'none'"));
    }

    @Test
    void deberiaResponderPreflightParaOrigenConfigurado() throws Exception {
        mockMvc.perform(options("/api/bicicletas")
                        .header(HttpHeaders.ORIGIN, "http://localhost:3000")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
                        "http://localhost:3000"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS,
                        "GET,POST,OPTIONS"));
    }

    @Test
    void deberiaRechazarOrigenNoConfigurado() throws Exception {
        mockMvc.perform(get("/api/bicicletas/disponibles")
                        .header(HttpHeaders.ORIGIN, "https://origen-no-permitido.example"))
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
    }
}
