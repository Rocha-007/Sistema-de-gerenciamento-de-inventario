package com.projeto.inventario.client;

import com.projeto.inventario.model.Produto;
import com.projeto.inventario.repository.ProdutoRepository;
import com.projeto.inventario.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InventarioApiClientTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void limparBanco() {
        produtoRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    void deveExecutarCrudCompletoUsandoMesmoClienteDaInterface() {
        InventarioApiClient client = new InventarioApiClient("http://localhost:" + port);

        InventarioApiClient.AuthResult registro = client.registrar(
                "usuario.swing",
                "123456",
                "swing@email.com"
        );

        assertThat(registro.token()).isNotBlank();
        assertThat(client.isAutenticado()).isTrue();

        Produto criado = client.criarProduto(Produto.builder()
                .nome("Leitor de codigo")
                .descricao("USB")
                .preco(new BigDecimal("250.00"))
                .quantidadeEstoque(4)
                .build());

        Produto atualizado = client.atualizarProduto(
                criado.getId(),
                Produto.builder()
                        .nome("Leitor de codigo")
                        .descricao("USB e Bluetooth")
                        .preco(new BigDecimal("275.00"))
                        .quantidadeEstoque(6)
                        .build()
        );

        List<Produto> produtos = client.listarProdutos();

        assertThat(atualizado.getQuantidadeEstoque()).isEqualTo(6);
        assertThat(produtos).hasSize(1);
        assertThat(produtos.get(0).getDescricao()).isEqualTo("USB e Bluetooth");

        client.deletarProduto(criado.getId());

        assertThat(client.listarProdutos()).isEmpty();
    }
}
