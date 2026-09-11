package com.alquiler_de_bicicletas.infraestructura.persistencia.adaptador;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.alquiler_de_bicicletas.aplicacion.puerto.salida.PuertoSalidaRepositorioBicicletas;
import com.alquiler_de_bicicletas.dominio.modelo.Bicicleta;
import com.alquiler_de_bicicletas.dominio.modelo.EstadoBicicleta;
import com.alquiler_de_bicicletas.dominio.modelo.TipoBicicleta;
import com.alquiler_de_bicicletas.infraestructura.persistencia.entidad.EntidadJpaBicicleta;
import com.alquiler_de_bicicletas.infraestructura.persistencia.repositorio.RepositorioJpaBicicletas;

@Repository
public class AdaptadorPersistenciaBicicletas implements PuertoSalidaRepositorioBicicletas {

    private final RepositorioJpaBicicletas repositorioJpaBicicletas;

    public AdaptadorPersistenciaBicicletas(RepositorioJpaBicicletas repositorioJpaBicicletas) {
        this.repositorioJpaBicicletas = repositorioJpaBicicletas;
    }

    @Override
    public Bicicleta guardar(Bicicleta bicicleta) {
        EntidadJpaBicicleta entidad = convertirAEntidad(bicicleta);
        return convertirADominio(repositorioJpaBicicletas.save(entidad));
    }

    @Override
    public Optional<Bicicleta> buscarPorCodigo(String codigo) {
        return repositorioJpaBicicletas.buscarPorCodigo(codigo).map(this::convertirADominio);
    }

    @Override
    public List<Bicicleta> obtenerDisponibles() {
        return repositorioJpaBicicletas.buscarPorEstado(EstadoBicicleta.DISPONIBLE)
                .stream()
            .map(this::convertirADominio)
                .toList();
    }

    @Override
    public List<Bicicleta> obtenerDisponiblesPorTipo(TipoBicicleta tipo) {
        return repositorioJpaBicicletas.buscarPorEstadoYTipo(EstadoBicicleta.DISPONIBLE, tipo)
                .stream()
            .map(this::convertirADominio)
                .toList();
    }

    @Override
    public boolean existePorCodigo(String codigo) {
        return repositorioJpaBicicletas.existePorCodigo(codigo);
    }

    private EntidadJpaBicicleta convertirAEntidad(Bicicleta bicicleta) {
        return new EntidadJpaBicicleta(bicicleta.getCodigo(), bicicleta.getTipo(), bicicleta.getEstado());
    }

    private Bicicleta convertirADominio(EntidadJpaBicicleta entidad) {
        return new Bicicleta(entidad.getCodigo(), entidad.getTipo(), entidad.getEstado());
    }
}