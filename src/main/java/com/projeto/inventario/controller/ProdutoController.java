package com.projeto.inventario.controller;

import com.projeto.inventario.config.OpenApiConfig;
import com.projeto.inventario.exception.ApiErrorResponse;
import com.projeto.inventario.model.Produto;
import com.projeto.inventario.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
@Tag(name = "Produtos", description = "Operacoes de consulta e manutencao do inventario.")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar produtos", description = "Retorna todos os produtos cadastrados.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista retornada. Pode estar vazia.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Produto.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente, invalido ou expirado", content = @Content)
    })
    public ResponseEntity<List<Produto>> listarTodos() {
        List<Produto> produtos = produtoService.listarTodos();
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Buscar produto por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto encontrado"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente, invalido ou expirado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Produto nao encontrado", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Produto> buscarPorId(
            @Parameter(description = "ID do produto", example = "1", required = true)
            @PathVariable Long id
    ) {
        Produto produto = produtoService.buscarPorId(id);
        return ResponseEntity.ok(produto);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Cadastrar produto", description = "Adiciona um novo produto ao inventario.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Produto cadastrado"),
            @ApiResponse(responseCode = "400", description = "JSON ausente ou invalido", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente, invalido ou expirado", content = @Content)
    })
    public ResponseEntity<Produto> criar(@RequestBody Produto produto) {
        Produto produtoCriado = produtoService.salvar(produto);
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoCriado);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Atualizar produto", description = "Substitui os dados de um produto existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto atualizado"),
            @ApiResponse(responseCode = "400", description = "JSON ausente ou invalido", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente, invalido ou expirado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Produto nao encontrado", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Produto> atualizar(
            @Parameter(description = "ID do produto", example = "1", required = true)
            @PathVariable Long id,
            @RequestBody Produto produto
    ) {
        Produto produtoAtualizado = produtoService.atualizar(id, produto);
        return ResponseEntity.ok(produtoAtualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Excluir produto", description = "Remove definitivamente um produto do inventario.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Produto excluido", content = @Content),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente, invalido ou expirado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Produto nao encontrado", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do produto", example = "1", required = true)
            @PathVariable Long id
    ) {
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
