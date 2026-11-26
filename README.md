
# CineShare API

API REST desenvolvida em Java com Spring Boot para uma plataforma de compartilhamento de recomendações de filmes e séries.

## Objetivo

O CineShare é um protótipo que busca permitir aos usuários organizar e compartilhar recomendações de conteúdos audiovisuais.

## Tecnologias

- Java 17
- Spring Boot
- Spring Data JPA
- MySQL
- Maven

## Estrutura do projeto

- `src/main`: código-fonte da aplicação
- `pom.xml`: configuração e dependências Maven
- `mvnw`: Maven Wrapper para Linux/macOS
- `mvnw.cmd`: Maven Wrapper para Windows

## Comandos principais

Execute os comandos a partir da raiz do projeto.

### Compilar

Linux/macOS:

```bash
./mvnw clean compile
```

Windows:

```bat
mvnw.cmd clean compile
```

### Executar os testes

Linux/macOS:

```bash
./mvnw test
```

Windows:

```bat
mvnw.cmd test
```

### Executar a aplicação

Linux/macOS:

```bash
./mvnw spring-boot:run
```

Windows:

```bat
mvnw.cmd spring-boot:run
```

Para gerar o pacote executável, use `./mvnw clean package` no Linux/macOS ou `mvnw.cmd clean package` no Windows. O arquivo gerado fica em `target/`.

## Status

Projeto em desenvolvimento.

ajustar !
