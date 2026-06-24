package com.projeto.inventario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resultado de uma operacao de autenticacao.")
public class LoginResponse {
    @Schema(
            description = "Token JWT usado nos endpoints protegidos.",
            example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYXJpYSJ9.assinatura",
            nullable = true
    )
    private String token;

    @Schema(description = "Nome do usuario autenticado.", example = "maria", nullable = true)
    private String username;

    @Schema(description = "Mensagem com o resultado da operacao.", example = "Autenticacao realizada com sucesso!")
    private String message;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static LoginResponseBuilder builder() {
        return new LoginResponseBuilder();
    }

    public static class LoginResponseBuilder {
        private String token;
        private String username;
        private String message;

        public LoginResponseBuilder token(String token) {
            this.token = token;
            return this;
        }

        public LoginResponseBuilder username(String username) {
            this.username = username;
            return this;
        }

        public LoginResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public LoginResponse build() {
            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setUsername(username);
            response.setMessage(message);
            return response;
        }
    }
}
