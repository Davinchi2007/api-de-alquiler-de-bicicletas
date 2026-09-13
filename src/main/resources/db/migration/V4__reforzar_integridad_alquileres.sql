ALTER TABLE bicicletas
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

ALTER TABLE alquileres
    DROP CONSTRAINT IF EXISTS uk_alquileres_bicicleta_inicio;

ALTER TABLE alquileres
    ADD CONSTRAINT fk_alquileres_bicicleta
    FOREIGN KEY (codigo_bicicleta) REFERENCES bicicletas (codigo);

CREATE UNIQUE INDEX uk_alquileres_bicicleta_activo
    ON alquileres (codigo_bicicleta)
    WHERE estado = 'ACTIVO';