package com.capbase.capbase.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MovimientoDTO {

    private Long id;
    private String concepto;
    private String descripcion;
    private BigDecimal cantidad;
    private LocalDate fecha;
    private String tipo;
    private String categoriaNombre;
    private String usuarioNombre;

    // Constructor vacío
    public MovimientoDTO() {
    }

    // Constructor completo
    public MovimientoDTO(Long id, String concepto, String descripcion, BigDecimal cantidad,
                         LocalDate fecha, String tipo, String categoriaNombre, String usuarioNombre) {
        this.id = id;
        this.concepto = concepto;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.tipo = tipo;
        this.categoriaNombre = categoriaNombre;
        this.usuarioNombre = usuarioNombre;
    }

    public Long getId() {
        return id;
    }

    public String getConcepto() {
        return concepto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getTipo() {
        return tipo;
    }

    public String getCategoriaNombre() {
        return categoriaNombre;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setCategoriaNombre(String categoriaNombre) {
        this.categoriaNombre = categoriaNombre;
    }

    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }
}