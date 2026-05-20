package com.projeto.inventario.controller;

import com.projeto.inventario.model.Produto;
import com.projeto.inventario.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador de Produtos
 * Todos os endpoints estão protegidos por autenticação JWT
 */
@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    /**
     * Lista todos os produtos
     * Requer autenticação
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Produto>> listarTodos() {
        List<Produto> produtos = produtoService.listarTodos();
        return ResponseEntity.ok(produtos);
    }

    /**
     * Busca um produto por ID
     * Requer autenticação
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            Produto produto = produtoService.buscarPorId(id);
            return ResponseEntity.ok(produto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Cria um novo produto
     * Requer autenticação
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> criar(@RequestBody Produto produto) {
        try {
            Produto produtoCriado = produtoService.salvar(produto);
            return ResponseEntity.status(HttpStatus.CREATED).body(produtoCriado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Erro ao criar produto: " + e.getMessage()));
        }
    }

    /**
     * Atualiza um produto existente
     * Requer autenticação
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Produto produto) {
        try {
            Produto produtoAtualizado = produtoService.atualizar(id, produto);
            return ResponseEntity.ok(produtoAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Erro ao atualizar produto: " + e.getMessage()));
        }
    }

    /**
     * Deleta um produto
     * Requer autenticação
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            produtoService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro ao deletar produto: " + e.getMessage()));
        }
    }

    /**
     * Classe interna para respostas de erro padronizadas
     */
    public static class ErrorResponse {
        public String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
