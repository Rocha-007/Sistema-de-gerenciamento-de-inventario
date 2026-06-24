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
    private Produto produto;

    @BeforeEach
    void setUp() {
        vendaRepository.deleteAll();
        produtoRepository.deleteAll();
        usuarioRepository.deleteAll();

        Usuario usuario = Usuario.builder()
                .username("lucas")
                .password(passwordEncoder.encode("123456"))
                .email("lucas@email.com")
                .ativo(true)
                .build();

        usuarioRepository.save(usuario);
        token = jwtTokenUtil.generateToken(usuario);

        produto = Produto.builder()
                .nome("Mouse gamer")
                .descricao("Mouse USB")
                .preco(new BigDecimal("129.90"))
                .quantidadeEstoque(10)
                .build();

        produto = produtoRepository.save(produto);
    }

    @Test
    void deveBloquearVendasSemTokenJwt() throws Exception {
        mockMvc.perform(get("/api/vendas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRegistrarVendaEBaixarEstoqueQuandoTokenJwtForEnviado() throws Exception {
        VendaRequest request = VendaRequest.builder()
                .produtoId(produto.getId())
                .quantidade(3)
                .build();

        mockMvc.perform(post("/api/vendas")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.produtoId", is(produto.getId().intValue())))
                .andExpect(jsonPath("$.nomeProduto", is("Mouse gamer")))
                .andExpect(jsonPath("$.quantidade", is(3)))
                .andExpect(jsonPath("$.valorTotal", is(389.70)));

        Produto produtoAtualizado = produtoRepository.findById(produto.getId()).orElseThrow();

        mockMvc.perform(get("/api/produtos/" + produto.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidadeEstoque", is(7)))
                .andExpect(jsonPath("$.id", is(produtoAtualizado.getId().intValue())));
    }

    @Test
    void deveRetornarConflitoQuandoEstoqueForInsuficiente() throws Exception {
        VendaRequest request = VendaRequest.builder()
                .produtoId(produto.getId())
                .quantidade(20)
                .build();

        mockMvc.perform(post("/api/vendas")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.message", is("Estoque insuficiente para o produto: Mouse gamer")));
    }
}
