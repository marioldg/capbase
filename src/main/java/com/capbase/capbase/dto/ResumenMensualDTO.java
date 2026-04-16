package com.capbase.capbase.dto;

import java.math.BigDecimal;

public class ResumenMensualDTO {

    private int mes;
    private BigDecimal ingresos;
    private BigDecimal gastos;
    private BigDecimal balance;

    public ResumenMensualDTO(int mes, BigDecimal ingresos, BigDecimal gastos, BigDecimal balance) {
        this.mes = mes;
        this.ingresos = ingresos;
        this.gastos = gastos;
        this.balance = balance;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public BigDecimal getIngresos() {
        return ingresos;
    }

    public void setIngresos(BigDecimal ingresos) {
        this.ingresos = ingresos;
    }

    public BigDecimal getGastos() {
        return gastos;
    }

    public void setGastos(BigDecimal gastos) {
        this.gastos = gastos;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}