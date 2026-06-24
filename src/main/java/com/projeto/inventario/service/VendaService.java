package com.projeto.inventario.service;

import com.projeto.inventario.dto.VendaRequest;
import com.projeto.inventario.dto.VendaResponse;
import com.projeto.inventario.exception.ResourceNotFoundException;
import com.projeto.inventario.model.Produto;
import com.projeto.inventario.model.Venda;
import com.projeto.inventario.repository.ProdutoRepository;
import com.projeto.inventario.repository.VendaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;

    public VendaService(VendaRepository vendaRepository, ProdutoRepository produtoRepository) {
        this.vendaRepository = vendaRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional(readOnly = true)
    public List<VendaResponse> listarTodas() {
        return vendaRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public VendaResponse buscarPorId(Long id) {
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venda nao encontrada com ID: " + id));

        return toResponse(venda);
    }

    @Transactional
    public VendaResponse registrarVenda(VendaRequest request) {
        validarRequest(request);

        Produto produto = produtoRepository.findById(request.getProdutoId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto nao encontrado com ID: " + request.getProdutoId()));

        Integer estoqueAtual = produto.getQuantidadeEstoque();
        if (estoqueAtual == null || estoqueAtual < request.getQuantidade()) {
            throw new IllegalStateException("Estoque insuficiente para o produto: " + produto.getNome());
        }

        BigDecimal precoUnitario = produto.getPreco();
        BigDecimal valorTotal = precoUnitario.multiply(BigDecimal.valueOf(request.getQuantidade()));

        produto.setQuantidadeEstoque(estoqueAtual - request.getQuantidade());
        produtoRepository.save(produto);

        Venda venda = Venda.builder()
                .produto(produto)
                .quantidade(request.getQuantidade())
                .precoUnitario(precoUnitario)
                .valorTotal(valorTotal)
                .build();

        Venda vendaSalva = vendaRepository.save(venda);
        return toResponse(vendaSalva);
    }

    private void validarRequest(VendaRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Dados da venda sao obrigatorios.");
        }

        if (request.getProdutoId() == null) {
            throw new IllegalArgumentException("O produtoId e obrigatorio.");
        }

        if (request.getQuantidade() == null || request.getQuantidade() <= 0) {
            throw new IllegalArgumentException("A quantidade da venda deve ser maior que zero.");
        }
    }

    private VendaResponse toResponse(Venda venda) {
        Produto produto = venda.getProduto();

        return VendaResponse.builder()
                .id(venda.getId())
                .produtoId(produto.getId())
                .nomeProduto(produto.getNome())
                .quantidade(venda.getQuantidade())
                .precoUnitario(venda.getPrecoUnitario())
                .valorTotal(venda.getValorTotal())
                .dataVenda(venda.getDataVenda())
                .build();
    }
}
