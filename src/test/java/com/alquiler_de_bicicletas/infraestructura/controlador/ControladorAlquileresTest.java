package com.alquiler_de_bicicletas.infraestructura.controlador;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.alquiler_de_bicicletas.aplicacion.puerto.entrada.PuertoEntradaConsultarHistorialAlquileres;
import com.alquiler_de_bicicletas.aplicacion.excepcion.AlquilerNoEncontradoException;
import com.alquiler_de_bicicletas.aplicacion.puerto.entrada.PuertoEntradaIniciarAlquiler;
import com.alquiler_de_bicicletas.aplicacion.puerto.entrada.PuertoEntradaFinalizarAlquiler;
import com.alquiler_de_bicicletas.dominio.modelo.Alquiler;
import com.alquiler_de_bicicletas.dominio.modelo.TarifaBicicleta;
import com.alquiler_de_bicicletas.dominio.excepcion.AlquilerYaFinalizadoException;
import com.alquiler_de_bicicletas.dominio.excepcion.FechaDevolucionInvalidaException;
import com.alquiler_de_bicicletas.dominio.excepcion.TransicionEstadoBicicletaInvalidaException;
import com.alquiler_de_bicicletas.infraestructura.controlador.mapeador.MapeadorHistorialAlquiler;
import com.alquiler_de_bicicletas.infraestructura.controlador.mapeador.MapeadorAlquilerRespuesta;
import com.alquiler_de_bicicletas.infraestructura.configuracion.SeguridadConfiguracion;
import com.alquiler_de_bicicletas.infraestructura.excepcion.ManejadorGlobalExcepciones;
import com.alquiler_de_bicicletas.aplicacion.excepcion.BicicletaNoEncontradaException;

@WebMvcTest(ControladorAlquileres.class)
@Import({MapeadorHistorialAlquiler.class, MapeadorAlquilerRespuesta.class,
        ManejadorGlobalExcepciones.class, SeguridadConfiguracion.class})
class ControladorAlquileresTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PuertoEntradaConsultarHistorialAlquileres puertoEntradaHistorial;

    @MockitoBean
    private PuertoEntradaIniciarAlquiler puertoEntradaIniciarAlquiler;

    @MockitoBean
    private PuertoEntradaFinalizarAlquiler puertoEntradaFinalizarAlquiler;

    @Test
    void deberiaFinalizarAlquilerYRetornarOkConLosDatosEconomicos() throws Exception {
        Alquiler alquiler = new Alquiler("BIC-002", "David",
                LocalDateTime.of(2026, 9, 11, 10, 0), 2, TarifaBicicleta.MONTAÑA);
        alquiler.finalizar(LocalDateTime.of(2026, 9, 11, 13, 20));
        when(puertoEntradaFinalizarAlquiler.finalizarAlquiler(
                "BIC-002", LocalDateTime.of(2026, 9, 11, 13, 20)))
                .thenReturn(alquiler);

        mockMvc.perform(post("/api/alquileres/BIC-002/finalizar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fechaHoraDevolucion\":\"2026-09-11T13:20:00\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoBicicleta").value("BIC-002"))
                .andExpect(jsonPath("$.fechaHoraDevolucion").exists())
                .andExpect(jsonPath("$.horasFacturables").value(4))
                .andExpect(jsonPath("$.costoBase").value(20000))
                .andExpect(jsonPath("$.multa").value(5000))
                .andExpect(jsonPath("$.total").value(25000));

        verify(puertoEntradaFinalizarAlquiler).finalizarAlquiler(
                "BIC-002", LocalDateTime.of(2026, 9, 11, 13, 20));
    }

    @Test
    void deberiaRetornarBadRequestCuandoLaFechaDeDevolucionEsNula() throws Exception {
        mockMvc.perform(post("/api/alquileres/BIC-002/finalizar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fechaHoraDevolucion\":null}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void deberiaRetornarNotFoundCuandoElAlquilerNoExiste() throws Exception {
        when(puertoEntradaFinalizarAlquiler.finalizarAlquiler(
                "BIC-999", LocalDateTime.of(2026, 9, 11, 13, 20)))
                .thenThrow(new AlquilerNoEncontradoException("BIC-999"));

        mockMvc.perform(post("/api/alquileres/BIC-999/finalizar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fechaHoraDevolucion\":\"2026-09-11T13:20:00\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deberiaRetornarConflictCuandoElAlquilerYaFueFinalizadoAlFinalizar() throws Exception {
        when(puertoEntradaFinalizarAlquiler.finalizarAlquiler(
                "BIC-002", LocalDateTime.of(2026, 9, 11, 13, 20)))
                .thenThrow(new AlquilerYaFinalizadoException("El alquiler ya fue finalizado"));

        mockMvc.perform(post("/api/alquileres/BIC-002/finalizar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fechaHoraDevolucion\":\"2026-09-11T13:20:00\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void deberiaRetornarBadRequestCuandoLaFechaDevolucionEsInvalidaAlFinalizar() throws Exception {
        when(puertoEntradaFinalizarAlquiler.finalizarAlquiler(
                "BIC-002", LocalDateTime.of(2026, 9, 11, 9, 0)))
                .thenThrow(new FechaDevolucionInvalidaException(
                        "La fecha de devolución debe ser posterior a la fecha de inicio"));

        mockMvc.perform(post("/api/alquileres/BIC-002/finalizar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fechaHoraDevolucion\":\"2026-09-11T09:00:00\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void deberiaIniciarAlquilerYRetornarCreated() throws Exception {
        Alquiler alquiler = new Alquiler("BIC-002", "David",
                LocalDateTime.of(2026, 9, 11, 10, 0), 2, TarifaBicicleta.MONTAÑA);
        when(puertoEntradaIniciarAlquiler.iniciarAlquiler(
                "BIC-002", "David", LocalDateTime.of(2026, 9, 11, 10, 0), 2))
                .thenReturn(alquiler);

        mockMvc.perform(post("/api/alquileres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigoBicicleta\":\"BIC-002\",\"nombreCliente\":\"David\","
                                + "\"fechaHoraInicio\":\"2026-09-11T10:00:00\",\"duracionEstimadaHoras\":2}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigoBicicleta").value("BIC-002"))
                .andExpect(jsonPath("$.nombreCliente").value("David"))
                .andExpect(jsonPath("$.estado").value("ACTIVO"))
                .andExpect(jsonPath("$.tarifaBicicleta").value("MONTAÑA"));

        verify(puertoEntradaIniciarAlquiler).iniciarAlquiler(
                "BIC-002", "David", LocalDateTime.of(2026, 9, 11, 10, 0), 2);
    }

    @Test
    void deberiaRetornarBadRequestCuandoLaSolicitudDeAlquilerEsInvalida() throws Exception {
        mockMvc.perform(post("/api/alquileres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigoBicicleta\":\"\",\"nombreCliente\":\"\","
                                + "\"fechaHoraInicio\":null,\"duracionEstimadaHoras\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

        @Test
        void deberiaRetornarBadRequestCuandoElJsonEsInvalido() throws Exception {
                mockMvc.perform(post("/api/alquileres")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("{\"codigoBicicleta\":\"BIC-002\",}"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        void deberiaRetornarBadRequestCuandoLaFechaTieneFormatoInvalido() throws Exception {
                mockMvc.perform(post("/api/alquileres")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("{\"codigoBicicleta\":\"BIC-002\",\"nombreCliente\":\"David\","
                                                                + "\"fechaHoraInicio\":\"fecha-invalida\",\"duracionEstimadaHoras\":2}"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        void deberiaRetornarBadRequestCuandoLaDuracionNoEsNumerica() throws Exception {
                mockMvc.perform(post("/api/alquileres")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("{\"codigoBicicleta\":\"BIC-002\",\"nombreCliente\":\"David\","
                                                                + "\"fechaHoraInicio\":\"2026-09-11T10:00:00\","
                                                                + "\"duracionEstimadaHoras\":\"dos\"}"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400));
        }

    @Test
    void deberiaRetornarNotFoundCuandoLaBicicletaNoExisteAlIniciarAlquiler() throws Exception {
        when(puertoEntradaIniciarAlquiler.iniciarAlquiler(
                "BIC-999", "David", LocalDateTime.of(2026, 9, 11, 10, 0), 2))
                .thenThrow(new BicicletaNoEncontradaException("BIC-999"));

        mockMvc.perform(post("/api/alquileres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigoBicicleta\":\"BIC-999\",\"nombreCliente\":\"David\","
                                + "\"fechaHoraInicio\":\"2026-09-11T10:00:00\",\"duracionEstimadaHoras\":2}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deberiaRetornarConflictCuandoLaBicicletaNoEstaDisponibleAlIniciarAlquiler() throws Exception {
        when(puertoEntradaIniciarAlquiler.iniciarAlquiler(
                "BIC-002", "David", LocalDateTime.of(2026, 9, 11, 10, 0), 2))
                .thenThrow(new TransicionEstadoBicicletaInvalidaException(
                        "La bicicleta BIC-002 no puede alquilarse porque está ALQUILADA"));

        mockMvc.perform(post("/api/alquileres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigoBicicleta\":\"BIC-002\",\"nombreCliente\":\"David\","
                                + "\"fechaHoraInicio\":\"2026-09-11T10:00:00\",\"duracionEstimadaHoras\":2}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void deberiaRetornarHistorialConDuracionRealYMontoDeMulta() throws Exception {
        Alquiler alquiler = new Alquiler("BIC-001", "Ana",
                LocalDateTime.of(2026, 1, 1, 10, 0), 2, TarifaBicicleta.MONTAÑA);
        alquiler.finalizar(LocalDateTime.of(2026, 1, 1, 13, 20));
        when(puertoEntradaHistorial.consultarHistorialAlquileres("BIC-001"))
                .thenReturn(List.of(alquiler));

        mockMvc.perform(get("/api/alquileres/bicicletas/BIC-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreCliente").value("Ana"))
                .andExpect(jsonPath("$[0].duracionRealMinutos").value(200))
                .andExpect(jsonPath("$[0].costoTotal").value(25000))
                .andExpect(jsonPath("$[0].tuvoMulta").value(true));
    }

    @Test
    void deberiaRetornarAlquilerActivoSinFechaDeFinNiCostoTotal() throws Exception {
        Alquiler alquiler = new Alquiler("BIC-001", "Luis",
                LocalDateTime.of(2026, 1, 1, 10, 0), 2, TarifaBicicleta.URBANA);
        when(puertoEntradaHistorial.consultarHistorialAlquileres("BIC-001"))
                .thenReturn(List.of(alquiler));

        mockMvc.perform(get("/api/alquileres/bicicletas/BIC-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fechaHoraDevolucion").doesNotExist())
                .andExpect(jsonPath("$[0].duracionRealMinutos").doesNotExist())
                .andExpect(jsonPath("$[0].costoTotal").doesNotExist())
                .andExpect(jsonPath("$[0].tuvoMulta").value(false));
    }

    @Test
    void deberiaRetornarListaVaciaCuandoLaBicicletaNoTieneHistorial() throws Exception {
        when(puertoEntradaHistorial.consultarHistorialAlquileres("BIC-001"))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/alquileres/bicicletas/BIC-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void deberiaRetornarAlquilerFinalizadoSinMulta() throws Exception {
        Alquiler alquiler = new Alquiler("BIC-001", "Ana",
                LocalDateTime.of(2026, 1, 1, 10, 0), 2, TarifaBicicleta.MONTAÑA);
        alquiler.finalizar(LocalDateTime.of(2026, 1, 1, 11, 30));
        when(puertoEntradaHistorial.consultarHistorialAlquileres("BIC-001"))
                .thenReturn(List.of(alquiler));

        mockMvc.perform(get("/api/alquileres/bicicletas/BIC-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fechaHoraDevolucion").exists())
                .andExpect(jsonPath("$[0].duracionRealMinutos").value(90))
                .andExpect(jsonPath("$[0].costoTotal").value(10000))
                .andExpect(jsonPath("$[0].tuvoMulta").value(false));
    }

    @Test
    void deberiaRetornarNoEncontradoCuandoLaBicicletaNoExiste() throws Exception {
        when(puertoEntradaHistorial.consultarHistorialAlquileres("BIC-999"))
                .thenThrow(new BicicletaNoEncontradaException("BIC-999"));

        mockMvc.perform(get("/api/alquileres/bicicletas/BIC-999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deberiaRetornarNoEncontradoCuandoNoExisteAlquilerActivo() throws Exception {
        when(puertoEntradaHistorial.consultarHistorialAlquileres("BIC-001"))
                .thenThrow(new AlquilerNoEncontradoException("BIC-001"));

        mockMvc.perform(get("/api/alquileres/bicicletas/BIC-001"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("No se encontró un alquiler activo para la bicicleta BIC-001"));
    }

    @Test
    void deberiaRetornarConflictoCuandoElAlquilerYaFueFinalizado() throws Exception {
        when(puertoEntradaHistorial.consultarHistorialAlquileres("BIC-001"))
                .thenThrow(new AlquilerYaFinalizadoException("El alquiler ya fue finalizado"));

        mockMvc.perform(get("/api/alquileres/bicicletas/BIC-001"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("El alquiler ya fue finalizado"));
    }

    @Test
    void deberiaRetornarConflictoCuandoLaTransicionDeBicicletaEsInvalida() throws Exception {
        when(puertoEntradaHistorial.consultarHistorialAlquileres("BIC-001"))
                .thenThrow(new TransicionEstadoBicicletaInvalidaException(
                        "La bicicleta BIC-001 no puede alquilarse porque está ALQUILADA"));

        mockMvc.perform(get("/api/alquileres/bicicletas/BIC-001"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message")
                        .value("La bicicleta BIC-001 no puede alquilarse porque está ALQUILADA"));
    }

    @Test
    void deberiaRetornarSolicitudInvalidaCuandoLaFechaDevolucionEsInvalida() throws Exception {
        when(puertoEntradaHistorial.consultarHistorialAlquileres("BIC-001"))
                .thenThrow(new FechaDevolucionInvalidaException(
                        "La fecha de devolución debe ser posterior a la fecha de inicio"));

        mockMvc.perform(get("/api/alquileres/bicicletas/BIC-001"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("La fecha de devolución debe ser posterior a la fecha de inicio"));
    }
}