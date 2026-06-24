# Sistema de Gerenciamento de Inventário

Sistema desenvolvido para auxiliar no controle de produtos de um comércio, permitindo organizar entradas, saídas e consultas de itens em estoque por meio de uma API RESTful. O projeto também inclui uma interface gráfica (GUI) em Swing para interação do usuário.

## 1. Visão Geral do Projeto

O sistema é construído sobre o framework Spring Boot, seguindo uma arquitetura em camadas bem definida. Ele oferece funcionalidades essenciais para o gerenciamento de inventário, incluindo autenticação de usuários, operações CRUD (Criar, Ler, Atualizar, Deletar) para produtos e registro de vendas com controle automático de estoque.

### 1.1. Tecnologias Utilizadas

O projeto emprega um conjunto robusto de tecnologias:

| Categoria          | Tecnologia           | Versão/Descrição                                   |
| :----------------- | :------------------- | :------------------------------------------------- |
| **Linguagem**      | Java                 | 17                                                 |
| **Framework**      | Spring Boot          | 3.2.5                                              |
| **Web**            | Spring Web           | Suporte a aplicações web e RESTful                 |
| **Persistência**   | Spring Data JPA      | Abstração para acesso a dados com Hibernate        |
| **Segurança**      | Spring Security      | Autenticação e autorização                         |
| **JWT**            | JJWT                 | Geração e validação de JSON Web Tokens             |
| **Banco de Dados** | H2 Database          | Para desenvolvimento (em arquivo local)            |
|                    | PostgreSQL           | Opção para banco de dados relacional em produção   |
| **Produtividade**  | Lombok               | Redução de código boilerplate                      |
| **Testes**         | JUnit, MockMvc       | Testes unitários e de integração                   |
| **Build**          | Maven                | Gerenciamento de dependências e ciclo de vida      |
| **Documentação**   | OpenAPI/Swagger UI   | Documentação interativa da API                     |

### 1.2. Funcionalidades Principais

As principais funcionalidades do sistema incluem:

*   **Cadastro e Login de Usuários**: Permite o registro de novos usuários e o login com geração de tokens JWT.
*   **Segurança**: Proteção de rotas da API usando Spring Security e JWT, garantindo que apenas usuários autenticados e autorizados possam acessar determinadas funcionalidades.
*   **CRUD de Produtos**: Operações completas para gerenciar produtos (criar, listar, buscar por ID, atualizar, deletar).
*   **Registro de Vendas**: Permite registrar vendas, com baixa automática da quantidade em estoque.
*   **Tratamento Global de Erros**: Respostas padronizadas para erros da API, melhorando a experiência do desenvolvedor e do cliente.
*   **Interface Gráfica (Swing)**: Uma aplicação desktop para interagir com a API, permitindo o gerenciamento visual de produtos e vendas.

## 2. Arquitetura do Sistema

O projeto adota uma arquitetura em camadas, comum em aplicações Spring Boot, promovendo a separação de responsabilidades e a manutenibilidade do código. As camadas principais são:

*   **Controller**: Responsável por receber as requisições HTTP, processá-las e retornar as respostas. Atua como a interface externa da API.
*   **Service**: Contém a lógica de negócio da aplicação. Orquestra as operações, valida dados e interage com a camada de repositório.
*   **Repository**: Abstrai a interação com o banco de dados, fornecendo métodos para persistir, buscar, atualizar e deletar entidades.
*   **Model**: Define as entidades de domínio (objetos que representam os dados do negócio, como `Produto`, `Usuario`, `Venda`).
*   **DTO (Data Transfer Object)**: Objetos utilizados para transferir dados entre as camadas, especialmente entre o cliente e o servidor, otimizando a comunicação e controlando quais dados são expostos.
*   **Security**: Componentes relacionados à autenticação e autorização, incluindo filtros JWT e configurações do Spring Security.
*   **Exception**: Classes para tratamento de exceções personalizadas e um handler global para padronizar as respostas de erro da API.
*   **UI (User Interface)**: Contém a lógica da interface gráfica Swing, que consome a API REST.
*   **Util**: Classes utilitárias, como `JwtTokenUtil` para manipulação de JWTs.

## 3. Fluxo de Dados e Funcionalidades Detalhadas

### 3.1. Autenticação e Autorização

O sistema implementa um fluxo de autenticação baseado em JWT (JSON Web Token) utilizando Spring Security.

#### 3.1.1. Registro de Usuário

1.  **Requisição**: Um cliente (seja a UI Swing ou outro cliente REST) envia uma requisição `POST` para `/api/auth/register` com `username`, `password` e `email`.
2.  **Controller (`AuthController.java`)**: Recebe a requisição. Antes de salvar, verifica se o `username` ou `email` já existem no banco de dados através do `UsuarioRepository`.
3.  **Criptografia de Senha**: Se o usuário for único, a senha é criptografada usando `BCryptPasswordEncoder` antes de ser persistida.
4.  **Persistência**: O novo `Usuario` é salvo no banco de dados via `UsuarioRepository`.
5.  **Geração de JWT**: Após o registro bem-sucedido, um JWT é gerado para o novo usuário usando `JwtTokenUtil`.
6.  **Resposta**: A API retorna um `LoginResponse` contendo o JWT, o `username` e uma mensagem de sucesso, com status `201 Created`.

#### 3.1.2. Login de Usuário

1.  **Requisição**: O cliente envia uma requisição `POST` para `/api/auth/login` com `username` e `password`.
2.  **Controller (`AuthController.java`)**: O `AuthenticationManager` do Spring Security tenta autenticar o usuário usando as credenciais fornecidas.
3.  **Validação**: O `DaoAuthenticationProvider` (configurado em `SecurityConfig.java`) utiliza o `CustomUserDetailsService` para carregar os detalhes do usuário e o `PasswordEncoder` para comparar a senha fornecida com a senha criptografada armazenada.
4.  **Geração de JWT**: Se as credenciais forem válidas, um JWT é gerado usando `JwtTokenUtil`.
5.  **Resposta**: A API retorna um `LoginResponse` com o JWT, o `username` e uma mensagem de sucesso, com status `200 OK`. Em caso de credenciais inválidas, retorna `401 Unauthorized`.

#### 3.1.3. Acesso a Rotas Protegidas

1.  **Requisição**: Para acessar endpoints protegidos (como `/api/produtos` ou `/api/vendas`), o cliente deve incluir o JWT no cabeçalho `Authorization` no formato `Bearer SEU_TOKEN_JWT`.
2.  **Filtro JWT (`JwtAuthenticationFilter.java`)**: Este filtro intercepta todas as requisições. Ele extrai o token do cabeçalho, valida-o usando `JwtTokenUtil`, carrega os detalhes do usuário e define o contexto de segurança do Spring Security para a requisição.
3.  **Autorização**: O Spring Security verifica se o usuário autenticado tem permissão para acessar o recurso solicitado. Se o token for inválido ou ausente, a requisição é rejeitada com `401 Unauthorized`.

### 3.2. Gerenciamento de Produtos

O CRUD de produtos é uma funcionalidade central, acessível apenas por usuários autenticados.

1.  **Requisição**: O cliente envia requisições HTTP (GET, POST, PUT, DELETE) para `/api/produtos` ou `/api/produtos/{id}`.
2.  **Controller (`ProdutoController.java`)**: Recebe as requisições e delega a lógica de negócio para o `ProdutoService`.
3.  **Service (`ProdutoService.java`)**: Contém a lógica para listar, criar, buscar, atualizar e deletar produtos. Realiza validações como nome não vazio, preço maior que zero e quantidade em estoque não negativa.
4.  **Persistência**: O `ProdutoService` interage com o `ProdutoRepository` para persistir e recuperar dados da entidade `Produto` no banco de dados.
5.  **Entidade (`Produto.java`)**: Representa a estrutura de um produto, incluindo `id`, `nome`, `descricao`, `preco` e `quantidadeEstoque`.
6.  **Resposta**: A API retorna os dados do produto ou uma confirmação da operação, com status HTTP apropriados (e.g., `200 OK`, `201 Created`, `204 No Content` para DELETE, `404 Not Found` se o produto não existir).

### 3.3. Gerenciamento de Vendas

O registro de vendas é uma funcionalidade crítica que envolve a atualização do estoque.

1.  **Requisição**: O cliente envia uma requisição `POST` para `/api/vendas` com `produtoId` e `quantidade`.
2.  **Controller (`VendaController.java`)**: Recebe a requisição e delega para o `VendaService`.
3.  **Service (`VendaService.java`)**: Esta é a camada central para o fluxo de vendas:
    *   **Validação**: Verifica se os dados da requisição são válidos (e.g., `produtoId` não nulo, `quantidade` maior que zero).
    *   **Busca de Produto**: Recupera o `Produto` correspondente ao `produtoId` do `ProdutoRepository`.
    *   **Verificação de Estoque**: Compara a `quantidade` solicitada com a `quantidadeEstoque` do produto. Se o estoque for insuficiente, uma `IllegalStateException` é lançada, resultando em um `409 Conflict` na API.
    *   **Cálculo de Valores**: Calcula o `valorTotal` da venda com base no `precoUnitario` do produto e na `quantidade` vendida.
    *   **Baixa de Estoque**: Se houver estoque suficiente, a `quantidadeEstoque` do `Produto` é atualizada (reduzida) e o produto é salvo novamente no `ProdutoRepository`.
    *   **Persistência da Venda**: Uma nova entidade `Venda` é criada com os detalhes da transação e salva no `VendaRepository`.
4.  **Entidade (`Venda.java`)**: Representa a estrutura de uma venda, incluindo um relacionamento `ManyToOne` com `Produto`, `quantidade`, `precoUnitario`, `valorTotal` e `dataVenda`.
5.  **Resposta**: A API retorna um `VendaResponse` com os detalhes da venda registrada, com status `201 Created`.

### 3.4. Tratamento de Erros

O sistema possui um `GlobalExceptionHandler` (`@ControllerAdvice`) que padroniza as respostas de erro da API. Isso garante que, em caso de exceções, o cliente receba um JSON consistente com `timestamp`, `status`, `error`, `message` e `path`, evitando vazamento de detalhes internos da aplicação. Por exemplo, `ResourceNotFoundException` resulta em `404 Not Found`, e `IllegalStateException` (como estoque insuficiente) em `409 Conflict`.

## 4. Persistência de Dados

O projeto utiliza Spring Data JPA para persistência de dados. Por padrão, ele é configurado para usar o banco de dados H2 em modo de arquivo local (`jdbc:h2:file:./data/inventario_db`), o que significa que os dados são persistidos em disco e não são perdidos ao reiniciar a aplicação. Há também configurações comentadas para PostgreSQL, permitindo fácil transição para um ambiente de produção.

As entidades `Produto`, `Usuario` e `Venda` são mapeadas para tabelas no banco de dados (`produtos`, `usuarios`, `vendas`, respectivamente) usando anotações JPA (`@Entity`, `@Table`, `@Id`, `@GeneratedValue`, `@Column`, `@ManyToOne`).

## 5. Interface de Usuário (Swing)

O projeto inclui uma interface gráfica de usuário desenvolvida em Swing, que atua como um cliente para a API REST. As telas principais são `LoginScreen.java` e `ProdutoScreen.java`.

*   **`InventarioApiClient.java`**: Esta classe é o ponto central de comunicação da UI Swing com a API REST. Ela encapsula as chamadas HTTP para os endpoints de autenticação, produtos e vendas. Gerencia o token JWT, adicionando-o automaticamente aos cabeçalhos das requisições protegidas.
*   **Fluxo de Interação**: A `LoginScreen` permite que o usuário se registre ou faça login. Após o login bem-sucedido, o JWT retornado pela API é armazenado no `InventarioApiClient` e a `ProdutoScreen` é carregada. A `ProdutoScreen` então utiliza o `InventarioApiClient` para realizar operações CRUD de produtos, exibindo os resultados em uma tabela e permitindo a interação do usuário para criar, editar ou excluir itens.
*   **Tratamento de Erros na UI**: O `InventarioApiClient` também é responsável por tratar erros da API, convertendo as respostas de erro JSON em mensagens amigáveis para exibição na interface Swing.

## 6. Divisão de Responsabilidades da Equipe

Para o desenvolvimento deste projeto, as responsabilidades foram divididas da seguinte forma:

### Guilherme — Interface (Swing)

*   **Responsável por**: Criar a interface gráfica em Swing, incluindo telas de Cadastro de produtos, Registro de vendas, Estoque e Relatórios. Fazer o consumo da API e garantir a usabilidade (fluxo simples e funcional).
*   **Cobre**: Integração Interface + API, Usabilidade.

### Lucas — Backend / API

*   **Responsável por**: Desenvolver a API REST, implementando Cadastro de produtos, Registro de vendas e Controle de estoque. Criar regras de negócio e proteger a API (login/autenticação).
*   **Cobre**: Funcionamento geral, API protegida, Qualidade arquitetural.

### Eduardo — Relatórios + Testes de Integração

*   **Responsável por**: Desenvolver relatórios de vendas. Validar o fluxo completo de vendas e a baixa no estoque. Fazer testes gerais do sistema e ajudar a corrigir bugs.
*   **Cobre**: Funcionamento geral, Relatórios, Integração.

### Daniel — Documentação + GitHub + Entrega

*   **Responsável por**: Criar README completo, documentar a API e como rodar o sistema. Organizar o repositório no GitHub, revisar a entrega final e ajudar na preparação da apresentação.
*   **Cobre**: Documentação final, Entrega + apresentação.

## 7. Como Executar o Projeto

1.  **Clone o repositório**:
    ```bash
    git clone https://github.com/Rocha-007/Sistema-de-gerenciamento-de-inventario.git
    ```
2.  **Entre na pasta do projeto**:
    ```bash
    cd Sistema-de-gerenciamento-de-inventario
    ```
3.  **Execute a aplicação**:
    ```bash
    mvn spring-boot:run
    ```
    Esse comando inicia a API e abre a tela de login Swing. Em ambientes sem interface gráfica, somente a API será iniciada.
4.  **A API fica disponível em**:
    `http://localhost:8080`

### 7.1. Como Executar a Interface Swing

Normalmente a interface já é aberta pelo comando `mvn spring-boot:run`. Também é possível executar `LoginScreen` diretamente pela IDE, desde que o backend esteja rodando em `http://localhost:8080`.

A interface permite criar uma conta, entrar, listar, cadastrar, atualizar e excluir produtos. Todas as operações são enviadas para a API REST e persistidas no banco H2; a interface não mantém registros simulados.

### 7.2. Como Executar os Testes

Para rodar os testes automatizados:

```bash
mvn test
```

Os testes foram criados com JUnit e MockMvc.

Atualmente os testes validam:

*   Bloqueio do CRUD de produtos sem token JWT, esperando 401 Unauthorized
*   Criação de produto com token JWT válido, esperando 201 Created
*   Retorno 404 Not Found com JSON padronizado ao buscar produto inexistente
*   Bloqueio dos endpoints de vendas sem token JWT
*   Registro de venda com token JWT válido
*   Baixa automática do estoque após uma venda
*   Retorno 409 Conflict quando o estoque for insuficiente

## 8. Documentação da API (Swagger UI)

Com a aplicação em execução, a documentação interativa da API está disponível em:

*   **Swagger UI**: `http://localhost:8080/swagger-ui.html`
*   **Especificação OpenAPI em JSON**: `http://localhost:8080/v3/api-docs`

Para testar os endpoints de produtos pelo Swagger:

1.  Execute `POST /api/auth/register` ou `POST /api/auth/login`.
2.  Copie o valor do campo `token` retornado.
3.  Clique em **Authorize** no topo da página.
4.  Informe somente o token, sem adicionar o prefixo `Bearer`.
5.  Execute as operações de produtos desejadas.

## 9. Banco de Dados (H2 Console)

Por padrão, o projeto usa um arquivo H2 local. Os dados permanecem salvos mesmo depois que a aplicação é encerrada:

```properties
spring.datasource.url=jdbc:h2:file:./data/inventario_db
spring.datasource.username=sa
spring.datasource.password=
```

O console do H2 pode ser acessado em:

`http://localhost:8080/h2-console`

No console, use `jdbc:h2:file:./data/inventario_db` como JDBC URL, usuário `sa` e deixe a senha em branco.

Também existe configuração comentada para PostgreSQL no arquivo `application.properties`.

## 10. Autores

Projeto desenvolvido em grupo para trabalho acadêmico:

*   **Guilherme** — Interface (Swing)
*   **Lucas** — Backend / API
*   **Eduardo** — Relatórios + Testes de Integração
*   **Daniel** — Documentação + GitHub + Entrega
