# Arquitetura do CineShare

O CineShare é uma aplicação web monolítica em um módulo Maven. `CineShareApiApplication` inicia o Spring Boot. O mesmo servidor entrega as páginas estáticas e recebe as chamadas REST. O navegador usa caminhos relativos à origem para consumir a API. [Classe de entrada](../src/main/java/com/cineShare/CineShareApiApplication.java) · [JavaScript](../src/main/resources/static/cineshare.js)

## Estrutura de pastas

```text
CineShare-api/
├── cineShare - documentacao/     # Documentação técnica
├── planes/                      # Diagnóstico e planos de evolução
├── .mvn/wrapper/                 # Configuração da distribuição Maven
├── mvnw, mvnw.cmd                # Scripts do Maven Wrapper
├── pom.xml                       # Dependências e build
└── src/
    ├── main/
    │   ├── java/
    │   │   ├── DTO/                      # Entrada de listas e respostas do TMDB
    │   │   └── com/cineShare/
    │   │       ├── CineShareApiApplication.java
    │   │       ├── controler/            # Controllers REST
    │   │       ├── services/             # Regras e integração com TMDB
    │   │       ├── repository/           # Consultas e acesso JPA
    │   │       └── model/                # Entidades e associações JPA
    │   └── resources/
    │       ├── application.properties
    │       ├── db/migration/             # SQL V1 ainda sem execução automática
    │       └── static/                   # HTML, CSS e JavaScript
    └── test/java/com/cineShare/           # Testes MVC, de serviço e de repositório
```

## Fluxo das requisições

1. O navegador recebe um dos três HTMLs. `cineshare.js` lê o caminho e monta a interface no DOM. O Spring Boot serve `index.html` também em `/`.
2. O JavaScript usa `fetch` para enviar requisições HTTP e consumir JSON. Os controllers fazem o mapeamento de rotas, parâmetros e corpos e chamam os serviços.
3. Os serviços aplicam as regras e usam os repositórios Spring Data. O Hibernate traduz as operações JPA para o banco configurado, MySQL na execução usual.
4. Nas buscas de filme e na criação de listas, `TituloService` acessa o TMDB por HTTP síncrono. A própria página carrega as imagens retornadas pelo backend a partir da CDN.
5. Os controllers retornam entidades, que o Jackson serializa para JSON. O navegador guarda a identificação da aba em `sessionStorage`, o tema em `localStorage` e os dados temporários em memória.

[Regras e componentes](03-backend.md) · [Contratos HTTP](05-api.md) · [Estado no navegador](06-frontend.md)

## Execução local

Use JDK 17, um MySQL acessível com o esquema `cineshare` já criado e um token de leitura do TMDB. Configure `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` e `TMDB_TOKEN` no ambiente. `TMDB_API_KEY` aparece nas propriedades e no README, mas o código atual da busca lê apenas o token. [Configuração](../src/main/resources/application.properties) · [README com exemplos de configuração](../README.md)

| Operação | Linux, macOS ou WSL | PowerShell |
| --- | --- | --- |
| Executar | `bash ./mvnw spring-boot:run` | `.\mvnw.cmd spring-boot:run` |
| Testar | `bash ./mvnw test` | `.\mvnw.cmd test` |
| Empacotar | `bash ./mvnw clean package` | `.\mvnw.cmd clean package` |

Abra `http://localhost:8080/` para acessar a interface com a porta padrão. O pacote gerado fica em `target/`. O Hibernate usa `ddl-auto=update` para atualizar tabelas a partir dos mapeamentos. A diferença entre o esquema efetivo e o V1 está descrita na documentação do banco. [Banco de dados](04-banco-de-dados.md)

## Verificação automatizada

O projeto tem testes unitários dos serviços com Mockito, testes de controllers com MockMvc e serviços simulados, e testes de repositório com `@DataJpaTest` e H2. Esses recortes cobrem regras, rotas e mapeamentos, com o MySQL e o TMDB real fora da suíte. [Testes](../src/test/java/com/cineShare)
