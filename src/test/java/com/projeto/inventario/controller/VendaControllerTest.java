package com.projeto.inventario.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.inventario.dto.VendaRequest;
import com.projeto.inventario.model.Produto;
import com.projeto.inventario.model.Usuario;
import com.projeto.inventario.repository.ProdutoRepository;
import com.projeto.inventario.repository.UsuarioRepository;
import com.projeto.inventario.repository.VendaRepository;
import com.projeto.inventario.util.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class VendaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private String token;

    @BeforeEach
    void setUp() {
        vendaRepository.deleteAll();
        produtoRepository.deleteAll();
        usuarioRepository.deleteAll();

        Usuario usuario = Usuario.builder()
                .username("eduardo")
                .password(passwordEncoder.encode("123456"))
                .email("eduardo@email.com")
                .ativo(true)
                .build();

        usuarioRepository.save(usuario);
        token = jwtTokenUtil.generateToken(usuario);
    }

    @Test
    void deveRegistrarVendaEBaixarEstoque() throws Exception {
        Produto produto = salvarProduto("Teclado", "Teclado USB", "100.00", 10);

        VendaRequest request = new VendaRequest();
        request.setProdutoId(produto.getId());
        request.setQuantidade(3);

        mockMvc.perform(post("/api/vendas")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.produto.id", is(produto.getId().intValue())))
                .andExpect(jsonPath("$.quantidade", is(3)))
                .andExpect(jsonPath("$.valorTotal", is(300.00)));

        mockMvc.perform(get("/api/produtos/" + produto.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidadeEstoque", is(7)));
    }

    @Test
    void deveGerarRelatorioDeVendas() throws Exception {
        Produto produto = salvarProduto("Mouse", "Mouse sem fio", "50.00", 8);
        registrarVenda(produto, 2);
        registrarVenda(produto, 1);

        mockMvc.perform(get("/api/vendas/relatorio")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalVendas", is(2)))
                .andExpect(jsonPath("$.totalItensVendidos", is(3)))
                .andExpect(jsonPath("$.faturamentoTotal", is(150.00)));
    }

    @Test
    void deveBloquearVendaComEstoqueInsuficiente() throws Exception {
        Produto produto = salvarProduto("Monitor", "Monitor LED", "900.00", 1);

        VendaRequest request = new VendaRequest();
        request.setProdutoId(produto.getId());
        request.setQuantidade(2);

        mockMvc.perform(post("/api/vendas")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Estoque insuficiente para realizar a venda.")));
    }

    @Test
    void deveBloquearVendasSemTokenJwt() throws Exception {
        mockMvc.perform(get("/api/vendas"))
                .andExpect(status().isUnauthorized());
    }

    private Produto salvarProduto(String nome, String descricao, String preco, int quantidade) {
        Produto produto = Produto.builder()
                .nome(nome)
                .descricao(descricao)
                .preco(new BigDecimal(preco))
                .quantidadeEstoque(quantidade)
                .build();

        return produtoRepository.save(produto);
    }

    private void registrarVenda(Produto produto, int quantidade) throws Exception {
        VendaRequest request = new VendaRequest();
        request.setProdutoId(produto.getId());
        request.setQuantidade(quantidade);

        mockMvc.perform(post("/api/vendas")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}
