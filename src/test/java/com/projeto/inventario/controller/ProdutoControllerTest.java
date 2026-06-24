package com.projeto.inventario.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
class ProdutoControllerTest {

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
                .username("lucas")
                .password(passwordEncoder.encode("123456"))
                .email("lucas@email.com")
                .ativo(true)
                .build();

        usuarioRepository.save(usuario);
        token = jwtTokenUtil.generateToken(usuario);
    }

    @Test
    void deveBloquearCrudSemTokenJwt() throws Exception {
        mockMvc.perform(get("/api/produtos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveCriarProdutoQuandoTokenJwtForEnviado() throws Exception {
        Produto produto = Produto.builder()
                .nome("Mouse gamer")
                .descricao("Mouse USB")
                .preco(new BigDecimal("129.90"))
                .quantidadeEstoque(15)
                .build();

        mockMvc.perform(post("/api/produtos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(produto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome", is("Mouse gamer")))
                .andExpect(jsonPath("$.quantidadeEstoque", is(15)));
    }

    @Test
    void deveRetornarNotFoundComJsonLimpoQuandoProdutoNaoExistir() throws Exception {
        mockMvc.perform(get("/api/produtos/999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Produto nao encontrado com ID: 999")))
                .andExpect(jsonPath("$.path", is("/api/produtos/999")));
    }
}
