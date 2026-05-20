package com.projeto.inventario.service;

import com.projeto.inventario.model.Produto;
import com.projeto.inventario.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository repository;

    // Injeção via construtor
    public ProdutoService(ProdutoRepository repository) {
        this.repository = repository;
    }

    public List<Produto> listarTodos() {
        return repository.findAll();
    }

    public Produto buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + id));
    }

    @Transactional
    public Produto salvar(Produto p) {
        return repository.save(p);
    }

    @Transactional
    public Produto atualizar(Long id, Produto p) {
        Produto existente = buscarPorId(id);
        
        existente.setNome(p.getNome());
        existente.setDescricao(p.getDescricao());
        existente.setPreco(p.getPreco());
        existente.setQuantidadeEstoque(p.getQuantidadeEstoque());
        
        return repository.save(existente);
    }

    @Transactional
    public void deletar(Long id) {
        Produto existente = buscarPorId(id);
        repository.delete(existente);
    }
}
