package com.projeto.inventario.controller;

import com.projeto.inventario.dto.LoginRequest;
import com.projeto.inventario.dto.LoginResponse;
import com.projeto.inventario.model.Usuario;
import com.projeto.inventario.repository.UsuarioRepository;
import com.projeto.inventario.util.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controlador de Autenticação
 * Responsável pelos endpoints de login e registro de usuários
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticacao", description = "Registro de usuarios e emissao de tokens JWT.")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Endpoint de Login - Tópico 3
     * Rota pública de autenticação que recebe usuário e senha
     * Retorna um Token JWT caso as credenciais sejam válidas
     *
     * @param loginRequest Objeto contendo username e password
     * @return Token JWT e informações do usuário
     */
    @PostMapping("/login")
    @Operation(
            summary = "Autenticar usuario",
            description = "Valida usuario e senha e retorna um token JWT com validade de 24 horas."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Autenticacao realizada.",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Credenciais invalidas.",
                    content = @Content(
                            schema = @Schema(implementation = LoginResponse.class),
                            examples = @ExampleObject(value = """
                                    {"token":null,"username":null,"message":"Erro na autenticacao: credenciais invalidas!"}
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno ao processar a autenticacao.",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))
            )
    })
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Autentica o usuário com as credenciais fornecidas
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // Obtém o usuário autenticado
            Usuario usuario = (Usuario) authentication.getPrincipal();

            // Gera o token JWT
            String token = jwtTokenUtil.generateToken(usuario);

            // Retorna o token e informações do usuário
            LoginResponse response = LoginResponse.builder()
                    .token(token)
                    .username(usuario.getUsername())
                    .message("Autenticação realizada com sucesso!")
                    .build();

            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(LoginResponse.builder()
                            .message("Erro na autenticação: credenciais inválidas!")
                            .build()
                    );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(LoginResponse.builder()
                            .message("Erro ao processar a autenticação!")
                            .build()
                    );
        }
    }

    /**
     * Endpoint de Registro - Cria um novo usuário
     *
     * @param usuario Dados do usuário a ser criado
     * @return Usuário criado e token de autenticação
     */
    @PostMapping("/register")
    @Operation(
            summary = "Registrar usuario",
            description = "Cria um usuario, criptografa sua senha com BCrypt e retorna um token JWT."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuario registrado.",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "JSON ausente ou invalido."),
            @ApiResponse(
                    responseCode = "409",
                    description = "Username ou e-mail ja cadastrado.",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno ao registrar o usuario.",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))
            )
    })
    public ResponseEntity<?> register(@RequestBody Usuario usuario) {
        try {
            // Verifica se o usuário já existe
            Optional<Usuario> usuarioExistente = usuarioRepository.findByUsername(usuario.getUsername());
            if (usuarioExistente.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(LoginResponse.builder()
                                .message("Username já está em uso!")
                                .build()
                        );
            }

            // Verifica se o email já está registrado
            Optional<Usuario> emailExistente = usuarioRepository.findByEmail(usuario.getEmail());
            if (emailExistente.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(LoginResponse.builder()
                                .message("Email já está registrado!")
                                .build()
                        );
            }

            // Criptografa a senha
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            usuario.setAtivo(true);

            // Salva o novo usuário
            Usuario usuarioSalvo = usuarioRepository.save(usuario);

            // Gera token JWT para o novo usuário
            String token = jwtTokenUtil.generateToken(usuarioSalvo);

            LoginResponse response = LoginResponse.builder()
                    .token(token)
                    .username(usuarioSalvo.getUsername())
                    .message("Usuário registrado com sucesso!")
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(LoginResponse.builder()
                            .message("Erro ao registrar usuário: " + e.getMessage())
                            .build()
                    );
        }
    }
}
