# Diagnóstico atual do CineShare API

**Data da auditoria:** 23 de novembro de 2026. **Escopo:** leitura da árvore de trabalho da `main`, incluindo `listas.html`, que estava fora do Git nessa data. Os três scripts embutidos passaram na checagem de sintaxe JavaScript. A compilação atual, a inicialização e os fluxos integrados com MySQL, TMDB e navegador ficaram sem validação em execução. A auditoria preservou os arquivos do projeto.

O projeto é um protótipo com cadastro, pesquisa de filmes e persistência de listas. As prioridades são proteger credenciais e autoria, alinhar o esquema SQL ao JPA e corrigir o filtro e o detalhe de listas. A sequência de trabalho está no [plano de ajustes](plano-de-ajustes.md).

## 1. Arquitetura e organização de pastas

O CineShare é uma aplicação web monolítica em um módulo Maven. A arquitetura é em camadas. O navegador consome a API Spring MVC, os controllers delegam aos serviços e os repositórios acessam o MySQL. O serviço de títulos também consulta o TMDB.

| Local | Responsabilidade |
|---|---|
| `src/main/java/com/cineShare/controler` | Endpoints REST |
| `src/main/java/com/cineShare/services` | Fluxos de usuários, títulos e listas |
| `src/main/java/com/cineShare/repository` | Consultas e persistência via Spring Data JPA |
| `src/main/java/com/cineShare/model` | Entidades e associações JPA |
| `src/main/java/DTO` | Entrada de criação de listas e mapeamento de respostas do TMDB |
| `src/main/resources/static` | Páginas HTML, CSS e scripts embutidos |
| `src/main/resources/db/migration` | Script SQL V1 |

A separação por responsabilidade facilita localizar o código. A nomenclatura dos pacotes é irregular, e `DTO` está fora da raiz `com.cineShare`, reunindo contratos da API e do provedor externo. [Classe de entrada](../src/main/java/com/cineShare/CineShareApiApplication.java) · [DTO de listas](../src/main/java/DTO/ListaIndicacaoDTO.java) · [DTO do TMDB](../src/main/java/DTO/TmdbResultado.java)

O POM não declara Flyway. O projeto configura `ddl-auto=update`, e o esquema `cineshare` precisa estar acessível na conexão inicial. O V1 cria `usuarios`, `listas` e `filmes`, incluindo status, datas, comentário e unicidade de e-mail. O JPA usa outras entidades e vincula listas a títulos pela tabela `lista_titulos`. Executar o V1 isoladamente não reproduz o esquema usado pela aplicação. [POM](../pom.xml) · [Configuração](../src/main/resources/application.properties) · [SQL V1](../src/main/resources/db/migration/V1__criacao_tabelas.sql) · [Entidade de lista](../src/main/java/com/cineShare/model/ListaIndicacao.java) · [Inicialização de banco no Spring Boot](https://docs.spring.io/spring-boot/how-to/data-initialization.html)

## 2. Tecnologias, dependências e versões

| Área | Versões verificadas na auditoria |
|---|---|
| Plataforma | Java 17 e Spring Boot 4.0.8 |
| Web e JSON | Spring MVC 7.0.9, Tomcat 11.0.24 e Jackson 3.1.5 |
| Persistência | Spring Data JPA 4.0.7, Jakarta Persistence 3.2.0, Hibernate 7.2.24.Final e MySQL Connector/J 9.7.0. A versão do servidor MySQL não está declarada. |
| Build e desenvolvimento | Maven 3.9.16, Maven Wrapper 3.3.4, Lombok 1.18.46 e DevTools 4.0.8 opcional |
| Interface e integrações | HTML, CSS e JavaScript nativo com Fetch e DOM. TMDB API v3 via `java.net.http.HttpClient`. |

As versões gerenciadas foram conferidas no BOM e, onde aplicável, no JAR local existente. O `index.html` empacotado difere da versão analisada no fonte. [POM](../pom.xml) · [Maven Wrapper](../.mvn/wrapper/maven-wrapper.properties) · [BOM do Spring Boot 4.0.8](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-dependencies/4.0.8) · [Busca de filmes na API do TMDB](https://developer.themoviedb.org/reference/search-movie)

- **Atualizações em 23/11/2026.** O Spring Boot 4.0.10 estava disponível na mesma linha e gerenciava Spring Framework 7.0.11, Jackson 3.1.8 e Hibernate 7.2.25.Final. A linha estável mais recente era 4.2.0. O Maven 3.9.16 era o último da linha 3.9 e já existia o Maven 3.10.2. [Spring Boot 4.0.10](https://github.com/spring-projects/spring-boot/releases/tag/v4.0.10) · [BOM do Spring Boot 4.0.10](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-dependencies/4.0.10) · [Histórico do Maven](https://maven.apache.org/docs/history.html)
- **Redundâncias.** O `spring-boot-starter` e o `spring-boot-starter-test` já são trazidos pelos starters especializados. A árvore inspecionada não continha testes em `src/test`. O import `com.fasterxml.jackson.annotation.JsonProperty` é compatível com Jackson 3. [POM](../pom.xml) · [Starter Web MVC](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-webmvc/4.0.8) · [Starter de teste JPA](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-data-jpa-test/4.0.8) · [Guia do Jackson 3](https://github.com/FasterXML/jackson/blob/main/jackson3/MIGRATING_TO_JACKSON_3.md)

## 3. Padrões de desenvolvimento

- As classes seguem `PascalCase`, os métodos seguem `camelCase` e os componentes ativos recebem dependências por construtor. A mistura de `buscar`, `listAll` e `showDbTitulo` dificulta prever nomes. `findByName` também pesquisa por ID. [Controller de usuários](../src/main/java/com/cineShare/controler/UserController.java) · [Controller de títulos](../src/main/java/com/cineShare/controler/TituloController.java)
- `TituloService` combina montagem da requisição HTTP, interpretação da resposta, seleção do filme, deduplicação e persistência. Essa concentração dificulta testar regras de negócio e falhas da integração isoladamente. [Serviço de títulos](../src/main/java/com/cineShare/services/TituloService.java)
- Há imports repetidos em `Titulo`, import curinga em `UserService`, imports sem uso e `System.out.println` no serviço de listas. O `@Data` nas entidades gera setters, igualdade e representação textual que podem incluir campos mutáveis e associações JPA. [Entidade de título](../src/main/java/com/cineShare/model/Titulo.java) · [Serviço de usuários](../src/main/java/com/cineShare/services/UserService.java) · [Serviço de listas](../src/main/java/com/cineShare/services/ListaIndicacaoService.java)

## 4. Componentização e reutilização

- Controllers pequenos e repositórios por entidade dão granularidade adequada para o protótipo. A criação de listas e a consulta pública estão acopladas a `getTitulo()`, que faz consulta externa e pode gravar no banco. Um cliente TMDB reutilizável e operações separadas de busca e gravação reduziriam esse acoplamento. [Serviço de títulos](../src/main/java/com/cineShare/services/TituloService.java) · [Serviço de listas](../src/main/java/com/cineShare/services/ListaIndicacaoService.java)
- A criação de lista já usa um DTO de entrada. Os controllers expõem entidades JPA nas respostas e recebem `User` diretamente no cadastro. DTOs para cadastro, usuário público, resumo e detalhe de lista permitem controlar o formato e os campos retornados. [DTO de listas](../src/main/java/DTO/ListaIndicacaoDTO.java) · [Controller de usuários](../src/main/java/com/cineShare/controler/UserController.java) · [Controller de listas](../src/main/java/com/cineShare/controler/ListaIndicacaoController.java)
- As três páginas combinam marcação, comunicação HTTP e estado visual. A consulta de listas já reaproveita `carregarListas` e `mostrarMensagem` na própria página. As oportunidades de reutilização entre páginas são as chamadas HTTP, os estilos de formulário e o feedback. [Cadastro](../src/main/resources/static/index.html) · [Criação de lista](../src/main/resources/static/lista.html) · [Consulta de listas](../src/main/resources/static/listas.html)

## 5. Gerenciamento de estado e comunicação

| Local | Estado e comunicação |
|---|---|
| Cadastro | Lê nome e e-mail do DOM, chama `POST /user` e guarda o ID retornado no `sessionStorage` da aba. |
| Criação de lista | Mantém a seleção em um array local, atualiza o DOM e envia o ID de usuário no JSON. Recarregar a página descarta a seleção. |
| Consulta de listas | Mantém resultados e mensagens no DOM. Os elementos renderizados referenciam os objetos retornados pela API para abrir os detalhes. |
| Servidor | Usa HTTP/JSON síncrono, chamadas diretas entre camadas e persistência em MySQL. A árvore não contém mensageria, eventos de domínio ou um store global no navegador. |

O ID no navegador serve para transportar uma referência, e o backend apenas verifica se esse usuário existe antes de atribuir autoria. A gravação de cada título ocorre antes da gravação da lista, sem uma transação de serviço que cubra a operação inteira. Se houver falha posterior, os títulos já gravados podem permanecer no banco. [Cadastro](../src/main/resources/static/index.html) · [Criação de lista](../src/main/resources/static/lista.html) · [Consulta de listas](../src/main/resources/static/listas.html) · [Serviço de listas](../src/main/java/com/cineShare/services/ListaIndicacaoService.java)

## 6. Rotas e navegação

| Método e rota | Comportamento mapeado |
|---|---|
| `POST /user` | Cadastra ou devolve usuário encontrado pelo e-mail. Ambos os caminhos retornam `200`. |
| `GET /user` | Lista usuários, com e-mail na resposta. |
| `GET /users/{buscar}` | Busca por ID quando recebe apenas dígitos ou por trecho do nome nos demais casos. |
| `GET /titulos/buscar?nome=…` | Pesquisa o TMDB, escolhe o primeiro resultado e pode gravar um título. |
| `GET /titulos/mostrar` | Lista títulos persistidos. |
| `POST /listas/create` | Grava uma lista com um a três títulos para o usuário indicado no JSON. Retorna `200`. |
| `GET /listas` | Lista todas as listas, com proprietário e títulos. Ignora o filtro `nome` enviado pela tela. |

[Controller de usuários](../src/main/java/com/cineShare/controler/UserController.java) · [Controller de títulos](../src/main/java/com/cineShare/controler/TituloController.java) · [Controller de listas](../src/main/java/com/cineShare/controler/ListaIndicacaoController.java)

- O cadastro redireciona para `lista.html`. A consulta em `listas.html` é acessível pelo caminho direto, sem link nas outras páginas. Cadastro e criação fixam `http://localhost:8080`, o que compromete o acesso por outro dispositivo, domínio ou porta. A tela de consulta usa `/listas` na mesma origem. [Cadastro](../src/main/resources/static/index.html) · [Criação de lista](../src/main/resources/static/lista.html) · [Consulta de listas](../src/main/resources/static/listas.html)
- A aplicação não declara controle de autenticação ou autorização por proprietário. O e-mail também é serializado na propriedade `user` das listas. A classe de credenciais está comentada. [POM](../pom.xml) · [Controller de credenciais](../src/main/java/com/cineShare/controler/CredentialController.java) · [Entidade de lista](../src/main/java/com/cineShare/model/ListaIndicacao.java) · [Entidade de usuário](../src/main/java/com/cineShare/model/User.java)
- As páginas são documentos estáticos e seus scripts são embutidos. O detalhe abre sob demanda usando os títulos da listagem. A consulta alternativa a `GET /listas/{id}` não possui rota correspondente. No JPA, `@ManyToMany` usa carregamento preguiçoso por padrão. Com Open EntityManager in View, a serialização pode disparar consultas adicionais por lista. O possível N+1 exige medição. [Consulta de listas](../src/main/resources/static/listas.html) · [Entidade de lista](../src/main/java/com/cineShare/model/ListaIndicacao.java) · [Open EntityManager in View](https://docs.spring.io/spring-boot/4.0/reference/data/sql.html)

## 7. Responsividade e organização de estilos

- Cadastro e criação usam os estilos padrão do navegador, quebras `<br>` e omitem `viewport`. O cadastro contém cercas de Markdown literais, inclusive antes do `DOCTYPE`, o que pode acionar o modo de compatibilidade. [Cadastro](../src/main/resources/static/index.html) · [Criação de lista](../src/main/resources/static/lista.html)
- A consulta usa CSS embutido com `viewport`, largura máxima e Flexbox com quebra na busca. Tem largura mínima no campo e valores locais para cor, espaçamento e raio, sem tema ou tokens. O comportamento em telas estreitas ficou sem inspeção visual. [Consulta de listas](../src/main/resources/static/listas.html)
- O campo de busca de listas usa placeholder sem rótulo associado, e os resultados são `div` clicáveis sem suporte equivalente a teclado. A adição de filme depende de Enter. Mensagens da consulta não anunciam mudanças a tecnologias assistivas. [Consulta de listas](../src/main/resources/static/listas.html) · [Criação de lista](../src/main/resources/static/lista.html)

## 8. Problemas de tipagem e possíveis erros

| Achado | Consequência |
|---|---|
| Validação de entrada e tratamento de erros incompletos | O backend valida o número de títulos, mas nomes vazios, títulos inválidos e ID de usuário ausente ou inexistente não recebem tratamento adequado. O cadastro não valida o formato de e-mail. Exceções genéricas tendem a transformar falhas previsíveis em `500`. [Serviço de listas](../src/main/java/com/cineShare/services/ListaIndicacaoService.java) · [Serviço de usuários](../src/main/java/com/cineShare/services/UserService.java) |
| Desserialização direta de `User` e deduplicação concorrente | Um ID preenchido pode levar o JPA ao caminho de atualização. A consulta por e-mail seguida de inserção abre uma janela de concorrência. A restrição única do SQL V1 não faz parte do mapeamento atual. [Controller de usuários](../src/main/java/com/cineShare/controler/UserController.java) · [Entidade de usuário](../src/main/java/com/cineShare/model/User.java) · [SQL V1](../src/main/resources/db/migration/V1__criacao_tabelas.sql) |
| Incompatibilidade de tipos e conversão numérica | `findByAnoLancamento(String)` tem parâmetro incompatível com o atributo `LocalDate`. O método não tem chamadas na árvore atual e pode falhar ao associar o parâmetro se passar a ser usado. Uma busca numérica fora da faixa de `Long` pode falhar na conversão de ID. [Repositório de títulos](../src/main/java/com/cineShare/repository/TituloRepository.java) · [Entidade de título](../src/main/java/com/cineShare/model/Titulo.java) · [Serviço de usuários](../src/main/java/com/cineShare/services/UserService.java) |
| Identidade frágil do filme | O primeiro resultado é escolhido automaticamente, o ID do TMDB é descartado e a identidade local depende do nome. Há risco de confundir homônimos e gravar duplicatas em chamadas concorrentes. Datas fora do formato esperado falham em `LocalDate.parse`. [DTO do TMDB](../src/main/java/DTO/TmdbResultado.java) · [Serviço de títulos](../src/main/java/com/cineShare/services/TituloService.java) |
| Pressão sobre threads e conexões | Cada busca cria um `HttpClient` sem timeouts explícitos e usa `send()` bloqueante. A criação pode fazer três chamadas em série, e a captura genérica não restaura a interrupção da thread. Há risco de saturação de recursos. A análise estática não evidenciou vazamento de memória. [Serviço de títulos](../src/main/java/com/cineShare/services/TituloService.java) · [Serviço de listas](../src/main/java/com/cineShare/services/ListaIndicacaoService.java) · [JDK 17 HttpClient](https://docs.oracle.com/en/java/javase/17/docs/api/java.net.http/java/net/http/HttpClient.html) |
| Concorrência no navegador | Os fluxos capturam falhas de `fetch`. Buscas de filme paralelas ainda podem ultrapassar o limite visual de três, cliques repetidos podem duplicar o envio e respostas fora de ordem podem misturar resultados de busca. [Criação de lista](../src/main/resources/static/lista.html) · [Consulta de listas](../src/main/resources/static/listas.html) |
| Senha versionada e dependência de configuração externa | O arquivo de configuração usa a conta `root` do MySQL com senha literal, também encontrada no histórico inspecionado. `TMDB_TOKEN` é exigido pelo serviço e sua ausência pode impedir a inicialização. [Configuração](../src/main/resources/application.properties) · [Serviço de títulos](../src/main/java/com/cineShare/services/TituloService.java) |

## 9. Código duplicado e débito técnico

- Adicionar um filme já aciona o TMDB e pode gravar o resultado. Ao salvar a lista, a API repete a consulta para cada nome, o que aumenta a latência e o consumo de cota e pode escolher outro resultado. As páginas também repetem configuração de `fetch`, verificação da resposta e feedback genérico. [Criação de lista](../src/main/resources/static/lista.html) · [Cadastro](../src/main/resources/static/index.html) · [Serviço de listas](../src/main/java/com/cineShare/services/ListaIndicacaoService.java)
- Há controller e stubs comentados, métodos sem chamadas como `TituloService.salvar()` e `existsBy…`, além de `tmdb.api-key` sem consumidor. [Controller de credenciais](../src/main/java/com/cineShare/controler/CredentialController.java) · [Repositório de listas](../src/main/java/com/cineShare/repository/ListaIndicacaoRepository.java) · [Repositório de usuários](../src/main/java/com/cineShare/repository/UserRepository.java) · [Serviço de títulos](../src/main/java/com/cineShare/services/TituloService.java) · [Configuração](../src/main/resources/application.properties)
- As listagens usam `findAll()` sem paginação. A listagem de listas inclui a entidade de usuário e a coleção de títulos. Na árvore auditada faltavam testes automatizados, CI e guia versionado de execução do projeto. O `mvnw` estava versionado sem permissão de execução para uso direto em Unix. [Serviço de listas](../src/main/java/com/cineShare/services/ListaIndicacaoService.java) · [Serviço de usuários](../src/main/java/com/cineShare/services/UserService.java) · [Serviço de títulos](../src/main/java/com/cineShare/services/TituloService.java) · [Maven Wrapper](../mvnw)

## 10. Funcionalidades existentes e estado de implementação

Os status abaixo se referem ao código auditado. A execução integrada ficou sem verificação.

| Funcionalidade | Status | Evidência |
|---|---|---|
| Cadastro e reuso de registro por e-mail | Implementado no código | Formulário e persistência, com retorno do registro existente pelo e-mail. [Serviço de usuários](../src/main/java/com/cineShare/services/UserService.java) |
| Listagem e pesquisa de usuários | Implementado na API | Consulta por nome ou ID, sem tela correspondente. [Controller de usuários](../src/main/java/com/cineShare/controler/UserController.java) |
| Pesquisa de filmes e catálogo local | Implementado na API | Consulta TMDB e listagem dos títulos persistidos. [Controller de títulos](../src/main/java/com/cineShare/controler/TituloController.java) |
| Plataforma de streaming | Mockado | O campo recebe sempre `Em desenvolvimento`. [Serviço de títulos](../src/main/java/com/cineShare/services/TituloService.java) |
| Criação de lista com um a três títulos | Implementado no código | Formulário e persistência, com lacunas em autoria e atomicidade. [Serviço de listas](../src/main/java/com/cineShare/services/ListaIndicacaoService.java) |
| Consulta de todas as listas | Implementado no código | Botão **Mostrar todas** e endpoint de listagem. O carregamento inicial está comentado. [Consulta de listas](../src/main/resources/static/listas.html) · [Controller de listas](../src/main/java/com/cineShare/controler/ListaIndicacaoController.java) |
| Filtro por nome | Quebrado | A tela envia `?nome=…` e o servidor retorna a listagem geral. [Consulta de listas](../src/main/resources/static/listas.html) · [Controller de listas](../src/main/java/com/cineShare/controler/ListaIndicacaoController.java) |
| Detalhe de lista | Parcial | A tela usa os títulos presentes na listagem, e o caminho alternativo por ID não possui rota. [Consulta de listas](../src/main/resources/static/listas.html) |
| Login e autorização | Pendente | O controller de credenciais está comentado e o servidor aceita a referência de autoria enviada pelo cliente. [Controller de credenciais](../src/main/java/com/cineShare/controler/CredentialController.java) · [Serviço de listas](../src/main/java/com/cineShare/services/ListaIndicacaoService.java) |
| Edição, exclusão, publicação, arquivamento e comentários | Pendente | O SQL prevê status e comentários. A API de listas tem criação e leitura geral. [SQL V1](../src/main/resources/db/migration/V1__criacao_tabelas.sql) · [Controller de listas](../src/main/java/com/cineShare/controler/ListaIndicacaoController.java) |

## Resumo executivo

**Pontos fortes.** A aplicação tem camadas legíveis, controllers pequenos, injeção por construtor e persistência com Spring Data. A regra de quantidade de filmes também é aplicada no servidor. Os fluxos de interface inspecionados renderizam nomes recebidos da API com `textContent`.

**Débitos prioritários.** A senha versionada, a falta de controle de autoria e a exposição de e-mails exigem atenção antes de disponibilizar a aplicação. O alinhamento do SQL com o JPA é necessário para reproduzir o ambiente. O filtro e o detalhe de listas, a identidade estável do filme e a consulta duplicada ao TMDB concentram o trabalho funcional.

**Próxima etapa.** Priorizar proteção de dados, inicialização reproduzível, contratos da API e integração da interface, de acordo com os critérios de aceite do [plano de ajustes](plano-de-ajustes.md).
