package com.alquiler_de_bicicletas.aplicacion.puerto.entrada;

import java.util.List;

import com.alquiler_de_bicicletas.dominio.modelo.Alquiler;

public interface PuertoEntradaConsultarHistorialAlquileres {

    List<Alquiler> consultarHistorialAlquileres(String codigoBicicleta);
}