# ELOVA API — Backend

## Estrutura de Packages

```
src/main/java/com/elova/api/
│
├── ElovaApiApplication.java          ← Classe principal Spring Boot
│
├── config/
│   ├── SecurityConfig.java           ← Spring Security + JWT + CORS
│   └── WebClientConfig.java          ← HTTP client para APIs externas
│
├── controller/
│   └── AuthController.java           ← Endpoints do Módulo 1 (Auth)
│
├── dto/
│   ├── CadastroDTO.java              ← Dados de cadastro (com validações)
│   ├── LoginDTO.java                 ← Dados de login
│   └── AuthDTO.java                  ← TokenResponse, SolicitarResetDTO, ResetSenhaDTO
│
├── exception/
│   ├── ElovaException.java           ← Exceção personalizada com status HTTP
│   └── GlobalExceptionHandler.java   ← Tratamento global de erros → JSON
│
├── model/
│   └── Usuario.java                  ← Entidade JPA (tabela: usuarios)
│
├── repository/
│   └── UsuarioRepository.java        ← JPA Repository do Usuario
│
├── security/
│   ├── JwtService.java               ← Geração e validação de JWT
│   └── JwtAuthFilter.java            ← Filtro que valida JWT em cada requisição
│
├── service/
│   ├── UsuarioService.java           ← Lógica: cadastro, login, recuperação de senha
│   └── EmailService.java             ← Envio de e-mails (boas-vindas, reset)
│
└── content/                          ← MÓDULO 3 - Content Engine
    ├── controller/
    │   └── ContentController.java    ← Endpoints de busca de conteúdo
    ├── dto/
    │   └── ContentDTO.java           ← DTOs de busca e resultado
    └── service/
        ├── ContentService.java       ← Orquestra as APIs externas
        ├── WikipediaService.java     ← Integração Wikipedia (gratuita)
        ├── YouTubeService.java       ← Integração YouTube Data API v3
        └── OpenLibraryService.java   ← Integração Open Library (gratuita)
```

---

## Módulo 1 — Auth

### Endpoints

| Método | Rota                        | Autenticação | Descrição                              |
|--------|-----------------------------|:------------:|----------------------------------------|
| POST   | `/auth/cadastro`            | ❌ Pública   | Cadastra novo usuário                  |
| POST   | `/auth/login`               | ❌ Pública   | Autentica e retorna JWT                |
| POST   | `/auth/recuperar-senha`     | ❌ Pública   | Envia e-mail de recuperação            |
| POST   | `/auth/redefinir-senha`     | ❌ Pública   | Redefine senha com token do e-mail     |
| GET    | `/auth/perfil`              | ✅ JWT       | Retorna dados do usuário logado        |

### Validações do Cadastro

| Campo           | Regras                                                               |
|-----------------|----------------------------------------------------------------------|
| `nome`          | Obrigatório · Apenas letras · Mín 4 · Máx 100 caracteres            |
| `email`         | Obrigatório · Formato válido · Máx 150 caracteres                   |
| `senha`         | Obrigatório · Mín 8 · Máx 72 · 1 maiúscula · 1 caractere especial  |
| `confirmarSenha`| Obrigatório · Máx 72 · Deve ser igual à senha                       |

### Exemplos de Uso

**Cadastro:**
```json
POST /auth/cadastro
{
  "nome": "Maria Silva",
  "email": "maria@email.com",
  "senha": "Senha@123",
  "confirmarSenha": "Senha@123"
}
```

**Login:**
```json
POST /auth/login
{
  "email": "maria@email.com",
  "senha": "Senha@123"
}
```
Resposta:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tipo": "Bearer",
  "nome": "Maria Silva",
  "email": "maria@email.com"
}
```

**Recuperar senha:**
```json
POST /auth/recuperar-senha
{ "email": "maria@email.com" }
```

**Redefinir senha:**
```json
POST /auth/redefinir-senha
{
  "token": "eyJ...",
  "novaSenha": "NovaSenha@456",
  "confirmarNovaSenha": "NovaSenha@456"
}
```

**Perfil (com JWT):**
```
GET /auth/perfil
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

## Módulo 3 — Content Engine

### Endpoints

| Método | Rota                            | Descrição                                         |
|--------|---------------------------------|---------------------------------------------------|
| GET    | `/content/buscar`               | Busca em YouTube + Wikipedia + Open Library       |
| GET    | `/content/materia`              | Busca por matéria e tópico do plano de estudos    |
| GET    | `/content/wikipedia/{titulo}`   | Resumo de artigo específico da Wikipedia          |

### Exemplos

```
GET /content/buscar?q=algebra+linear&tipo=TODOS
GET /content/buscar?q=fotossíntese&tipo=VIDEO&limite=5
GET /content/buscar?q=história+do+brasil&dificuldade=FACIL
GET /content/materia?materia=Matemática&topico=Funções&dificuldade=MEDIO
GET /content/wikipedia/Álgebra linear
```

### APIs Externas Utilizadas

| Fonte        | Tipo      | Gratuita | Key necessária |
|--------------|-----------|:--------:|:--------------:|
| Wikipedia    | Artigos   | ✅ Sim   | ❌ Não         |
| Open Library | Livros    | ✅ Sim   | ❌ Não         |
| YouTube      | Vídeos    | ✅ Sim*  | ✅ Sim         |

*YouTube: 10.000 unidades/dia grátis. Cada busca = 100 unidades.

---

## Configuração

### 1. Banco de dados
Crie o banco no MySQL:
```sql
CREATE DATABASE elova_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. application.properties
Configure as seguintes chaves:
```properties
spring.datasource.password=SUA_SENHA_MYSQL
spring.mail.username=SEU_EMAIL@gmail.com
spring.mail.password=SUA_SENHA_DE_APP_GMAIL
elova.api.youtube.key=SUA_YOUTUBE_API_KEY
```

### 3. Obter chave do YouTube
1. Acesse https://console.cloud.google.com
2. Crie um projeto → Ative a YouTube Data API v3
3. Crie uma credencial do tipo "API Key"
4. Cole em `elova.api.youtube.key`

### 4. Rodar o projeto
```bash
mvn spring-boot:run
# ou pelo IntelliJ: Run → ElovaApiApplication
```

---

## Segurança

- Senhas armazenadas com **BCrypt** (fator 12)
- Autenticação via **JWT** (24h de validade)
- Tokens de reset com validade de **30 minutos**
- Rotas protegidas exigem header `Authorization: Bearer <token>`
- CSRF desabilitado (API stateless)
- CORS configurado (ajuste em `SecurityConfig` para produção)
