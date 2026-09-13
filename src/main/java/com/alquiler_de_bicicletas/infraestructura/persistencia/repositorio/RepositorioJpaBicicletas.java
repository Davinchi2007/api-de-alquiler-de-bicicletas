package com.alquiler_de_bicicletas.infraestructura.persistencia.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.alquiler_de_bicicletas.dominio.modelo.EstadoBicicleta;
import com.alquiler_de_bicicletas.dominio.modelo.TipoBicicleta;
import com.alquiler_de_bicicletas.infraestructura.persistencia.entidad.EntidadJpaBicicleta;

public interface RepositorioJpaBicicletas extends JpaRepository<EntidadJpaBicicleta, Long> {

    @Query("select bicicleta from EntidadJpaBicicleta bicicleta where bicicleta.codigo = :codigo")
    Optional<EntidadJpaBicicleta> buscarPorCodigo(@Param("codigo") String codigo);

    @Query("select bicicleta from EntidadJpaBicicleta bicicleta where bicicleta.estado = :estado")
    List<EntidadJpaBicicleta> buscarPorEstado(@Param("estado") EstadoBicicleta estado);

    @Query("select bicicleta from EntidadJpaBicicleta bicicleta where bicicleta.estado = :estado and bicicleta.tipo = :tipo")
    List<EntidadJpaBicicleta> buscarPorEstadoYTipo(
            @Param("estado") EstadoBicicleta estado,
            @Param("tipo") TipoBicicleta tipo);

    @Query("select count(bicicleta) > 0 from EntidadJpaBicicleta bicicleta where bicicleta.codigo = :codigo")
    boolean existePorCodigo(@Param("codigo") String codigo);
}