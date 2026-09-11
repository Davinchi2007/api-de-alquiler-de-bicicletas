package com.alquiler_de_bicicletas.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alquiler_de_bicicletas.domain.model.EstadoBicicleta;
import com.alquiler_de_bicicletas.domain.model.TipoBicicleta;
import com.alquiler_de_bicicletas.infrastructure.persistence.entity.BicicletaJpaEntity;

public interface BicicletaJpaRepository extends JpaRepository<BicicletaJpaEntity, Long> {

    Optional<BicicletaJpaEntity> findByCodigo(String codigo);

    List<BicicletaJpaEntity> findByEstado(EstadoBicicleta estado);

    List<BicicletaJpaEntity> findByEstadoAndTipo(EstadoBicicleta estado, TipoBicicleta tipo);

    boolean existsByCodigo(String codigo);
}