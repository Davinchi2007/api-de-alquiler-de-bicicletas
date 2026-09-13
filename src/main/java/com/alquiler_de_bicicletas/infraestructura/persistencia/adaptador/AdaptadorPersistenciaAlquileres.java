package com.alquiler_de_bicicletas.infraestructura.persistencia.adaptador;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.alquiler_de_bicicletas.aplicacion.puerto.salida.PuertoSalidaRepositorioAlquileres;
import com.alquiler_de_bicicletas.dominio.modelo.Alquiler;
import com.alquiler_de_bicicletas.dominio.modelo.EstadoAlquiler;
import com.alquiler_de_bicicletas.dominio.modelo.TarifaBicicleta;
import com.alquiler_de_bicicletas.infraestructura.persistencia.entidad.EntidadJpaAlquiler;
import com.alquiler_de_bicicletas.infraestructura.persistencia.repositorio.RepositorioJpaAlquileres;

@Repository
public class AdaptadorPersistenciaAlquileres implements PuertoSalidaRepositorioAlquileres {

    private final RepositorioJpaAlquileres repositorioJpaAlquileres;

    public AdaptadorPersistenciaAlquileres(RepositorioJpaAlquileres repositorioJpaAlquileres) {
        this.repositorioJpaAlquileres = repositorioJpaAlquileres;
    }

    @Override
    public Alquiler guardar(Alquiler alquiler) {
        EntidadJpaAlquiler entidad = alquiler.getEstado() == EstadoAlquiler.FINALIZADO
            ? buscarEntidadActiva(alquiler)
            : convertirAEntidad(alquiler);

        return convertirADominio(repositorioJpaAlquileres.save(entidad));
    }

    private EntidadJpaAlquiler buscarEntidadActiva(Alquiler alquiler) {
        return repositorioJpaAlquileres.buscarActivoPorCodigoBicicleta(alquiler.getCodigoBicicleta())
                .map(entidadExistente -> actualizarEntidad(entidadExistente, alquiler))
                .orElseThrow(() -> new IllegalStateException(
                        "No existe un alquiler activo para la bicicleta " + alquiler.getCodigoBicicleta()));
    }

    @Override
    public Optional<Alquiler> buscarActivoPorCodigoBicicleta(String codigoBicicleta) {
        return repositorioJpaAlquileres.buscarActivoPorCodigoBicicleta(codigoBicicleta)
                .map(this::convertirADominio);
    }

    @Override
    public List<Alquiler> buscarPorCodigoBicicleta(String codigoBicicleta) {
        return repositorioJpaAlquileres.buscarPorCodigoBicicleta(codigoBicicleta)
                .stream()
                .map(this::convertirADominio)
                .toList();
    }

    private EntidadJpaAlquiler convertirAEntidad(Alquiler alquiler) {
        return new EntidadJpaAlquiler(
                alquiler.getCodigoBicicleta(),
                alquiler.getNombreCliente(),
                alquiler.getFechaHoraInicio(),
                Math.toIntExact(alquiler.getDuracionEstimadaHoras()),
                alquiler.getTarifaBicicleta().obtenerValorPorHora(),
                alquiler.getEstado(),
                alquiler.getFechaHoraDevolucion(),
                convertirHorasFacturables(alquiler),
                convertirImporte(alquiler, alquiler.getCostoBase()),
                convertirImporte(alquiler, alquiler.getMulta()),
                convertirImporte(alquiler, alquiler.getTotal()));
    }

    private EntidadJpaAlquiler actualizarEntidad(EntidadJpaAlquiler entidad, Alquiler alquiler) {
        entidad.actualizarDatos(
                alquiler.getNombreCliente(),
                Math.toIntExact(alquiler.getDuracionEstimadaHoras()),
                alquiler.getTarifaBicicleta().obtenerValorPorHora(),
                alquiler.getEstado(),
                alquiler.getFechaHoraDevolucion(),
                convertirHorasFacturables(alquiler),
                convertirImporte(alquiler, alquiler.getCostoBase()),
                convertirImporte(alquiler, alquiler.getMulta()),
                convertirImporte(alquiler, alquiler.getTotal()));
        return entidad;
    }

    private Alquiler convertirADominio(EntidadJpaAlquiler entidad) {
        Alquiler alquiler = new Alquiler(
                entidad.getCodigoBicicleta(),
                entidad.getNombreCliente(),
                entidad.getFechaHoraInicio(),
                entidad.getDuracionEstimadaHoras(),
                resolverTarifa(entidad.getTarifaPorHora()));

        if (entidad.getEstado() == EstadoAlquiler.FINALIZADO) {
            alquiler.finalizar(entidad.getFechaHoraDevolucion());
        }

        return alquiler;
    }

    private TarifaBicicleta resolverTarifa(Long tarifaPorHora) {
        for (TarifaBicicleta tarifa : TarifaBicicleta.values()) {
            if (tarifa.obtenerValorPorHora() == tarifaPorHora) {
                return tarifa;
            }
        }
        throw new IllegalArgumentException("No existe una tarifa de bicicleta para el valor " + tarifaPorHora);
    }

    private Integer convertirHorasFacturables(Alquiler alquiler) {
        return alquiler.getEstado() == EstadoAlquiler.ACTIVO
                ? null
                : Math.toIntExact(alquiler.getHorasFacturables());
    }

    private Long convertirImporte(Alquiler alquiler, long importe) {
        return alquiler.getEstado() == EstadoAlquiler.ACTIVO ? null : importe;
    }
}