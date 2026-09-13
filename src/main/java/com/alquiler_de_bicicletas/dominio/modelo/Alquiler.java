package com.alquiler_de_bicicletas.dominio.modelo;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import com.alquiler_de_bicicletas.dominio.excepcion.AlquilerYaFinalizadoException;
import com.alquiler_de_bicicletas.dominio.excepcion.FechaDevolucionInvalidaException;

public class Alquiler {

    private static final long PORCENTAJE_MULTA = 50;
    private static final long MINUTOS_POR_HORA = 60;
    private static final long SEGUNDOS_POR_HORA = MINUTOS_POR_HORA * 60;

    private static final String MSG_CODIGO_BICI_OBLIGATORIO = "El código de bicicleta es obligatorio";
    private static final String MSG_CLIENTE_OBLIGATORIO = "El nombre del cliente es obligatorio";
    private static final String MSG_FECHA_INICIO_OBLIGATORIA = "La fecha y hora de inicio son obligatorias";
    private static final String MSG_DURACION_INVALIDA = "La duración estimada debe ser mayor que cero";
    private static final String MSG_TARIFA_OBLIGATORIA = "La tarifa de bicicleta es obligatoria";
    private static final String MSG_ALQUILER_FINALIZADO = "El alquiler ya fue finalizado";
    private static final String MSG_FECHA_DEVOLUCION_INVALIDA = "La fecha de devolución debe ser posterior a la fecha de inicio";

    private final String codigoBicicleta;
    private final String nombreCliente;
    private final LocalDateTime fechaHoraInicio;
    private final long duracionEstimadaHoras;
    private final TarifaBicicleta tarifaBicicleta;
    private EstadoAlquiler estado;
    private LocalDateTime fechaHoraDevolucion;
    private long horasFacturables;
    private long costoBase;
    private long multa;
    private long total;

    public Alquiler(
            String codigoBicicleta,
            String nombreCliente,
            LocalDateTime fechaHoraInicio,
            long duracionEstimadaHoras,
            TarifaBicicleta tarifaBicicleta) {
        this.codigoBicicleta = validarTexto(codigoBicicleta, MSG_CODIGO_BICI_OBLIGATORIO);
        this.nombreCliente = validarTexto(nombreCliente, MSG_CLIENTE_OBLIGATORIO);
        this.fechaHoraInicio = Objects.requireNonNull(fechaHoraInicio, MSG_FECHA_INICIO_OBLIGATORIA);

        if (duracionEstimadaHoras <= 0) {
            throw new IllegalArgumentException(MSG_DURACION_INVALIDA);
        }

        this.duracionEstimadaHoras = duracionEstimadaHoras;
        this.tarifaBicicleta = Objects.requireNonNull(tarifaBicicleta, MSG_TARIFA_OBLIGATORIA);
        this.estado = EstadoAlquiler.ACTIVO;
    }

    public void finalizar(LocalDateTime fechaHoraDevolucion) {
        if (estado == EstadoAlquiler.FINALIZADO) {
            throw new AlquilerYaFinalizadoException(MSG_ALQUILER_FINALIZADO);
        }
        if (fechaHoraDevolucion == null || !fechaHoraDevolucion.isAfter(fechaHoraInicio)) {
            throw new FechaDevolucionInvalidaException(MSG_FECHA_DEVOLUCION_INVALIDA);
        }

        Duration duracionReal = Duration.between(fechaHoraInicio, fechaHoraDevolucion);
        this.horasFacturables = redondearHorasHaciaArriba(duracionReal);
        this.costoBase = horasFacturables * tarifaBicicleta.obtenerValorPorHora();
        this.multa = calcularMulta(duracionReal);
        this.total = costoBase + multa;
        this.fechaHoraDevolucion = fechaHoraDevolucion;
        this.estado = EstadoAlquiler.FINALIZADO;
    }

    private long calcularMulta(Duration duracionReal) {
        Duration retraso = duracionReal.minusHours(duracionEstimadaHoras);
        if (retraso.isZero() || retraso.isNegative()) {
            return 0;
        }

        long horasDeRetraso = redondearHorasHaciaArriba(retraso);
        long mitadTarifa = tarifaBicicleta.obtenerValorPorHora() * PORCENTAJE_MULTA / 100;
        return horasDeRetraso * mitadTarifa;
    }

    private static long redondearHorasHaciaArriba(Duration duracion) {
        long segundos = duracion.getSeconds();
        long horas = segundos / SEGUNDOS_POR_HORA;
        boolean tieneFraccion = segundos % SEGUNDOS_POR_HORA != 0 || duracion.getNano() > 0;
        return tieneFraccion ? horas + 1 : horas;
    }

    private static String validarTexto(String valor, String mensaje) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensaje);
        }
        return valor;
    }

    public String getCodigoBicicleta() {
        return codigoBicicleta;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public long getDuracionEstimadaHoras() {
        return duracionEstimadaHoras;
    }

    public TarifaBicicleta getTarifaBicicleta() {
        return tarifaBicicleta;
    }

    public EstadoAlquiler getEstado() {
        return estado;
    }

    public LocalDateTime getFechaHoraDevolucion() {
        return fechaHoraDevolucion;
    }

    public long getHorasFacturables() {
        return horasFacturables;
    }

    public long getCostoBase() {
        return costoBase;
    }

    public long getMulta() {
        return multa;
    }

    public long getTotal() {
        return total;
    }
}