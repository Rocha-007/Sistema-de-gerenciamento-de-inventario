package com.projeto.inventario.service;

import com.projeto.inventario.exception.ResourceNotFoundException;
import com.projeto.inventario.model.Produto;
import com.projeto.inventario.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository repository;

    public ProdutoService(ProdutoRepository repository) {
        this.repository = repository;
    }

    public List<Produto> listarTodos() {
        return repository.findAll();
    }

    public Produto buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto nao encontrado com ID: " + id));
    }

    @Transactional
    public Produto salvar(Produto produto) {
        validarProduto(produto);
        return repository.save(produto);
    }

    @Transactional
    public Produto atualizar(Long id, Produto produto) {
        validarProduto(produto);
        Produto existente = buscarPorId(id);

        existente.setNome(produto.getNome());
        existente.setDescricao(produto.getDescricao());
        existente.setPreco(produto.getPreco());
        existente.setQuantidadeEstoque(produto.getQuantidadeEstoque());

        return repository.save(existente);
    }

    @Transactional
    public void deletar(Long id) {
        Produto existente = buscarPorId(id);
        repository.delete(existente);
    }

    private void validarProduto(Produto produto) {
        if (produto == null) {
            throw new IllegalArgumentException("Dados do produto sao obrigatorios.");
        }

        if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do produto e obrigatorio.");
        }

        if (produto.getPreco() == null || produto.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O preco do produto deve ser maior que zero.");
        }

        if (produto.getQuantidadeEstoque() == null || produto.getQuantidadeEstoque() < 0) {
            throw new IllegalArgumentException("A quantidade em estoque nao pode ser negativa.");
        }
    }
}
