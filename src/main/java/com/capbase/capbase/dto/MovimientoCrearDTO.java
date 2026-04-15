package com.capbase.capbase.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MovimientoCrearDTO {

    private String concepto;
    private String descripcion;
    private BigDecimal cantidad;
    private LocalDate fecha;
    private String tipo;
    private Long categoriaId;
    private Long usuarioId;

    // Constructor vacío
    public MovimientoCrearDTO() {
    }

    // Constructor completo
    public MovimientoCrearDTO(String concepto, String descripcion, BigDecimal cantidad,
                              LocalDate fecha, String tipo, Long categoriaId, Long usuarioId) {
        this.concepto = concepto;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.tipo = tipo;
        this.categoriaId = categoriaId;
        this.usuarioId = usuarioId;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
}