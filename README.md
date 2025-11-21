# Módulo 2 – Serviço de Login com API Gateway (Juan · Frettec)

Este repositório implementa o **Módulo 2** do projeto de autenticação do aluno **Juan**, composto por:

- **Módulo de domínio (`domain/`)**: regras de negócio e contratos de portas (ports).
- **Serviço de login (`login/`)**: API REST para **registro** e **autenticação** de usuários.
- **API Gateway (`gateway/`)**: borda única para entrada das requisições, com validação de **JWT**.

A arquitetura aplica princípios de **Clean Architecture**, separando claramente:

- **Domínio** – independente de frameworks.
- **Aplicação** – casos de uso e orquestração.
- **Infraestrutura** – JPA, segurança, JWT, banco de dados.
- **Borda** – API Login e Gateway.

---

## 1. Visão Geral da Solução

O objetivo deste módulo é fornecer um **serviço de autenticação centralizado**, capaz de:

- Registrar novos usuários.
- Autenticar usuários existentes com e-mail e senha.
- Gerar **tokens JWT** para acesso autenticado.
- Validar tokens na borda através de um **API Gateway**.

Fluxo principal:

1. O cliente chama o **Login Service** via `/api/v1/auth/registrar` para registrar um usuário.
2. Depois, autentica em `/api/v1/auth/login` e recebe um **JWT**.
3. O cliente envia esse token no header `Authorization: Bearer <token>` para acessar rotas protegidas atrás do **Gateway**.
4. O **Gateway** valida o token com `JwtValidator` antes de encaminhar a requisição.

---

## 2. Arquitetura e Módulos

Estrutura em alto nível:

```text
modulo2-juan
├── domain/   (núcleo de domínio)
├── login/    (serviço de autenticação)
└── gateway/  (API Gateway)
````

### 2.1 Módulo `domain/`

Pacote base: `com.retrocore.juan.mod2.domain`

Responsável pelas **regras de negócio** e **interfaces** que descrevem os contratos do sistema.

Principais elementos:

* **Model**

  * `UsuarioCredencial`

    * Representa o usuário autenticável (id, email, senhaHash).
* **Exceptions**

  * `CredencialInvalidaException`

    * Lançada quando e-mail ou senha são inválidos.
  * `UsuarioExistenteException`

    * Lançada ao tentar registrar um e-mail já cadastrado.
* **Ports**

  * `UsuarioRepositoryPort`

    * `salvar(UsuarioCredencial usuario)`
    * `buscarPorEmail(String email)`
  * `PasswordEncryptorPort`

    * `hash(String senha)`
    * `comparar(String senhaPura, String hash)`
  * `TokenGeneratorPort`

    * `gerarToken(String email)`

> O módulo `domain` não depende de Spring, banco de dados ou bibliotecas específicas de segurança. Apenas define o comportamento esperado.

---

### 2.2 Módulo `login/` – Serviço de Autenticação

Pacote base: `com.retrocore.juan.mod2.login`

Este módulo contém:

* A **aplicação Spring Boot** do serviço de login.
* Os **casos de uso** (orquestração da lógica).
* A **API REST**.
* Os **adapters** de persistência e segurança.

#### 2.2.1 Aplicação

* `LoginApplication`

  * Classe principal do Spring Boot.
  * Usa `@SpringBootApplication(scanBasePackages = "com.retrocore.juan.mod2")` para enxergar domínio e login.

Configuração em `login/src/main/resources/application.yml`:

* Porta padrão: `8080`
* Datasource:

  * `SPRING_DATASOURCE_URL`
  * `SPRING_DATASOURCE_USERNAME`
  * `SPRING_DATASOURCE_PASSWORD`
* JWT:

  * `JWT_SECRET`
  * `JWT_EXPIRATION_MS`

Todos esses valores são injetados via variáveis de ambiente (setadas no `docker-compose.yml`).

#### 2.2.2 API REST

Pacote: `com.retrocore.juan.mod2.login.api`

* `AutenticacaoController`

  * Base path: `/api/v1/auth`
  * Endpoints:

    * `POST /api/v1/auth/registrar`
    * `POST /api/v1/auth/login`
* `ApiExceptionHandler`

  * Trata:

    * `UsuarioExistenteException` → `409 CONFLICT`
    * `CredencialInvalidaException` → `401 UNAUTHORIZED`

**DTOs** (pacote `application.dto`):

* `RegistroDTO(String email, String senha)`
* `LoginDTO(String email, String senha)`
* `TokenDTO(String token)`

#### 2.2.3 Casos de uso (Use Cases)

Pacote: `com.retrocore.juan.mod2.login.application.usecase`

* `RegistrarUsuarioUseCase`

  * Verifica se já existe usuário com o e-mail informado.
  * Se existir, lança `UsuarioExistenteException`.
  * Se não existir:

    * Gera o hash da senha via `PasswordEncryptorPort`.
    * Constrói `UsuarioCredencial` usando `UsuarioMapper`.
    * Persiste via `UsuarioRepositoryPort`.

* `AutenticarUsuarioUseCase`

  * Busca usuário pelo e-mail via `UsuarioRepositoryPort`.
  * Compara a senha com `PasswordEncryptorPort.comparar(...)`.
  * Se inválida, lança `CredencialInvalidaException`.
  * Se válida, gera token via `TokenGeneratorPort`.
  * Retorna `TokenDTO` com o JWT.

#### 2.2.4 Infraestrutura: JPA, Bcrypt e JWT

Pacotes:

* `login.infrastructure.jpa`

  * `UsuarioEntity`

    * Entidade JPA com `id`, `email`, `senhaHash`.
  * `UsuarioJpaRepository`

    * Interface `JpaRepository` com método `findByEmail(String email)`.

* `login.infrastructure.adapter`

  * `UsuarioRepositoryAdapter`

    * Implementa `UsuarioRepositoryPort`.
    * Converte entre `UsuarioCredencial` e `UsuarioEntity`.

* `login.infrastructure.security`

  * `BCryptEncryptorAdapter`

    * Implementa `PasswordEncryptorPort` usando `BCryptPasswordEncoder`.
  * `JwtTokenProviderAdapter`

    * Implementa `TokenGeneratorPort`.
    * Gera JWT com:

      * `sub` = e-mail do usuário.
      * Algoritmo `HS256`.
      * Expiração baseada em `JWT_EXPIRATION_MS`.

---

### 2.3 Módulo `gateway/` – API Gateway

Pacote base: `com.retrocore.juan.mod2.gateway`

Responsável por:

* Expor a única porta pública do sistema.
* Fazer o **roteamento** para o serviço de login.
* **Validar o JWT** nas rotas protegidas.

#### 2.3.1 Aplicação e Rotas

* `GatewayApplication`

  * Aplicação Spring Boot do gateway.

* `RoteamentoConfig`

  * Define rota:

    * ID: `"login-service"`
    * Path: `/api/v1/auth/**`
    * Destino: `http://login-service:8080` (nome do container Docker).

Configuração em `gateway/src/main/resources/application.yml`:

* Porta padrão: `8080`
* CORS liberado para qualquer origem (útil para testes).
* Propriedade `JWT_SECRET` usada pelo `JwtValidator`.

#### 2.3.2 Filtro Global de JWT

Pacotes:

* `gateway.config`

  * `JwtFiltroGlobal`

    * Implementa `GlobalFilter`.
    * Lógica:

      * Se a rota começar com `/api/v1/auth`, ela é considerada **pública**, o filtro só encaminha.
      * Outras rotas (futuras) devem exigir `Authorization: Bearer <token>`.
      * Se o header estiver ausente ou inválido → responde `401 UNAUTHORIZED` sem chamar o serviço de destino.
      * Usa `JwtValidator` para validar e extrair o e-mail.

* `gateway.security`

  * `JwtValidator`

    * Usa `io.jsonwebtoken.Jwts` para validar o token com o mesmo `JWT_SECRET` do login-service.
    * Método principal: `extrairEmail(String token)`.

---

## 3. Tecnologias Utilizadas

* **Linguagem:** Java 21
* **Framework:** Spring Boot 3
* **APIs:**

  * Spring Web
  * Spring Data JPA
* **Segurança:**

  * `spring-security-crypto` (BCrypt)
  * `jjwt` (JSON Web Tokens)
* **Banco de dados:** PostgreSQL 16
* **Containerização:** Docker e Docker Compose
* **Build:** Maven + Maven Wrapper (`mvnw`)

---

## 4. Execução com Docker Compose

Pré-requisitos:

* Docker
* Docker Compose

Na raiz do projeto (`modulo2-juan`):

```bash
docker-compose up -d
```

Serviços:

* `db` – PostgreSQL

  * Porta local: `5434`
* `login-service` – serviço de autenticação

  * Porta interna: `8080` (acessível via gateway)
* `api-gateway` – gateway de entrada

  * Porta local: `8080`

Rede Docker: `mod2-net`.

Para encerrar:

```bash
docker-compose down
```

---

## 5. Endpoints da API de Autenticação

Base (via gateway): `http://localhost:8080/api/v1/auth`

### 5.1 Registrar Usuário

**POST** `/api/v1/auth/registrar`

Request body:

```json
{
  "email": "usuario@teste.com",
  "senha": "minha-senha-forte"
}
```

Respostas:

* `200 OK` – usuário registrado com sucesso.
* `409 CONFLICT` – se já existir usuário com o e-mail informado.

---

### 5.2 Login

**POST** `/api/v1/auth/login`

Request body:

```json
{
  "email": "usuario@teste.com",
  "senha": "minha-senha-forte"
}
```

Resposta `200 OK`:

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

Erros:

* `401 UNAUTHORIZED` – credenciais inválidas.
* O corpo usualmente contém a mensagem de `CredencialInvalidaException`.

---

## 6. Fluxo de Autenticação e Validação de JWT

1. **Registro**

   * O controller recebe `RegistroDTO`.
   * `RegistrarUsuarioUseCase` verifica se existe usuário com o e-mail.
   * Senha é transformada em hash via `BCryptEncryptorAdapter`.
   * `UsuarioRepositoryAdapter` salva em `usuarios`.

2. **Login**

   * O controller recebe `LoginDTO`.
   * `AutenticarUsuarioUseCase` busca usuário por e-mail.
   * Compara a senha com o hash com `BCryptEncryptorAdapter`.
   * Se ok, `JwtTokenProviderAdapter` gera JWT com:

     * `subject = email`.
     * Expiração `JWT_EXPIRATION_MS`.

3. **Gateway**

   * O cliente envia `Authorization: Bearer <token>`.
   * `JwtFiltroGlobal` aplica:

     * Se rota for `/api/v1/auth/**`, passa direto (pública).
     * Futuras rotas protegidas deverão exigir token válido.
   * `JwtValidator` valida a assinatura e extrai o e-mail do token.

---
