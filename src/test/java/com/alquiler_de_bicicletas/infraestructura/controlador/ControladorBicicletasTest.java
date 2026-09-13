package com.alquiler_de_bicicletas.infraestructura.controlador;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.alquiler_de_bicicletas.aplicacion.excepcion.BicicletaNoEncontradaException;
import com.alquiler_de_bicicletas.aplicacion.excepcion.BicicletaYaExisteException;
import com.alquiler_de_bicicletas.aplicacion.puerto.entrada.PuertoEntradaGestionBicicletas;
import com.alquiler_de_bicicletas.dominio.modelo.Bicicleta;
import com.alquiler_de_bicicletas.dominio.modelo.EstadoBicicleta;
import com.alquiler_de_bicicletas.dominio.modelo.TipoBicicleta;
import com.alquiler_de_bicicletas.infraestructura.controlador.mapeador.MapeadorBicicletaRespuesta;
import com.alquiler_de_bicicletas.infraestructura.excepcion.ManejadorGlobalExcepciones;

@WebMvcTest(ControladorBicicletas.class)
@Import({MapeadorBicicletaRespuesta.class, ManejadorGlobalExcepciones.class})
class ControladorBicicletasTest {

    @Autowired
    private MockMvc mockMvc;

        @MockitoBean
        private PuertoEntradaGestionBicicletas puertoEntradaBicicletas;

    @Test
        void deberiaCrearBicicletaCuandoLaSolicitudEsValida() throws Exception {
                when(puertoEntradaBicicletas.registrar(
                                "BIC-001", TipoBicicleta.URBANA, EstadoBicicleta.DISPONIBLE))
                .thenReturn(bicicleta("BIC-001", TipoBicicleta.URBANA));

        mockMvc.perform(post("/api/bicicletas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"BIC-001\",\"tipo\":\"URBANA\",\"estado\":\"DISPONIBLE\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value("BIC-001"))
                .andExpect(jsonPath("$.tipo").value("URBANA"))
                .andExpect(jsonPath("$.estado").value("DISPONIBLE"));
    }

    @Test
        void deberiaRetornarSolicitudInvalidaCuandoElCodigoEstaVacio() throws Exception {
        mockMvc.perform(post("/api/bicicletas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"   \",\"tipo\":\"URBANA\",\"estado\":\"DISPONIBLE\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
        void deberiaRetornarSolicitudInvalidaCuandoElTipoEsNulo() throws Exception {
        mockMvc.perform(post("/api/bicicletas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"BIC-001\",\"tipo\":null,\"estado\":\"DISPONIBLE\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
        void deberiaRetornarConflictoCuandoElCodigoYaExiste() throws Exception {
                when(puertoEntradaBicicletas.registrar(
                                "BIC-001", TipoBicicleta.URBANA, EstadoBicicleta.DISPONIBLE))
                .thenThrow(new BicicletaYaExisteException("BIC-001"));

        mockMvc.perform(post("/api/bicicletas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"BIC-001\",\"tipo\":\"URBANA\",\"estado\":\"DISPONIBLE\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
        void deberiaRetornarBicicletaCuandoElCodigoExiste() throws Exception {
        when(puertoEntradaBicicletas.buscarPorCodigo("BIC-001"))
                .thenReturn(bicicleta("BIC-001", TipoBicicleta.URBANA));

        mockMvc.perform(get("/api/bicicletas/BIC-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("BIC-001"));
    }

    @Test
        void deberiaRetornarNoEncontradoCuandoElCodigoNoExiste() throws Exception {
        when(puertoEntradaBicicletas.buscarPorCodigo("BIC-999"))
                .thenThrow(new BicicletaNoEncontradaException("BIC-999"));

        mockMvc.perform(get("/api/bicicletas/BIC-999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
        void deberiaRetornarBicicletasDisponibles() throws Exception {
        when(puertoEntradaBicicletas.obtenerDisponibles())
                .thenReturn(List.of(bicicleta("BIC-001", TipoBicicleta.URBANA)));

        mockMvc.perform(get("/api/bicicletas/disponibles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value("BIC-001"));

        verify(puertoEntradaBicicletas).obtenerDisponibles();
    }

    @Test
        void deberiaFiltrarBicicletasDisponiblesPorTipo() throws Exception {
        when(puertoEntradaBicicletas.obtenerDisponiblesPorTipo(TipoBicicleta.MONTAÑA))
                .thenReturn(List.of(bicicleta("BIC-002", TipoBicicleta.MONTAÑA)));

        mockMvc.perform(get("/api/bicicletas/disponibles").param("tipo", "MONTAÑA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipo").value("MONTAÑA"));

        verify(puertoEntradaBicicletas).obtenerDisponiblesPorTipo(TipoBicicleta.MONTAÑA);
    }

    @Test
        void deberiaRetornarSolicitudInvalidaCuandoElTipoNoEsValido() throws Exception {
        mockMvc.perform(get("/api/bicicletas/disponibles").param("tipo", "INVALIDO"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    private Bicicleta bicicleta(String codigo, TipoBicicleta tipo) {
        return new Bicicleta(codigo, tipo, EstadoBicicleta.DISPONIBLE);
    }
}