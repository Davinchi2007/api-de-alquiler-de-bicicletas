package com.alquiler_de_bicicletas.aplicacion.servicio;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alquiler_de_bicicletas.aplicacion.excepcion.AlquilerNoEncontradoException;
import com.alquiler_de_bicicletas.aplicacion.excepcion.BicicletaNoEncontradaException;
import com.alquiler_de_bicicletas.aplicacion.puerto.entrada.PuertoEntradaFinalizarAlquiler;
import com.alquiler_de_bicicletas.aplicacion.puerto.entrada.PuertoEntradaIniciarAlquiler;
import com.alquiler_de_bicicletas.aplicacion.puerto.entrada.PuertoEntradaConsultarHistorialAlquileres;
import com.alquiler_de_bicicletas.aplicacion.puerto.salida.PuertoSalidaRepositorioAlquileres;
import com.alquiler_de_bicicletas.aplicacion.puerto.salida.PuertoSalidaRepositorioBicicletas;
import com.alquiler_de_bicicletas.dominio.modelo.Alquiler;
import com.alquiler_de_bicicletas.dominio.modelo.Bicicleta;

@Service
public class ServicioGestionAlquileres implements PuertoEntradaIniciarAlquiler,
    PuertoEntradaFinalizarAlquiler, PuertoEntradaConsultarHistorialAlquileres {

    private final PuertoSalidaRepositorioBicicletas repositorioBicicletas;
    private final PuertoSalidaRepositorioAlquileres repositorioAlquileres;

    public ServicioGestionAlquileres(
            PuertoSalidaRepositorioBicicletas repositorioBicicletas,
            PuertoSalidaRepositorioAlquileres repositorioAlquileres) {
        this.repositorioBicicletas = repositorioBicicletas;
        this.repositorioAlquileres = repositorioAlquileres;
    }

    @Override
    @Transactional
    public Alquiler iniciarAlquiler(
            String codigoBicicleta,
            String nombreCliente,
            LocalDateTime fechaHoraInicio,
            long duracionEstimadaHoras) {
        Bicicleta bicicleta = buscarBicicleta(codigoBicicleta);
        bicicleta.alquilar();

        Alquiler alquiler = new Alquiler(
                codigoBicicleta,
                nombreCliente,
                fechaHoraInicio,
                duracionEstimadaHoras,
                bicicleta.getTipo().obtenerTarifa());
        Alquiler alquilerGuardado = repositorioAlquileres.guardar(alquiler);
        repositorioBicicletas.guardar(bicicleta);

        return alquilerGuardado;
    }

    @Override
    @Transactional
    public Alquiler finalizarAlquiler(String codigoBicicleta, LocalDateTime fechaHoraDevolucion) {
        Alquiler alquiler = repositorioAlquileres.buscarActivoPorCodigoBicicleta(codigoBicicleta)
                .orElseThrow(() -> new AlquilerNoEncontradoException(codigoBicicleta));
        Bicicleta bicicleta = buscarBicicleta(codigoBicicleta);

        alquiler.finalizar(fechaHoraDevolucion);
        bicicleta.devolver();

        Alquiler alquilerGuardado = repositorioAlquileres.guardar(alquiler);
        repositorioBicicletas.guardar(bicicleta);

        return alquilerGuardado;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Alquiler> consultarHistorialAlquileres(String codigoBicicleta) {
        buscarBicicleta(codigoBicicleta);
        return repositorioAlquileres.buscarPorCodigoBicicleta(codigoBicicleta);
    }

    private Bicicleta buscarBicicleta(String codigoBicicleta) {
        return repositorioBicicletas.buscarPorCodigo(codigoBicicleta)
                .orElseThrow(() -> new BicicletaNoEncontradaException(codigoBicicleta));
    }

}