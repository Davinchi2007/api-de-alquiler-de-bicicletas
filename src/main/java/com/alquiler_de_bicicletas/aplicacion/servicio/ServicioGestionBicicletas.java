package com.alquiler_de_bicicletas.aplicacion.servicio;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alquiler_de_bicicletas.aplicacion.excepcion.BicicletaNoEncontradaException;
import com.alquiler_de_bicicletas.aplicacion.excepcion.BicicletaYaExisteException;
import com.alquiler_de_bicicletas.aplicacion.puerto.entrada.PuertoEntradaGestionBicicletas;
import com.alquiler_de_bicicletas.aplicacion.puerto.salida.PuertoSalidaRepositorioBicicletas;
import com.alquiler_de_bicicletas.dominio.modelo.Bicicleta;
import com.alquiler_de_bicicletas.dominio.modelo.EstadoBicicleta;
import com.alquiler_de_bicicletas.dominio.modelo.TipoBicicleta;

@Service
public class ServicioGestionBicicletas implements PuertoEntradaGestionBicicletas {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServicioGestionBicicletas.class);

    private final PuertoSalidaRepositorioBicicletas repositorioBicicletas;

    public ServicioGestionBicicletas(PuertoSalidaRepositorioBicicletas repositorioBicicletas) {
        this.repositorioBicicletas = repositorioBicicletas;
    }

    @Override
    public Bicicleta registrar(String codigo, TipoBicicleta tipo, EstadoBicicleta estado) {
        if (repositorioBicicletas.existePorCodigo(codigo)) {
            LOGGER.warn("Registro rechazado: bicicleta duplicada codigoBicicleta={}", codigo);
            throw new BicicletaYaExisteException(codigo);
        }

        Bicicleta bicicleta = Bicicleta.crear(codigo, tipo, estado);
        Bicicleta bicicletaGuardada = repositorioBicicletas.guardar(bicicleta);
        LOGGER.info("Bicicleta registrada codigoBicicleta={} tipo={} estado={}", codigo, tipo, estado);
        return bicicletaGuardada;
    }

    @Override
    public Bicicleta buscarPorCodigo(String codigo) {
        return repositorioBicicletas.buscarPorCodigo(codigo)
                .orElseThrow(() -> new BicicletaNoEncontradaException(codigo));
    }

    @Override
    public List<Bicicleta> obtenerDisponibles() {
        return repositorioBicicletas.obtenerDisponibles();
    }

    @Override
    public List<Bicicleta> obtenerDisponiblesPorTipo(TipoBicicleta tipo) {
        return repositorioBicicletas.obtenerDisponiblesPorTipo(tipo);
    }
}