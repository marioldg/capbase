package com.capbase.capbase.dto;

import java.math.BigDecimal;

public class PresupuestoCrearDTO {

    private BigDecimal limite;
    private int mes;
    private int anio;
    private Long categoriaId;

    public BigDecimal getLimite() { return limite; }
    public void setLimite(BigDecimal limite) { this.limite = limite; }

    public int getMes() { return mes; }
    public void setMes(int mes) { this.mes = mes; }

    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }

    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }
}