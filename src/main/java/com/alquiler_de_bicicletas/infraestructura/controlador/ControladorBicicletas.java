package com.alquiler_de_bicicletas.infraestructura.controlador;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alquiler_de_bicicletas.aplicacion.puerto.entrada.PuertoEntradaGestionBicicletas;
import com.alquiler_de_bicicletas.dominio.modelo.Bicicleta;
import com.alquiler_de_bicicletas.dominio.modelo.TipoBicicleta;
import com.alquiler_de_bicicletas.infraestructura.controlador.dto.RespuestaBicicletaDTO;
import com.alquiler_de_bicicletas.infraestructura.controlador.dto.SolicitudCrearBicicletaDTO;
import com.alquiler_de_bicicletas.infraestructura.controlador.mapeador.MapeadorBicicletaRespuesta;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bicicletas")
public class ControladorBicicletas {

    private final PuertoEntradaGestionBicicletas puertoEntradaBicicletas;
    private final MapeadorBicicletaRespuesta mapeadorBicicletaRespuesta;

    public ControladorBicicletas(
            PuertoEntradaGestionBicicletas puertoEntradaBicicletas,
            MapeadorBicicletaRespuesta mapeadorBicicletaRespuesta) {
        this.puertoEntradaBicicletas = puertoEntradaBicicletas;
        this.mapeadorBicicletaRespuesta = mapeadorBicicletaRespuesta;
    }

    @PostMapping
    public ResponseEntity<RespuestaBicicletaDTO> registrar(
            @Valid @RequestBody SolicitudCrearBicicletaDTO solicitud) {
        Bicicleta bicicleta = puertoEntradaBicicletas.registrar(
            solicitud.codigo(), solicitud.tipo(), solicitud.estado());
        RespuestaBicicletaDTO respuesta = mapeadorBicicletaRespuesta.convertirARespuesta(bicicleta);

        return ResponseEntity.created(URI.create("/api/bicicletas/" + respuesta.codigo())).body(respuesta);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<RespuestaBicicletaDTO> buscarPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(mapeadorBicicletaRespuesta.convertirARespuesta(
                puertoEntradaBicicletas.buscarPorCodigo(codigo)));
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<RespuestaBicicletaDTO>> obtenerDisponibles(
            @RequestParam(required = false) TipoBicicleta tipo) {
        List<Bicicleta> bicicletas = obtenerBicicletasDisponibles(tipo);

        return ResponseEntity.ok(bicicletas.stream()
                .map(mapeadorBicicletaRespuesta::convertirARespuesta)
                .toList());
    }

    private List<Bicicleta> obtenerBicicletasDisponibles(TipoBicicleta tipo) {
        return tipo == null
                ? puertoEntradaBicicletas.obtenerDisponibles()
                : puertoEntradaBicicletas.obtenerDisponiblesPorTipo(tipo);
    }
}