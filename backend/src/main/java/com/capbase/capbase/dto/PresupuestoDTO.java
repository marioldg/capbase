package com.capbase.capbase.dto;

import java.math.BigDecimal;

public class PresupuestoDTO {

    private Long id;
    private BigDecimal limite;
    private int mes;
    private int anio;
    private String categoriaNombre;

    public PresupuestoDTO(Long id, BigDecimal limite, int mes, int anio, String categoriaNombre) {
        this.id = id;
        this.limite = limite;
        this.mes = mes;
        this.anio = anio;
        this.categoriaNombre = categoriaNombre;
    }

    public Long getId() { return id; }
    public BigDecimal getLimite() { return limite; }
    public int getMes() { return mes; }
    public int getAnio() { return anio; }
    public String getCategoriaNombre() { return categoriaNombre; }
}