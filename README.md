
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

## Executar pelo VS Code

Abra a pasta do projeto no VS Code e use **Terminal > Novo Terminal**. É necessário ter JDK 17 e um MySQL acessível. Configure as variáveis antes de iniciar.

PowerShell:

```powershell
$env:SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/cineshare"
$env:SPRING_DATASOURCE_USERNAME="root"
$env:SPRING_DATASOURCE_PASSWORD="sua-senha"
$env:TMDB_TOKEN="seu-token"
$env:TMDB_API_KEY="sua-chave"
.\mvnw.cmd spring-boot:run
```

WSL, Linux ou macOS:

```bash
export SPRING_DATASOURCE_URL='jdbc:mysql://localhost:3306/cineshare'
export SPRING_DATASOURCE_USERNAME='root'
export SPRING_DATASOURCE_PASSWORD='sua-senha'
export TMDB_TOKEN='seu-token'
export TMDB_API_KEY='sua-chave'
bash ./mvnw spring-boot:run
```

A aplicação fica disponível em `http://localhost:8080/`. Para verificar o projeto no mesmo terminal, execute `bash ./mvnw test` ou `.\mvnw.cmd test` no PowerShell. Para compilar sem iniciar o servidor, use `bash ./mvnw -q -DskipTests compile` ou `.\mvnw.cmd -q -DskipTests compile`.
