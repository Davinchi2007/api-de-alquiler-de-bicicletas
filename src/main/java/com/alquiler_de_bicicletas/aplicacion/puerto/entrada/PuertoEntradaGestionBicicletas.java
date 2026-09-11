package com.alquiler_de_bicicletas.aplicacion.puerto.entrada;

import java.util.List;

import com.alquiler_de_bicicletas.dominio.modelo.Bicicleta;
import com.alquiler_de_bicicletas.dominio.modelo.TipoBicicleta;

public interface PuertoEntradaGestionBicicletas {

    Bicicleta registrar(String codigo, TipoBicicleta tipo);

    Bicicleta buscarPorCodigo(String codigo);

    List<Bicicleta> obtenerDisponibles();

    List<Bicicleta> obtenerDisponiblesPorTipo(TipoBicicleta tipo);
}