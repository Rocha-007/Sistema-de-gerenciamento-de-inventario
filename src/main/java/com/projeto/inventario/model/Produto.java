package com.projeto.inventario.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "produtos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Produto armazenado no inventario.")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador gerado pelo banco.", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Nome do produto.", example = "Teclado mecanico", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nome;

    @Schema(description = "Descricao opcional do produto.", example = "Teclado ABNT2 com switches azuis")
    private String descricao;

    @Column(nullable = false)
    @Schema(description = "Preco unitario.", example = "249.90", minimum = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal preco;

    @Column(name = "quantidade_estoque", nullable = false)
    @Schema(description = "Quantidade disponivel em estoque.", example = "15", minimum = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantidadeEstoque;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public Integer getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(Integer quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public static ProdutoBuilder builder() {
        return new ProdutoBuilder();
    }

    public static class ProdutoBuilder {
        private Long id;
        private String nome;
        private String descricao;
        private BigDecimal preco;
        private Integer quantidadeEstoque;

        public ProdutoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ProdutoBuilder nome(String nome) {
            this.nome = nome;
            return this;
        }

        public ProdutoBuilder descricao(String descricao) {
            this.descricao = descricao;
            return this;
        }

        public ProdutoBuilder preco(BigDecimal preco) {
            this.preco = preco;
            return this;
        }

        public ProdutoBuilder quantidadeEstoque(Integer quantidadeEstoque) {
            this.quantidadeEstoque = quantidadeEstoque;
            return this;
        }

        public Produto build() {
            Produto produto = new Produto();
            produto.setId(id);
            produto.setNome(nome);
            produto.setDescricao(descricao);
            produto.setPreco(preco);
            produto.setQuantidadeEstoque(quantidadeEstoque);
            return produto;
        }
    }
}
