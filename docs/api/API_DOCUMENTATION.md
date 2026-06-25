# Documentação da API REST do Sistema de Gerenciamento de Inventário

Este documento detalha a API RESTful do Sistema de Gerenciamento de Inventário, fornecendo informações sobre seus endpoints, métodos HTTP, estruturas de requisição e resposta, e como interagir com a documentação interativa (Swagger UI).

## 1. Visão Geral da API

A API é construída com Spring Boot e segue os princípios REST, utilizando JSON para comunicação. Ela é protegida por autenticação JWT (JSON Web Token), garantindo que apenas usuários autorizados possam acessar a maioria dos recursos. A API oferece funcionalidades para:

*   **Autenticação**: Registro e login de usuários.
*   **Produtos**: Operações CRUD (Criar, Ler, Atualizar, Deletar) para produtos no inventário.
*   **Vendas**: Registro de vendas com controle automático de estoque.

## 2. Acesso à Documentação Interativa (Swagger UI)

Com a aplicação em execução, a documentação interativa da API, gerada automaticamente via OpenAPI 3, está disponível nos seguintes endereços:

*   **Swagger UI**: `http://localhost:8080/swagger-ui.html`
*   **Especificação OpenAPI em JSON**: `http://localhost:8080/v3/api-docs`

### Como Testar os Endpoints Protegidos via Swagger UI:

Para interagir com os endpoints que exigem autenticação (a maioria dos endpoints de produtos e vendas), siga os passos:

1.  **Autentique-se**: Execute o endpoint `POST /api/auth/register` (para criar um novo usuário) ou `POST /api/auth/login` (para um usuário existente) na seção `Autenticacao` do Swagger UI.
2.  **Copie o Token**: Após uma autenticação bem-sucedida, a resposta conterá um campo `token`. Copie o valor deste token.
3.  **Autorize no Swagger**: Clique no botão **Authorize** (geralmente no canto superior direito da página do Swagger UI).
4.  **Insira o Token**: No campo de autorização, cole *somente o token*, sem adicionar o prefixo `Bearer` (o Swagger UI adicionará automaticamente).
5.  **Execute as Operações**: Agora você pode executar os endpoints protegidos nas seções `Produtos` e `Vendas`.

## 3. Endpoints da API

### 3.1. Autenticação (`/api/auth`)

Estes endpoints são públicos e não exigem autenticação prévia.

#### 3.1.1. Registrar Usuário

*   **URL**: `/api/auth/register`
*   **Método**: `POST`
*   **Descrição**: Cria um novo usuário no sistema e retorna um token JWT para futuras requisições.
*   **Corpo da Requisição (JSON)**:
    ```json
    {
      "username": "novo_usuario",
      "password": "senha_segura",
      "email": "novo.usuario@email.com"
    }
    ```
*   **Resposta de Sucesso (201 Created)**:
    ```json
    {
      "token": "eyJhbGciOiJIUzI1Ni...",
      "username": "novo_usuario",
      "message": "Usuário registrado com sucesso!"
    }
    ```
*   **Resposta de Erro (409 Conflict)**: Se o username ou email já estiverem em uso.
    ```json
    {
      "token": null,
      "username": null,
      "message": "Username já está em uso!"
    }
    ```

#### 3.1.2. Login de Usuário

*   **URL**: `/api/auth/login`
*   **Método**: `POST`
*   **Descrição**: Autentica um usuário existente e retorna um token JWT.
*   **Corpo da Requisição (JSON)**:
    ```json
    {
      "username": "usuario_existente",
      "password": "senha_existente"
    }
    ```
*   **Resposta de Sucesso (200 OK)**:
    ```json
    {
      "token": "eyJhbGciOiJIUzI1Ni...",
      "username": "usuario_existente",
      "message": "Autenticação realizada com sucesso!"
    }
    ```
*   **Resposta de Erro (401 Unauthorized)**: Se as credenciais forem inválidas.
    ```json
    {
      "token": null,
      "username": null,
      "message": "Erro na autenticação: credenciais inválidas!"
    }
    ```

### 3.2. Produtos (`/api/produtos`)

Todos os endpoints de produtos exigem um token JWT válido no cabeçalho `Authorization: Bearer SEU_TOKEN_JWT`.

#### 3.2.1. Listar Produtos

*   **URL**: `/api/produtos`
*   **Método**: `GET`
*   **Descrição**: Retorna uma lista de todos os produtos cadastrados.
*   **Resposta de Sucesso (200 OK)**:
    ```json
    [
      {
        "id": 1,
        "nome": "Mouse gamer",
        "descricao": "Mouse USB",
        "preco": 129.90,
        "quantidadeEstoque": 15
      },
      {
        "id": 2,
        "nome": "Teclado mecânico",
        "descricao": "Teclado ABNT2 com switches azuis",
        "preco": 249.90,
        "quantidadeEstoque": 10
      }
    ]
    ```

#### 3.2.2. Buscar Produto por ID

*   **URL**: `/api/produtos/{id}`
*   **Método**: `GET`
*   **Descrição**: Retorna os detalhes de um produto específico pelo seu ID.
*   **Parâmetros de Path**: `id` (Long, obrigatório) - O ID do produto.
*   **Resposta de Sucesso (200 OK)**:
    ```json
    {
      "id": 1,
      "nome": "Mouse gamer",
      "descricao": "Mouse USB",
      "preco": 129.90,
      "quantidadeEstoque": 15
    }
    ```
*   **Resposta de Erro (404 Not Found)**: Se o produto não for encontrado.
    ```json
    {
      "timestamp": "2026-05-20T13:00:00",
      "status": 404,
      "error": "Not Found",
      "message": "Produto não encontrado com ID: 999",
      "path": "/api/produtos/999"
    }
    ```

#### 3.2.3. Criar Produto

*   **URL**: `/api/produtos`
*   **Método**: `POST`
*   **Descrição**: Adiciona um novo produto ao inventário.
*   **Corpo da Requisição (JSON)**:
    ```json
    {
      "nome": "Fone de ouvido",
      "descricao": "Fone com microfone",
      "preco": 89.90,
      "quantidadeEstoque": 50
    }
    ```
*   **Resposta de Sucesso (201 Created)**:
    ```json
    {
      "id": 3,
      "nome": "Fone de ouvido",
      "descricao": "Fone com microfone",
      "preco": 89.90,
      "quantidadeEstoque": 50
    }
    ```

#### 3.2.4. Atualizar Produto

*   **URL**: `/api/produtos/{id}`
*   **Método**: `PUT`
*   **Descrição**: Atualiza os detalhes de um produto existente pelo seu ID.
*   **Parâmetros de Path**: `id` (Long, obrigatório) - O ID do produto a ser atualizado.
*   **Corpo da Requisição (JSON)**:
    ```json
    {
      "nome": "Mouse gamer RGB",
      "descricao": "Mouse USB com LEDs RGB",
      "preco": 149.90,
      "quantidadeEstoque": 20
    }
    ```
*   **Resposta de Sucesso (200 OK)**:
    ```json
    {
      "id": 1,
      "nome": "Mouse gamer RGB",
      "descricao": "Mouse USB com LEDs RGB",
      "preco": 149.90,
      "quantidadeEstoque": 20
    }
    ```
*   **Resposta de Erro (404 Not Found)**: Se o produto não for encontrado.

#### 3.2.5. Deletar Produto

*   **URL**: `/api/produtos/{id}`
*   **Método**: `DELETE`
*   **Descrição**: Remove um produto do inventário pelo seu ID.
*   **Parâmetros de Path**: `id` (Long, obrigatório) - O ID do produto a ser deletado.
*   **Resposta de Sucesso (204 No Content)**: Nenhuma resposta de corpo.
*   **Resposta de Erro (404 Not Found)**: Se o produto não for encontrado.

### 3.3. Vendas (`/api/vendas`)

Todos os endpoints de vendas exigem um token JWT válido no cabeçalho `Authorization: Bearer SEU_TOKEN_JWT`.

#### 3.3.1. Listar Vendas

*   **URL**: `/api/vendas`
*   **Método**: `GET`
*   **Descrição**: Retorna uma lista de todas as vendas registradas.
*   **Resposta de Sucesso (200 OK)**:
    ```json
    [
      {
        "id": 1,
        "produtoId": 1,
        "nomeProduto": "Mouse gamer",
        "quantidade": 2,
        "precoUnitario": 129.90,
        "valorTotal": 259.80,
        "dataVenda": "2026-06-23T20:00:00"
      }
    ]
    ```

#### 3.3.2. Buscar Venda por ID

*   **URL**: `/api/vendas/{id}`
*   **Método**: `GET`
*   **Descrição**: Retorna os detalhes de uma venda específica pelo seu ID.
*   **Parâmetros de Path**: `id` (Long, obrigatório) - O ID da venda.
*   **Resposta de Sucesso (200 OK)**:
    ```json
    {
      "id": 1,
      "produtoId": 1,
      "nomeProduto": "Mouse gamer",
      "quantidade": 2,
      "precoUnitario": 129.90,
      "valorTotal": 259.80,
      "dataVenda": "2026-06-23T20:00:00"
    }
    ```
*   **Resposta de Erro (404 Not Found)**: Se a venda não for encontrada.

#### 3.3.3. Registrar Venda

*   **URL**: `/api/vendas`
*   **Método**: `POST`
*   **Descrição**: Registra uma nova venda e automaticamente baixa a quantidade do produto em estoque.
*   **Corpo da Requisição (JSON)**:
    ```json
    {
      "produtoId": 1,
      "quantidade": 2
    }
    ```
*   **Resposta de Sucesso (201 Created)**:
    ```json
    {
      "id": 1,
      "produtoId": 1,
      "nomeProduto": "Mouse gamer",
      "quantidade": 2,
      "precoUnitario": 129.90,
      "valorTotal": 259.80,
      "dataVenda": "2026-06-23T20:00:00"
    }
    ```
*   **Resposta de Erro (409 Conflict)**: Se o estoque for insuficiente para a quantidade solicitada.
    ```json
    {
      "timestamp": "2026-06-23T20:00:00",
      "status": 409,
      "error": "Conflict",
      "message": "Estoque insuficiente para o produto: Mouse gamer",
      "path": "/api/vendas"
    }
    ```

## 4. Tratamento de Erros Global

A API implementa um `GlobalExceptionHandler` (`@ControllerAdvice`) para padronizar as respostas de erro. Em caso de exceções, a API retorna um JSON consistente com os seguintes campos:

*   `timestamp`: Data e hora do erro.
*   `status`: Código de status HTTP.
*   `error`: Mensagem de erro HTTP (e.g., "Not Found", "Conflict").
*   `message`: Mensagem detalhada do erro.
*   `path`: O endpoint que gerou o erro.

**Exemplo de Resposta de Erro (404 Not Found)**:

```json
{
  "timestamp": "2026-05-20T13:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Produto não encontrado com ID: 999",
  "path": "/api/produtos/999"
}
```

## 5. Segurança

A API utiliza Spring Security com JWT para proteger seus endpoints. As rotas públicas são `/api/auth/register`, `/api/auth/login`, `/h2-console/**`, `/swagger-ui.html`, `/swagger-ui/**` e `/v3/api-docs/**`. Todas as outras rotas exigem autenticação via JWT.

Para acessar rotas protegidas, o cliente deve enviar o token JWT no cabeçalho da requisição:

`Authorization: Bearer SEU_TOKEN_JWT`

Caso o token não seja enviado ou seja inválido, a API retorna `401 Unauthorized`.
