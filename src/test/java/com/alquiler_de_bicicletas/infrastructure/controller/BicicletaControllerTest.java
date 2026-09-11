package com.alquiler_de_bicicletas.infrastructure.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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

import com.alquiler_de_bicicletas.application.exception.BicicletaNoEncontradaException;
import com.alquiler_de_bicicletas.application.exception.BicicletaYaExisteException;
import com.alquiler_de_bicicletas.application.port.in.BicicletaUseCase;
import com.alquiler_de_bicicletas.domain.model.Bicicleta;
import com.alquiler_de_bicicletas.domain.model.EstadoBicicleta;
import com.alquiler_de_bicicletas.domain.model.TipoBicicleta;
import com.alquiler_de_bicicletas.infrastructure.controller.mapper.BicicletaDtoMapper;
import com.alquiler_de_bicicletas.infrastructure.exception.GlobalExceptionHandler;

@WebMvcTest(BicicletaController.class)
@Import({BicicletaDtoMapper.class, GlobalExceptionHandler.class})
class BicicletaControllerTest {

    @Autowired
    private MockMvc mockMvc;

        @MockitoBean
    private BicicletaUseCase bicicletaUseCase;

    @Test
    void should_CreateBicicleta_When_RequestIsValid() throws Exception {
        when(bicicletaUseCase.registrar("BIC-001", TipoBicicleta.URBANA))
                .thenReturn(bicicleta("BIC-001", TipoBicicleta.URBANA));

        mockMvc.perform(post("/api/bicicletas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"BIC-001\",\"tipo\":\"URBANA\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value("BIC-001"))
                .andExpect(jsonPath("$.tipo").value("URBANA"))
                .andExpect(jsonPath("$.estado").value("DISPONIBLE"));
    }

    @Test
    void should_ReturnBadRequest_When_CodigoIsBlank() throws Exception {
        mockMvc.perform(post("/api/bicicletas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"   \",\"tipo\":\"URBANA\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void should_ReturnBadRequest_When_TipoIsNull() throws Exception {
        mockMvc.perform(post("/api/bicicletas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"BIC-001\",\"tipo\":null}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void should_ReturnConflict_When_CodigoAlreadyExists() throws Exception {
        when(bicicletaUseCase.registrar("BIC-001", TipoBicicleta.URBANA))
                .thenThrow(new BicicletaYaExisteException("BIC-001"));

        mockMvc.perform(post("/api/bicicletas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"BIC-001\",\"tipo\":\"URBANA\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void should_ReturnBicicleta_When_CodigoExists() throws Exception {
        when(bicicletaUseCase.buscarPorCodigo("BIC-001"))
                .thenReturn(bicicleta("BIC-001", TipoBicicleta.URBANA));

        mockMvc.perform(get("/api/bicicletas/BIC-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("BIC-001"));
    }

    @Test
    void should_ReturnNotFound_When_CodigoDoesNotExist() throws Exception {
        when(bicicletaUseCase.buscarPorCodigo("BIC-999"))
                .thenThrow(new BicicletaNoEncontradaException("BIC-999"));

        mockMvc.perform(get("/api/bicicletas/BIC-999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void should_ReturnAvailableBicicletas() throws Exception {
        when(bicicletaUseCase.obtenerDisponibles())
                .thenReturn(List.of(bicicleta("BIC-001", TipoBicicleta.URBANA)));

        mockMvc.perform(get("/api/bicicletas/disponibles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value("BIC-001"));

        verify(bicicletaUseCase).obtenerDisponibles();
    }

    @Test
    void should_FilterAvailableBicicletasByTipo() throws Exception {
        when(bicicletaUseCase.obtenerDisponiblesPorTipo(TipoBicicleta.MONTAÑA))
                .thenReturn(List.of(bicicleta("BIC-002", TipoBicicleta.MONTAÑA)));

        mockMvc.perform(get("/api/bicicletas/disponibles").param("tipo", "MONTAÑA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipo").value("MONTAÑA"));

        verify(bicicletaUseCase).obtenerDisponiblesPorTipo(TipoBicicleta.MONTAÑA);
    }

    @Test
    void should_ReturnBadRequest_WhenTipoIsInvalid() throws Exception {
        mockMvc.perform(get("/api/bicicletas/disponibles").param("tipo", "INVALIDO"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    private Bicicleta bicicleta(String codigo, TipoBicicleta tipo) {
        return new Bicicleta(codigo, tipo, EstadoBicicleta.DISPONIBLE);
    }
}