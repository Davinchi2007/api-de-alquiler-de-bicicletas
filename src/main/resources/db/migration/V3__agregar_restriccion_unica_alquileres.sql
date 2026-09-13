ALTER TABLE alquileres
    ADD CONSTRAINT uk_alquileres_bicicleta_inicio
    UNIQUE (codigo_bicicleta, fecha_hora_inicio);