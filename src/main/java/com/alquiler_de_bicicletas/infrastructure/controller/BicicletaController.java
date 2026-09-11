package com.alquiler_de_bicicletas.infrastructure.controller;

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

import com.alquiler_de_bicicletas.application.port.in.BicicletaUseCase;
import com.alquiler_de_bicicletas.domain.model.Bicicleta;
import com.alquiler_de_bicicletas.domain.model.TipoBicicleta;
import com.alquiler_de_bicicletas.infrastructure.controller.dto.BicicletaResponse;
import com.alquiler_de_bicicletas.infrastructure.controller.dto.CrearBicicletaRequest;
import com.alquiler_de_bicicletas.infrastructure.controller.mapper.BicicletaDtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bicicletas")
public class BicicletaController {

    private final BicicletaUseCase bicicletaUseCase;
    private final BicicletaDtoMapper bicicletaDtoMapper;

    public BicicletaController(BicicletaUseCase bicicletaUseCase, BicicletaDtoMapper bicicletaDtoMapper) {
        this.bicicletaUseCase = bicicletaUseCase;
        this.bicicletaDtoMapper = bicicletaDtoMapper;
    }

    @PostMapping
    public ResponseEntity<BicicletaResponse> registrar(@Valid @RequestBody CrearBicicletaRequest request) {
        Bicicleta bicicleta = bicicletaUseCase.registrar(
            request.codigo(), request.tipo());
        BicicletaResponse response = bicicletaDtoMapper.toResponse(bicicleta);

        return ResponseEntity.created(URI.create("/api/bicicletas/" + response.codigo())).body(response);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<BicicletaResponse> buscarPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(bicicletaDtoMapper.toResponse(bicicletaUseCase.buscarPorCodigo(codigo)));
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<BicicletaResponse>> obtenerDisponibles(
            @RequestParam(required = false) TipoBicicleta tipo) {
        List<Bicicleta> bicicletas = tipo == null
                ? bicicletaUseCase.obtenerDisponibles()
                : bicicletaUseCase.obtenerDisponiblesPorTipo(tipo);

        return ResponseEntity.ok(bicicletas.stream().map(bicicletaDtoMapper::toResponse).toList());
    }
}