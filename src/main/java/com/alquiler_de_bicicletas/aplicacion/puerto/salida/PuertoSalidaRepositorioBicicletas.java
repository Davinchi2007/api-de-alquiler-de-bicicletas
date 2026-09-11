package com.alquiler_de_bicicletas.aplicacion.puerto.salida;

import java.util.List;
import java.util.Optional;

import com.alquiler_de_bicicletas.dominio.modelo.Bicicleta;
import com.alquiler_de_bicicletas.dominio.modelo.TipoBicicleta;

public interface PuertoSalidaRepositorioBicicletas {

    Bicicleta guardar(Bicicleta bicicleta);

    Optional<Bicicleta> buscarPorCodigo(String codigo);

    List<Bicicleta> obtenerDisponibles();

    List<Bicicleta> obtenerDisponiblesPorTipo(TipoBicicleta tipo);

    boolean existePorCodigo(String codigo);
}