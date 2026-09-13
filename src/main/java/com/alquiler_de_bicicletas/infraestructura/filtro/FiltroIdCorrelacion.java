package com.alquiler_de_bicicletas.infraestructura.filtro;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FiltroIdCorrelacion extends OncePerRequestFilter {

    public static final String NOMBRE_HEADER = "X-Correlation-Id";
    public static final String CLAVE_MDC = "correlationId";
    private static final int LONGITUD_MAXIMA_ID = 100;

    @Override
    protected void doFilterInternal(
            HttpServletRequest solicitud,
            HttpServletResponse respuesta,
            FilterChain cadena) throws ServletException, IOException {
        String idCorrelacion = obtenerIdCorrelacion(solicitud.getHeader(NOMBRE_HEADER));
        MDC.put(CLAVE_MDC, idCorrelacion);
        respuesta.setHeader(NOMBRE_HEADER, idCorrelacion);

        try {
            cadena.doFilter(solicitud, respuesta);
        } finally {
            MDC.remove(CLAVE_MDC);
        }
    }

    private String obtenerIdCorrelacion(String idRecibido) {
        if (idRecibido == null || idRecibido.isBlank() || idRecibido.length() > LONGITUD_MAXIMA_ID) {
            return UUID.randomUUID().toString();
        }
        return idRecibido;
    }
}