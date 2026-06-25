package com.projeto.inventario.dto;

import java.math.BigDecimal;

public class RelatorioVendasResponse {

    private long totalVendas;
    private int totalItensVendidos;
    private BigDecimal faturamentoTotal;

    public RelatorioVendasResponse() {
    }

    public RelatorioVendasResponse(long totalVendas, int totalItensVendidos, BigDecimal faturamentoTotal) {
        this.totalVendas = totalVendas;
        this.totalItensVendidos = totalItensVendidos;
        this.faturamentoTotal = faturamentoTotal;
    }

    public long getTotalVendas() {
        return totalVendas;
    }

    public void setTotalVendas(long totalVendas) {
        this.totalVendas = totalVendas;
    }

    public int getTotalItensVendidos() {
        return totalItensVendidos;
    }

    public void setTotalItensVendidos(int totalItensVendidos) {
        this.totalItensVendidos = totalItensVendidos;
    }

    public BigDecimal getFaturamentoTotal() {
        return faturamentoTotal;
    }

    public void setFaturamentoTotal(BigDecimal faturamentoTotal) {
        this.faturamentoTotal = faturamentoTotal;
    }
}
