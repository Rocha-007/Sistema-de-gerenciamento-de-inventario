package com.projeto.inventario.service;

import com.projeto.inventario.dto.RelatorioVendasResponse;
import com.projeto.inventario.dto.VendaRequest;
import com.projeto.inventario.model.Produto;
import com.projeto.inventario.model.Venda;
import com.projeto.inventario.repository.ProdutoRepository;
import com.projeto.inventario.repository.VendaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ProdutoService produtoService;
    private final ProdutoRepository produtoRepository;

    public VendaService(VendaRepository vendaRepository, ProdutoService produtoService, ProdutoRepository produtoRepository) {
        this.vendaRepository = vendaRepository;
        this.produtoService = produtoService;
        this.produtoRepository = produtoRepository;
    }

    public List<Venda> listarTodas() {
        return vendaRepository.findAll();
    }

    @Transactional
    public Venda registrarVenda(VendaRequest request) {
        validarRequest(request);

        Produto produto = produtoService.buscarPorId(request.getProdutoId());
        int quantidadeVendida = request.getQuantidade();

        if (produto.getQuantidadeEstoque() < quantidadeVendida) {
            throw new IllegalArgumentException("Estoque insuficiente para realizar a venda.");
        }

        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - quantidadeVendida);
        produtoRepository.save(produto);

        Venda venda = new Venda();
        venda.setProduto(produto);
        venda.setQuantidade(quantidadeVendida);
        venda.setValorUnitario(produto.getPreco());
        venda.setValorTotal(produto.getPreco().multiply(BigDecimal.valueOf(quantidadeVendida)));
        venda.setDataVenda(LocalDateTime.now());

        return vendaRepository.save(venda);
    }

    public RelatorioVendasResponse gerarRelatorio() {
        List<Venda> vendas = vendaRepository.findAll();
        int totalItens = vendas.stream()
                .mapToInt(Venda::getQuantidade)
                .sum();
        BigDecimal faturamento = vendas.stream()
                .map(Venda::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new RelatorioVendasResponse(vendas.size(), totalItens, faturamento);
    }

    private void validarRequest(VendaRequest request) {
        if (request == null || request.getProdutoId() == null) {
            throw new IllegalArgumentException("Informe o produto da venda.");
        }
        if (request.getQuantidade() == null || request.getQuantidade() <= 0) {
            throw new IllegalArgumentException("A quantidade vendida deve ser maior que zero.");
        }
    }
}
