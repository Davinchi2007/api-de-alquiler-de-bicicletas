package com.alquiler_de_bicicletas.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.alquiler_de_bicicletas.application.port.out.BicicletaRepositoryPort;
import com.alquiler_de_bicicletas.domain.model.Bicicleta;
import com.alquiler_de_bicicletas.domain.model.EstadoBicicleta;
import com.alquiler_de_bicicletas.domain.model.TipoBicicleta;
import com.alquiler_de_bicicletas.infrastructure.persistence.entity.BicicletaJpaEntity;
import com.alquiler_de_bicicletas.infrastructure.persistence.repository.BicicletaJpaRepository;

@Repository
public class BicicletaPersistenceAdapter implements BicicletaRepositoryPort {

    private final BicicletaJpaRepository bicicletaJpaRepository;

    public BicicletaPersistenceAdapter(BicicletaJpaRepository bicicletaJpaRepository) {
        this.bicicletaJpaRepository = bicicletaJpaRepository;
    }

    @Override
    public Bicicleta guardar(Bicicleta bicicleta) {
        BicicletaJpaEntity entity = toEntity(bicicleta);
        return toDomain(bicicletaJpaRepository.save(entity));
    }

    @Override
    public Optional<Bicicleta> buscarPorCodigo(String codigo) {
        return bicicletaJpaRepository.findByCodigo(codigo).map(this::toDomain);
    }

    @Override
    public List<Bicicleta> obtenerDisponibles() {
        return bicicletaJpaRepository.findByEstado(EstadoBicicleta.DISPONIBLE)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Bicicleta> obtenerDisponiblesPorTipo(TipoBicicleta tipo) {
        return bicicletaJpaRepository.findByEstadoAndTipo(EstadoBicicleta.DISPONIBLE, tipo)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existePorCodigo(String codigo) {
        return bicicletaJpaRepository.existsByCodigo(codigo);
    }

    private BicicletaJpaEntity toEntity(Bicicleta bicicleta) {
        return new BicicletaJpaEntity(bicicleta.getCodigo(), bicicleta.getTipo(), bicicleta.getEstado());
    }

    private Bicicleta toDomain(BicicletaJpaEntity entity) {
        return new Bicicleta(entity.getCodigo(), entity.getTipo(), entity.getEstado());
    }
}