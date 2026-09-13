package com.alquiler_de_bicicletas.infraestructura.persistencia.repositorio;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.alquiler_de_bicicletas.infraestructura.persistencia.entidad.EntidadJpaAlquiler;

public interface RepositorioJpaAlquileres extends JpaRepository<EntidadJpaAlquiler, Long> {

    @Query("""
            select alquiler
            from EntidadJpaAlquiler alquiler
            where alquiler.codigoBicicleta = :codigoBicicleta
              and alquiler.estado = com.alquiler_de_bicicletas.dominio.modelo.EstadoAlquiler.ACTIVO
            """)
    Optional<EntidadJpaAlquiler> buscarActivoPorCodigoBicicleta(
            @Param("codigoBicicleta") String codigoBicicleta);

    @Query("""
            select alquiler
            from EntidadJpaAlquiler alquiler
            where alquiler.codigoBicicleta = :codigoBicicleta
            order by alquiler.fechaHoraInicio
            """)
    List<EntidadJpaAlquiler> buscarPorCodigoBicicleta(
            @Param("codigoBicicleta") String codigoBicicleta);

    @Query("""
            select alquiler
            from EntidadJpaAlquiler alquiler
            where alquiler.codigoBicicleta = :codigoBicicleta
              and alquiler.fechaHoraInicio = :fechaHoraInicio
            """)
    Optional<EntidadJpaAlquiler> buscarPorCodigoBicicletaYFechaHoraInicio(
            @Param("codigoBicicleta") String codigoBicicleta,
            @Param("fechaHoraInicio") LocalDateTime fechaHoraInicio);

}