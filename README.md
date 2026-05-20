# Sistema de Gerenciamento de Inventario

Sistema desenvolvido para auxiliar no controle de produtos de um comercio, permitindo organizar entradas, saidas e consultas de itens em estoque por meio de uma API REST.

O projeto utiliza Spring Boot e segue uma arquitetura em camadas, separando controller, service, repository, model, DTO, security e tratamento de excecoes.

## Tecnologias Utilizadas

- Java 17
- Spring Boot 3.2.5
- Spring Web
- Spring Data JPA
- Spring Security
- JWT com biblioteca JJWT
- H2 Database para desenvolvimento
- PostgreSQL como opcao de banco relacional
- Lombok
- JUnit
- MockMvc
- Maven

## Funcionalidades

- Cadastro de usuarios
- Login com geracao de token JWT
- Protecao de rotas com Spring Security
- CRUD de produtos
- Tratamento global de erros da API
- Testes automatizados basicos para seguranca e cadastro de produtos

## Estrutura do Projeto

src/main/java/com/projeto/inventario
|-- controller
|   |-- AuthController.java
|   `-- ProdutoController.java
|-- dto
|   |-- LoginRequest.java
|   `-- LoginResponse.java
|-- exception
|   |-- ApiErrorResponse.java
|   |-- GlobalExceptionHandler.java
|   `-- ResourceNotFoundException.java
|-- model
|   |-- Produto.java
|   `-- Usuario.java
|-- repository
|   |-- ProdutoRepository.java
|   `-- UsuarioRepository.java
|-- security
|   |-- CustomUserDetailsService.java
|   |-- JwtAuthenticationFilter.java
|   `-- SecurityConfig.java
|-- service
|   `-- ProdutoService.java
|-- util
|   `-- JwtTokenUtil.java
`-- InventarioApplication.java

## Seguranca e Autenticacao

A API utiliza Spring Security com JWT para proteger os endpoints do sistema.

Rotas publicas:

- POST /api/auth/register
- POST /api/auth/login
- /h2-console/**

Rotas protegidas:

- Todos os endpoints de CRUD, como /api/produtos

Para acessar uma rota protegida, o cliente deve enviar o token JWT no cabecalho da requisicao:

Authorization: Bearer SEU_TOKEN_JWT

Caso o token nao seja enviado ou seja invalido, a API retorna:

401 Unauthorized

## Endpoints de Autenticacao

### Registrar Usuario

POST /api/auth/register

Exemplo de corpo da requisicao:

{
  "username": "fulano",
  "password": "123456",
  "email": "fulano@email.com"
}

### Login

POST /api/auth/login

Exemplo de corpo da requisicao:

{
  "username": "fulano",
  "password": "123456"
}

Exemplo de resposta:

{
  "token": "token.jwt.gerado",
  "username": "fulano",
  "message": "Autenticacao realizada com sucesso!"
}

## Endpoints de Produtos

Todos os endpoints abaixo exigem token JWT.

### Listar Produtos

GET /api/produtos

### Buscar Produto por ID

GET /api/produtos/{id}

### Criar Produto

POST /api/produtos

Exemplo de corpo da requisicao:

{
  "nome": "Mouse gamer",
  "descricao": "Mouse USB",
  "preco": 129.90,
  "quantidadeEstoque": 15
}

### Atualizar Produto

PUT /api/produtos/{id}

### Deletar Produto

DELETE /api/produtos/{id}

## Tratamento de Erros

O projeto possui um handler global com @ControllerAdvice, responsavel por capturar erros comuns e retornar uma resposta JSON padronizada.

Exemplo: ao buscar um produto inexistente, a API retorna 404 Not Found com um corpo semelhante a:

{
  "timestamp": "2026-05-20T13:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Produto nao encontrado com ID: 999",
  "path": "/api/produtos/999"
}

Isso evita que a API retorne uma mensagem tecnica ou quebre a resposta esperada pelo cliente.

## Banco de Dados

Por padrao, o projeto esta configurado para usar H2 em memoria durante o desenvolvimento:

spring.datasource.url=jdbc:h2:mem:inventario_db
spring.datasource.username=sa
spring.datasource.password=

O console do H2 pode ser acessado em:

http://localhost:8080/h2-console

Tambem existe configuracao comentada para PostgreSQL no arquivo application.properties.

## Como Executar o Projeto

1. Clone o repositorio:

git clone https://github.com/Rocha-007/Sistema-de-gerenciamento-de-inventario.git

2. Entre na pasta do projeto:

cd Sistema-de-gerenciamento-de-inventario

3. Execute a aplicacao:

mvn spring-boot:run

4. Acesse a API em:

http://localhost:8080

## Como Executar os Testes

Para rodar os testes automatizados:

mvn test

Os testes foram criados com JUnit e MockMvc.

Atualmente os testes validam:

- Bloqueio do CRUD de produtos sem token JWT, esperando 401 Unauthorized
- Criacao de produto com token JWT valido, esperando 201 Created
- Retorno 404 Not Found com JSON padronizado ao buscar produto inexistente

## Atualizacoes Recentes

Foram adicionadas as seguintes melhorias ao projeto:

- Configuracao do Spring Security em SecurityConfig
- Autenticacao stateless com JWT
- Bloqueio dos endpoints de CRUD sem token
- Liberacao das rotas de login e cadastro
- Handler global de excecoes com @ControllerAdvice
- Resposta padronizada para erro 404 Not Found
- Testes basicos com JUnit e MockMvc

## Autores

Projeto desenvolvido em grupo para trabalho academico.

