package com.alquiler_de_bicicletas.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.alquiler_de_bicicletas.application.exception.BicicletaNoEncontradaException;
import com.alquiler_de_bicicletas.application.exception.BicicletaYaExisteException;
import com.alquiler_de_bicicletas.application.port.in.BicicletaUseCase;
import com.alquiler_de_bicicletas.application.port.out.BicicletaRepositoryPort;
import com.alquiler_de_bicicletas.domain.model.Bicicleta;
import com.alquiler_de_bicicletas.domain.model.EstadoBicicleta;
import com.alquiler_de_bicicletas.domain.model.TipoBicicleta;

@Service
public class BicicletaService implements BicicletaUseCase {

    private final BicicletaRepositoryPort bicicletaRepositoryPort;

    public BicicletaService(BicicletaRepositoryPort bicicletaRepositoryPort) {
        this.bicicletaRepositoryPort = bicicletaRepositoryPort;
    }

    @Override
    public Bicicleta registrar(String codigo, TipoBicicleta tipo) {
        if (bicicletaRepositoryPort.existePorCodigo(codigo)) {
            throw new BicicletaYaExisteException(codigo);
        }

        Bicicleta bicicleta = new Bicicleta(codigo, tipo, EstadoBicicleta.DISPONIBLE);
        return bicicletaRepositoryPort.guardar(bicicleta);
    }

    @Override
    public Bicicleta buscarPorCodigo(String codigo) {
        return bicicletaRepositoryPort.buscarPorCodigo(codigo)
                .orElseThrow(() -> new BicicletaNoEncontradaException(codigo));
    }

    @Override
    public List<Bicicleta> obtenerDisponibles() {
        return bicicletaRepositoryPort.obtenerDisponibles();
    }

    @Override
    public List<Bicicleta> obtenerDisponiblesPorTipo(TipoBicicleta tipo) {
        return bicicletaRepositoryPort.obtenerDisponiblesPorTipo(tipo);
    }
}