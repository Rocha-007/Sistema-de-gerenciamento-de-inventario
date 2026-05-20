package com.projeto.inventario.service;

import com.projeto.inventario.exception.ResourceNotFoundException;
import com.projeto.inventario.model.Produto;
import com.projeto.inventario.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return repository.save(produto);
    }

    @Transactional
    public Produto atualizar(Long id, Produto produto) {
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
}
