package com.alquiler_de_bicicletas.application.port.in;

import java.util.List;

import com.alquiler_de_bicicletas.domain.model.Bicicleta;
import com.alquiler_de_bicicletas.domain.model.TipoBicicleta;

public interface BicicletaUseCase {

    Bicicleta registrar(String codigo, TipoBicicleta tipo);

    Bicicleta buscarPorCodigo(String codigo);

    List<Bicicleta> obtenerDisponibles();

    List<Bicicleta> obtenerDisponiblesPorTipo(TipoBicicleta tipo);
}