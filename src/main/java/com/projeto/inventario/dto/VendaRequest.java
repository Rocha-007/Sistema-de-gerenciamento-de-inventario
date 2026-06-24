package com.projeto.inventario.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para registrar uma venda.")
public class VendaRequest {

    @Schema(description = "ID do produto vendido.", example = "1")
    private Long produtoId;

    @Schema(description = "Quantidade vendida.", example = "2")
    private Integer quantidade;

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}
