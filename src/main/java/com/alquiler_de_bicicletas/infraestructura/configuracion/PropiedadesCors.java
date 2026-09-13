package com.alquiler_de_bicicletas.infraestructura.configuracion;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.seguridad.cors")
public record PropiedadesCors(List<String> origenesPermitidos) {
}