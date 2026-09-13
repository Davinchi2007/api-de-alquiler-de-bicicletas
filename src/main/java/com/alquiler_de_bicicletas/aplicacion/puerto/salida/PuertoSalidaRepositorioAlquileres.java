package com.alquiler_de_bicicletas.aplicacion.puerto.salida;

import java.util.List;
import java.util.Optional;

import com.alquiler_de_bicicletas.dominio.modelo.Alquiler;

public interface PuertoSalidaRepositorioAlquileres {

    Alquiler guardar(Alquiler alquiler);

    Optional<Alquiler> buscarActivoPorCodigoBicicleta(String codigoBicicleta);

    List<Alquiler> buscarPorCodigoBicicleta(String codigoBicicleta);
}