package com.projeto.inventario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados para registrar uma venda.")
public class VendaRequest {
    @Schema(description = "ID do produto vendido.", example = "1")
    private Long produtoId;

    @Schema(description = "Quantidade vendida.", example = "2")
    private Integer quantidade;
}
