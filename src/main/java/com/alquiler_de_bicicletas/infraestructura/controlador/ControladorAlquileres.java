package com.alquiler_de_bicicletas.infraestructura.controlador;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.alquiler_de_bicicletas.aplicacion.puerto.entrada.PuertoEntradaIniciarAlquiler;
import com.alquiler_de_bicicletas.aplicacion.puerto.entrada.PuertoEntradaFinalizarAlquiler;
import com.alquiler_de_bicicletas.aplicacion.puerto.entrada.PuertoEntradaConsultarHistorialAlquileres;
import com.alquiler_de_bicicletas.dominio.modelo.Alquiler;
import com.alquiler_de_bicicletas.infraestructura.controlador.dto.RespuestaAlquilerDTO;
import com.alquiler_de_bicicletas.infraestructura.controlador.dto.RespuestaHistorialAlquilerDTO;
import com.alquiler_de_bicicletas.infraestructura.controlador.dto.SolicitudCrearAlquilerDTO;
import com.alquiler_de_bicicletas.infraestructura.controlador.dto.SolicitudFinalizarAlquilerDTO;
import com.alquiler_de_bicicletas.infraestructura.controlador.mapeador.MapeadorAlquilerRespuesta;
import com.alquiler_de_bicicletas.infraestructura.controlador.mapeador.MapeadorHistorialAlquiler;

@RestController
@RequestMapping("/api/alquileres")
public class ControladorAlquileres {

    private final PuertoEntradaConsultarHistorialAlquileres puertoEntradaHistorial;
    private final PuertoEntradaIniciarAlquiler puertoEntradaIniciarAlquiler;
    private final PuertoEntradaFinalizarAlquiler puertoEntradaFinalizarAlquiler;
    private final MapeadorHistorialAlquiler mapeadorHistorial;
    private final MapeadorAlquilerRespuesta mapeadorAlquilerRespuesta;

    public ControladorAlquileres(
            PuertoEntradaConsultarHistorialAlquileres puertoEntradaHistorial,
            MapeadorHistorialAlquiler mapeadorHistorial,
            PuertoEntradaIniciarAlquiler puertoEntradaIniciarAlquiler,
            MapeadorAlquilerRespuesta mapeadorAlquilerRespuesta,
            PuertoEntradaFinalizarAlquiler puertoEntradaFinalizarAlquiler) {
        this.puertoEntradaHistorial = puertoEntradaHistorial;
        this.mapeadorHistorial = mapeadorHistorial;
        this.puertoEntradaIniciarAlquiler = puertoEntradaIniciarAlquiler;
        this.mapeadorAlquilerRespuesta = mapeadorAlquilerRespuesta;
        this.puertoEntradaFinalizarAlquiler = puertoEntradaFinalizarAlquiler;
    }

    @PostMapping
    public ResponseEntity<RespuestaAlquilerDTO> iniciarAlquiler(
            @Valid @RequestBody SolicitudCrearAlquilerDTO solicitud) {
        Alquiler alquiler = puertoEntradaIniciarAlquiler.iniciarAlquiler(
                solicitud.codigoBicicleta(),
                solicitud.nombreCliente(),
                solicitud.fechaHoraInicio(),
                solicitud.duracionEstimadaHoras());
        RespuestaAlquilerDTO respuesta = mapeadorAlquilerRespuesta.convertirARespuesta(alquiler);

        return ResponseEntity.created(URI.create("/api/alquileres/" + respuesta.codigoBicicleta()))
                .body(respuesta);
    }

        @PostMapping("/{codigoBicicleta}/finalizar")
        public ResponseEntity<RespuestaAlquilerDTO> finalizarAlquiler(
            @PathVariable String codigoBicicleta,
            @Valid @RequestBody SolicitudFinalizarAlquilerDTO solicitud) {
        Alquiler alquiler = puertoEntradaFinalizarAlquiler.finalizarAlquiler(
            codigoBicicleta, solicitud.fechaHoraDevolucion());
        return ResponseEntity.ok(mapeadorAlquilerRespuesta.convertirARespuesta(alquiler));
        }

    @GetMapping("/bicicletas/{codigoBicicleta}")
    public ResponseEntity<List<RespuestaHistorialAlquilerDTO>> consultarHistorial(
            @PathVariable String codigoBicicleta) {
        List<Alquiler> alquileres = puertoEntradaHistorial.consultarHistorialAlquileres(codigoBicicleta);
        return ResponseEntity.ok(alquileres.stream()
                .map(mapeadorHistorial::convertirARespuesta)
                .toList());
    }
}