package com.alquiler_de_bicicletas.infraestructura.persistencia.entidad;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.alquiler_de_bicicletas.dominio.modelo.EstadoAlquiler;

@Entity
@Table(name = "alquileres")
public class EntidadJpaAlquiler {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_bicicleta", nullable = false, length = 50)
    private String codigoBicicleta;

    @Column(name = "nombre_cliente", nullable = false, length = 150)
    private String nombreCliente;

    @Column(name = "fecha_hora_inicio", nullable = false)
    private LocalDateTime fechaHoraInicio;

    @Column(name = "duracion_estimada_horas", nullable = false)
    private Integer duracionEstimadaHoras;

    @Column(name = "tarifa_por_hora", nullable = false)
    private Long tarifaPorHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoAlquiler estado;

    @Column(name = "fecha_hora_devolucion")
    private LocalDateTime fechaHoraDevolucion;

    @Column(name = "horas_facturables")
    private Integer horasFacturables;

    @Column(name = "costo_base")
    private Long costoBase;

    @Column(name = "multa")
    private Long multa;

    @Column(name = "total")
    private Long total;

    protected EntidadJpaAlquiler() {
    }

    public EntidadJpaAlquiler(
            String codigoBicicleta,
            String nombreCliente,
            LocalDateTime fechaHoraInicio,
            Integer duracionEstimadaHoras,
            Long tarifaPorHora,
            EstadoAlquiler estado,
            LocalDateTime fechaHoraDevolucion,
            Integer horasFacturables,
            Long costoBase,
            Long multa,
            Long total) {
        this.codigoBicicleta = codigoBicicleta;
        this.nombreCliente = nombreCliente;
        this.fechaHoraInicio = fechaHoraInicio;
        this.duracionEstimadaHoras = duracionEstimadaHoras;
        this.tarifaPorHora = tarifaPorHora;
        this.estado = estado;
        this.fechaHoraDevolucion = fechaHoraDevolucion;
        this.horasFacturables = horasFacturables;
        this.costoBase = costoBase;
        this.multa = multa;
        this.total = total;
    }

    public void actualizarDatos(
            String nombreCliente,
            Integer duracionEstimadaHoras,
            Long tarifaPorHora,
            EstadoAlquiler estado,
            LocalDateTime fechaHoraDevolucion,
            Integer horasFacturables,
            Long costoBase,
            Long multa,
            Long total) {
        this.nombreCliente = nombreCliente;
        this.duracionEstimadaHoras = duracionEstimadaHoras;
        this.tarifaPorHora = tarifaPorHora;
        this.estado = estado;
        this.fechaHoraDevolucion = fechaHoraDevolucion;
        this.horasFacturables = horasFacturables;
        this.costoBase = costoBase;
        this.multa = multa;
        this.total = total;
    }

    public Long getId() {
        return id;
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

    public Integer getDuracionEstimadaHoras() {
        return duracionEstimadaHoras;
    }

    public Long getTarifaPorHora() {
        return tarifaPorHora;
    }

    public EstadoAlquiler getEstado() {
        return estado;
    }

    public LocalDateTime getFechaHoraDevolucion() {
        return fechaHoraDevolucion;
    }

    public Integer getHorasFacturables() {
        return horasFacturables;
    }

    public Long getCostoBase() {
        return costoBase;
    }

    public Long getMulta() {
        return multa;
    }

    public Long getTotal() {
        return total;
    }
}