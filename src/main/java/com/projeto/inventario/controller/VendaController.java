package com.projeto.inventario.controller;

import com.projeto.inventario.config.OpenApiConfig;
import com.projeto.inventario.dto.RelatorioVendasResponse;
import com.projeto.inventario.dto.VendaRequest;
import com.projeto.inventario.model.Venda;
import com.projeto.inventario.service.VendaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/vendas")
@Tag(name = "Vendas", description = "Registro de vendas e relatorios.")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class VendaController {

    private final VendaService vendaService;

    public VendaController(VendaService vendaService) {
        this.vendaService = vendaService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar vendas", description = "Retorna as vendas registradas.")
    public ResponseEntity<List<Venda>> listarTodas() {
        return ResponseEntity.ok(vendaService.listarTodas());
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Registrar venda", description = "Registra uma venda e baixa a quantidade no estoque.")
    public ResponseEntity<Venda> registrarVenda(@RequestBody VendaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vendaService.registrarVenda(request));
    }

    @GetMapping("/relatorio")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Relatorio de vendas", description = "Retorna total de vendas, itens vendidos e faturamento.")
    public ResponseEntity<RelatorioVendasResponse> gerarRelatorio() {
        return ResponseEntity.ok(vendaService.gerarRelatorio());
    }
}
