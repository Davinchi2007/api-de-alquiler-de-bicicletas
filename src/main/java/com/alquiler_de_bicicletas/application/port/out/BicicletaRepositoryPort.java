package com.alquiler_de_bicicletas.application.port.out;

import java.util.List;
import java.util.Optional;

import com.alquiler_de_bicicletas.domain.model.Bicicleta;
import com.alquiler_de_bicicletas.domain.model.TipoBicicleta;

public interface BicicletaRepositoryPort {

    Bicicleta guardar(Bicicleta bicicleta);

    Optional<Bicicleta> buscarPorCodigo(String codigo);

    List<Bicicleta> obtenerDisponibles();

    List<Bicicleta> obtenerDisponiblesPorTipo(TipoBicicleta tipo);

    boolean existePorCodigo(String codigo);
}