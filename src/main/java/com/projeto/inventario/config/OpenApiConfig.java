package com.projeto.inventario.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    public static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI inventarioOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Gerenciamento de Inventario")
                        .version("1.0.0")
                        .description("""
                                API REST para autenticacao de usuarios e gerenciamento de produtos.

                                Para usar os endpoints protegidos:
                                1. Registre um usuario ou realize o login.
                                2. Copie o token JWT retornado.
                                3. Clique em Authorize e informe somente o token.
                                """)
                        .contact(new Contact().name("Sistema de Gerenciamento de Inventario")))
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("Ambiente local"))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Token JWT obtido nos endpoints de login ou registro.")));
    }
}
