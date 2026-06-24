package com.projeto.inventario.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Resposta padronizada para erros processados pela API.")
public class ApiErrorResponse {

    @Schema(description = "Data e hora em que o erro ocorreu.", example = "2026-06-24T14:30:00")
    private final LocalDateTime timestamp;
    @Schema(description = "Codigo HTTP.", example = "404")
    private final int status;
    @Schema(description = "Nome do status HTTP.", example = "Not Found")
    private final String error;
    @Schema(description = "Detalhes do erro.", example = "Produto nao encontrado com ID: 99")
    private final String message;
    @Schema(description = "Caminho que originou o erro.", example = "/api/produtos/99")
    private final String path;

    public ApiErrorResponse(int status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }
}
