package com.projeto.inventario.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.inventario.dto.RelatorioVendasResponse;
import com.projeto.inventario.dto.VendaRequest;
import com.projeto.inventario.model.Produto;
import com.projeto.inventario.model.Venda;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class InventarioApiClient {

    private static final String DEFAULT_BASE_URL = "http://localhost:8080";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private String token;

    public InventarioApiClient() {
        this(DEFAULT_BASE_URL);
    }

    public InventarioApiClient(String baseUrl) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules();
        this.baseUrl = baseUrl;
    }

    public AuthResult registrar(String username, String password, String email) {
        AuthRequest request = new AuthRequest(username, password, email);
        AuthResult result = send("/api/auth/register", "POST", request, AuthResult.class, false);
        this.token = result.token();
        return result;
    }

    public AuthResult login(String username, String password) {
        AuthRequest request = new AuthRequest(username, password, null);
        AuthResult result = send("/api/auth/login", "POST", request, AuthResult.class, false);
        this.token = result.token();
        return result;
    }

    public List<Produto> listarProdutos() {
        return send("/api/produtos", "GET", null, new TypeReference<>() {}, true);
    }

    public Produto criarProduto(Produto produto) {
        return send("/api/produtos", "POST", produto, Produto.class, true);
    }

    public Produto atualizarProduto(Long id, Produto produto) {
        return send("/api/produtos/" + id, "PUT", produto, Produto.class, true);
    }

    public void deletarProduto(Long id) {
        send("/api/produtos/" + id, "DELETE", null, Void.class, true);
    }

    public Venda registrarVenda(Long produtoId, Integer quantidade) {
        VendaRequest request = new VendaRequest();
        request.setProdutoId(produtoId);
        request.setQuantidade(quantidade);
        return send("/api/vendas", "POST", request, Venda.class, true);
    }

    public List<Venda> listarVendas() {
        return send("/api/vendas", "GET", null, new TypeReference<>() {}, true);
    }

    public RelatorioVendasResponse obterRelatorioVendas() {
        return send("/api/vendas/relatorio", "GET", null, RelatorioVendasResponse.class, true);
    }

    public boolean isAutenticado() {
        return token != null && !token.isBlank();
    }

    public void limparSessao() {
        this.token = null;
    }

    private <T> T send(String path, String method, Object body, Class<T> responseType, boolean autenticado) {
        try {
            HttpResponse<String> response = httpClient.send(
                    buildRequest(path, method, body, autenticado),
                    HttpResponse.BodyHandlers.ofString()
            );
            return parseResponse(response, responseType);
        } catch (IOException e) {
            throw new ApiClientException("Nao foi possivel conectar ao backend. Verifique se a API esta rodando.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiClientException("Operacao interrompida.");
        }
    }

    private <T> T send(String path, String method, Object body, TypeReference<T> responseType, boolean autenticado) {
        try {
            HttpResponse<String> response = httpClient.send(
                    buildRequest(path, method, body, autenticado),
                    HttpResponse.BodyHandlers.ofString()
            );
            return parseResponse(response, responseType);
        } catch (IOException e) {
            throw new ApiClientException("Nao foi possivel conectar ao backend. Verifique se a API esta rodando.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiClientException("Operacao interrompida.");
        }
    }

    private HttpRequest buildRequest(String path, String method, Object body, boolean autenticado) throws IOException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Content-Type", "application/json");

        if (autenticado) {
            if (!isAutenticado()) {
                throw new ApiClientException("Faca login antes de acessar os produtos.");
            }
            builder.header("Authorization", "Bearer " + token);
        }

        if (body == null) {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        } else {
            builder.method(method, HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)));
        }

        return builder.build();
    }

    private <T> T parseResponse(HttpResponse<String> response, Class<T> responseType) throws IOException {
        validateStatus(response);
        if (responseType == Void.class || response.body() == null || response.body().isBlank()) {
            return null;
        }
        return objectMapper.readValue(response.body(), responseType);
    }

    private <T> T parseResponse(HttpResponse<String> response, TypeReference<T> responseType) throws IOException {
        validateStatus(response);
        return objectMapper.readValue(response.body(), responseType);
    }

    private void validateStatus(HttpResponse<String> response) throws IOException {
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return;
        }

        String message = "Erro na comunicacao com a API. Status: " + response.statusCode();
        if (response.body() != null && !response.body().isBlank()) {
            JsonNode root = objectMapper.readTree(response.body());
            if (root.hasNonNull("message")) {
                message = root.get("message").asText();
            }
        }

        throw new ApiClientException(message);
    }

    private record AuthRequest(String username, String password, String email) {
    }

    public record AuthResult(String token, String username, String message) {
    }

    public static class ApiClientException extends RuntimeException {
        public ApiClientException(String message) {
            super(message);
        }
    }
}
