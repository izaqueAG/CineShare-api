# Diagnóstico atual do CineShare API

Revisão de 26 de novembro de 2026, baseada no código atual da API, da interface, dos testes e da configuração. O CineShare já permite identificação por e-mail, pesquisa de filmes no TMDB, criação de listas e consulta pela interface. Os principais pontos em aberto são a proteção da autoria e dos dados pessoais, a divergência entre o SQL e o modelo JPA e a repetição de consultas externas na gravação da lista. [Documentação técnica](../cineShare%20-%20documentacao/01-visao-geral.md)

## 1. Arquitetura e organização de pastas

O Spring Boot serve os arquivos estáticos e a API a partir do mesmo módulo Maven. Controllers delegam aos serviços, que usam repositórios Spring Data JPA para acessar o banco. O serviço de títulos também consulta o TMDB por HTTP síncrono. Os tipos de entrada de lista e de resposta do TMDB ficam no pacote `DTO`, fora de `com.cineShare`. [Arquitetura, pastas e execução local](../cineShare%20-%20documentacao/02-arquitetura.md)

O Hibernate mantém as tabelas via `ddl-auto=update`. O V1 usa nomes e regras de outro desenho de banco e fica fora da inicialização padrão. A tabela compartilhada `usuarios` merece atenção em bancos existentes. Uma coluna `data_cadastro` obrigatória e sem valor padrão pode impedir cadastros, porque a entidade não grava essa coluna. [Tabelas e relacionamento](../cineShare%20-%20documentacao/04-banco-de-dados.md)

## 2. Tecnologias, dependências e versões

O POM declara Java 17 e Spring Boot 4.0.8, com Spring MVC, Spring Data JPA e MySQL Connector/J. O Wrapper fixa Maven 3.9.16. A interface usa HTML, CSS e JavaScript nativo. Há 40 testes distribuídos em oito classes, com Mockito nos serviços, MockMvc nos controllers e H2 nos testes de repositório. O README traz instruções de execução para PowerShell, Linux, macOS e WSL. [Tecnologias](../cineShare%20-%20documentacao/01-visao-geral.md#tecnologias) · [Testes](../src/test/java/com/cineShare) · [README](../README.md)

## 3. Padrões de desenvolvimento

A aplicação tem controllers pequenos, injeção por construtor e repositórios por entidade. Os serviços concentram as regras. Controle de entrada e saída ainda é irregular. O cadastro recebe uma entidade JPA diretamente, a criação de listas recebe um DTO e os controllers devolvem as entidades. O tratamento de erros fica com o Spring MVC e o Spring Boot. [Backend](../cineShare%20-%20documentacao/03-backend.md) · [Contratos e erros](../cineShare%20-%20documentacao/05-api.md)

## 4. Componentização e reutilização

Os três HTMLs compartilham o cabeçalho, o estojo, os estilos e um helper de HTTP implementados em `cineshare.js` e `cineshare.css`. O script monta a interface e mantém as funções das páginas no mesmo arquivo. No servidor, `TituloService` concentra a chamada ao TMDB, a seleção de resultado, a regra de reuso e a gravação. Separar a pesquisa externa da persistência ajudaria a controlar os efeitos da busca e a testar falhas do provedor. [Frontend](../cineShare%20-%20documentacao/06-frontend.md) · [Serviço de títulos](../src/main/java/com/cineShare/services/TituloService.java)

## 5. Gerenciamento de estado e comunicação

O navegador guarda o ID e o nome em `sessionStorage`, o tema em `localStorage` e as listas carregadas ou a seleção de filmes em memória. Sair limpa a identificação da aba. A lista é gravada depois de resolver os nomes e possivelmente inserir títulos. Sem uma transação de serviço que cubra o fluxo inteiro, uma falha posterior pode deixar os títulos já inseridos no catálogo. [Estado no navegador](../cineShare%20-%20documentacao/06-frontend.md#javascript-dados-e-estado) · [Criação de lista](../cineShare%20-%20documentacao/03-backend.md#listaindicacaoservice)

## 6. Rotas e navegação

A interface tem navegação entre Home, consulta e criação e usa requisições à mesma origem. A consulta carrega `GET /listas` ao abrir a página, filtra por nome da lista ou do autor no navegador e abre os detalhes a partir do JSON já recebido. O servidor ainda lista todos os registros, sem paginação, e carece de rota de detalhe por ID. [Endpoints](../cineShare%20-%20documentacao/05-api.md) · [Páginas](../cineShare%20-%20documentacao/06-frontend.md#páginas-e-navegação)

A identificação por e-mail entrega o registro existente e não verifica a posse do endereço. A API aceita a autoria a partir do `userId` informado pelo cliente e inclui e-mails nas consultas de usuários e listas. Esses comportamentos afetam a privacidade e a atribuição das indicações em uso compartilhado. [Serviço de usuários](../src/main/java/com/cineShare/services/UserService.java) · [Serviço de listas](../src/main/java/com/cineShare/services/ListaIndicacaoService.java)

## 7. Responsividade e organização de estilos

Os três HTMLs usam viewport e a folha compartilhada aplica Grid e Flexbox. Até `800px`, as colunas do conteúdo e do estojo passam a uma coluna. A Home também oferece prateleira com rolagem horizontal, tema claro e escuro e arte de fallback para filmes sem capa. O empilhamento da Home foi observado em viewport de 390 px no Chromium. O uso em dispositivos físicos e a revisão completa de acessibilidade ainda estão pendentes. [CSS](../src/main/resources/static/cineshare.css) · [Frontend](../cineShare%20-%20documentacao/06-frontend.md)

## 8. Problemas de tipagem e possíveis erros

| Achado | Consequência |
| --- | --- |
| A configuração versionada usa a conta `root` do MySQL com senha literal. | As credenciais dependem de substituição no ambiente para evitar o uso do segredo e da conta privilegiada do arquivo. [Configuração](../src/main/resources/application.properties) |
| O cadastro recebe `User` diretamente e procura o e-mail antes de gravar. | O cliente pode preencher o ID da entidade. Sem unicidade no esquema ativo, gravações concorrentes podem duplicar e-mails. [Controller](../src/main/java/com/cineShare/controler/UserController.java) · [Modelo](../src/main/java/com/cineShare/model/User.java) |
| A importação escolhe o primeiro resultado e compara filmes por nome. | Filmes homônimos podem apontar para o mesmo registro local. Novos registros guardam `tmdbId`, mas a busca de reuso ainda independe desse identificador. [Serviço de títulos](../src/main/java/com/cineShare/services/TituloService.java) |
| A criação valida a quantidade e a existência do usuário, com exceções genéricas. | Erros de entrada previsíveis podem virar `500`. Esse status foi observado ao enviar uma coleção vazia. [API](../cineShare%20-%20documentacao/05-api.md#erros) |
| `findByAnoLancamento(String)` está sem uso e recebe um tipo diferente do atributo `LocalDate`. A busca numérica de usuários converte a entrada com `Long.parseLong`. | O método de título tem um contrato incoerente com o campo. Uma sequência numérica fora da faixa de `Long` pode falhar antes da consulta de usuário. [Repositório de títulos](../src/main/java/com/cineShare/repository/TituloRepository.java) · [Serviço de usuários](../src/main/java/com/cineShare/services/UserService.java) |
| A interface conserva o detalhe aberto após filtrar e exibe sequências `\n` literais no resumo de compartilhamento. | O detalhe pode ficar desconectado da seleção visível e o texto copiado pode carregar formatação incorreta. [Diagnóstico da interface](../cineShare%20-%20documentacao/06-frontend.md#diagnóstico-da-interface) |

## 9. Código duplicado e débito técnico

- A pesquisa por filme pode gravar um título. Salvar a lista volta a pesquisar cada nome, com até três chamadas externas em série. O cliente HTTP é construído por consulta e não configura timeout explícito. [Serviço de títulos](../src/main/java/com/cineShare/services/TituloService.java) · [Serviço de listas](../src/main/java/com/cineShare/services/ListaIndicacaoService.java)
- A listagem completa inclui títulos e autores. O volume de JSON cresce com a base e o carregamento das associações pode gerar consultas adicionais. O custo ainda precisa ser medido no MySQL. [API](../cineShare%20-%20documentacao/05-api.md) · [Modelo de lista](../src/main/java/com/cineShare/model/ListaIndicacao.java)
- A interface verifica o limite de filmes antes de aguardar cada busca e permite cliques repetidos no botão de gravação. Requisições em paralelo podem exceder o limite visual ou enviar a lista mais de uma vez. [Frontend](../cineShare%20-%20documentacao/06-frontend.md#diagnóstico-da-interface)

## 10. Funcionalidades existentes e estado de implementação

| Funcionalidade | Estado atual |
| --- | --- |
| Cadastro e identificação | O backend cria ou recupera o registro pelo e-mail. A interface mantém a identificação da aba. |
| Home, iluminação e estojo | Implementados. A Home usa títulos das listas ou demonstrações e o estojo mostra dados disponíveis de cada título. |
| Busca de filmes e catálogo local | Implementados. A consulta externa cobre filmes e o campo de plataforma tem texto fixo. |
| Criação e consulta de listas | Implementadas. O servidor exige de um a três nomes e um usuário existente. A interface filtra localmente e lê os detalhes da listagem. |
| Compartilhamento | A interface gera um resumo e oferece a cópia. A URL é geral e a formatação do texto tem uma falha confirmada. |
| Autenticação, autorização, edição, exclusão e publicação por estado | Pendentes. |

[Funcionalidades e tecnologias](../cineShare%20-%20documentacao/01-visao-geral.md) · [API](../cineShare%20-%20documentacao/05-api.md) · [Frontend](../cineShare%20-%20documentacao/06-frontend.md)

## Verificação executada

- `bash ./mvnw test` compilou o projeto e executou 40 testes, todos aprovados.
- `node --check src/main/resources/static/cineshare.js` aprovou a sintaxe do script compartilhado.
- O Spring Boot foi iniciado localmente com H2 temporário. Foram verificados as três páginas, cadastro e reuso por e-mail, busca e listagem de usuários, catálogo de títulos, listagem vazia de listas e respostas HTTP `400`, `404` e `500` nos cenários descritos na API.
- O Chromium, com respostas HTTP simuladas, exercitou identificação, Home, tema, estojo, filtro e detalhe de listas, envio de lista, fallback em viewport estreito e saída. Foram confirmados o detalhe residual e as quebras de linha literais no resumo.
- A execução com MySQL e TMDB reais e a cópia efetiva para a área de transferência ficaram fora da verificação.

## Resumo executivo

Para disponibilizar o protótipo a mais usuários, a prioridade é controlar autoria e exposição de e-mails e retirar o segredo do arquivo versionado. Em seguida, alinhar o esquema JPA a uma migração verificável no MySQL e usar a identidade do título já selecionado para eliminar a nova pesquisa por nome ao gravar. Na interface, os ajustes imediatos são limpar o detalhe ao filtrar, corrigir o texto de compartilhamento e controlar envios simultâneos. [Plano de ajustes existente](plano-de-ajustes.md) · [Documentação técnica](../cineShare%20-%20documentacao/01-visao-geral.md)
