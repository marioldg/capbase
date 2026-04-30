package com.capbase.capbase.dto;

import java.math.BigDecimal;

public class PresupuestoEstadoDTO {

    private String categoria;
    private BigDecimal limite;
    private BigDecimal gastado;
    private double porcentaje;

    public PresupuestoEstadoDTO(String categoria, BigDecimal limite, BigDecimal gastado, double porcentaje) {
        this.categoria = categoria;
        this.limite = limite;
        this.gastado = gastado;
        this.porcentaje = porcentaje;
    }

    public String getCategoria() { return categoria; }
    public BigDecimal getLimite() { return limite; }
    public BigDecimal getGastado() { return gastado; }
    public double getPorcentaje() { return porcentaje; }
}