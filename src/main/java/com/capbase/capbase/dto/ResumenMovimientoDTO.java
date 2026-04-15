package com.capbase.capbase.dto;

import java.math.BigDecimal;

public class ResumenMovimientoDTO {

    private BigDecimal totalIngresos;
    private BigDecimal totalGastos;
    private BigDecimal balance;

    public ResumenMovimientoDTO() {
    }

    public ResumenMovimientoDTO(BigDecimal totalIngresos, BigDecimal totalGastos, BigDecimal balance) {
        this.totalIngresos = totalIngresos;
        this.totalGastos = totalGastos;
        this.balance = balance;
    }

    public BigDecimal getTotalIngresos() {
        return totalIngresos;
    }

    public void setTotalIngresos(BigDecimal totalIngresos) {
        this.totalIngresos = totalIngresos;
    }

    public BigDecimal getTotalGastos() {
        return totalGastos;
    }

    public void setTotalGastos(BigDecimal totalGastos) {
        this.totalGastos = totalGastos;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}