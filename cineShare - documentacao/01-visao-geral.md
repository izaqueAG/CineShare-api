# Visão geral do CineShare

Documentação revisada em 26 de novembro de 2026. O CineShare é um protótipo web para organizar e compartilhar indicações de filmes. A interface usa a estética de uma locadora retrô e permite explorar listas de outros usuários e montar uma lista própria com até três filmes.

## Funcionalidades

| Fluxo | Comportamento atual |
| --- | --- |
| Identificação | O formulário coleta nome e e-mail. A API cria um usuário ou recupera o registro com o mesmo e-mail. O navegador guarda o ID e o nome na aba. |
| Home | Exibe saudação, atalhos e até cinco filmes das listas existentes. Exibe títulos de demonstração quando os destaques estão vazios ou a requisição falha. |
| Exploração | Carrega as listas e permite filtrar pelo nome da lista ou do autor. Cada lista abre seus filmes na própria página, com capa, direção e data em um estojo visual. |
| Criação | Pesquisa filmes no TMDB e permite selecionar de um a três. O servidor grava a lista com o usuário informado e os títulos resolvidos. |
| Compartilhamento | Depois de salvar, a tela oferece a cópia de um resumo com o nome, os filmes e o endereço da página geral de listas. |
| Consultas auxiliares | A API lista usuários, busca por nome ou ID e lista o catálogo local de títulos. |

A busca externa cobre filmes. Séries, plataformas de streaming, edição e exclusão de listas, publicação por status e comentários de usuário estão fora do comportamento implementado. A identificação por e-mail e a saída no navegador são operações locais de acesso à interface. A API opera sem autenticação e sem autorização de autoria. [Regras do backend](03-backend.md) · [Páginas e navegação](06-frontend.md)

## Tecnologias

| Área | Tecnologias do projeto |
| --- | --- |
| Plataforma | Java 17 e Spring Boot 4.0.8. |
| Web | Spring MVC, Jackson e Tomcat embutido. |
| Persistência | Spring Data JPA, Hibernate, MySQL e MySQL Connector/J. |
| Interface | HTML5, CSS e JavaScript nativo com DOM, Fetch e Web Storage. |
| Catálogo externo | TMDB API v3 para pesquisa de filmes e CDN do TMDB para capas. |
| Build e testes | Maven 3.9.16 via Wrapper 3.3.4, Lombok, JUnit, Mockito, MockMvc e H2 em memória nos testes de repositório. |

[POM](../pom.xml) · [Configuração do Maven Wrapper](../.mvn/wrapper/maven-wrapper.properties)

## Diagnóstico

A mudança visual centralizou os recursos das três páginas, acrescentou navegação entre elas e tornou o filtro funcional no navegador. A limitação mais importante para uso compartilhado está na autoria e na privacidade. O servidor confia no `userId` informado pelo cliente e inclui e-mail nas respostas com usuários. Também permanecem a divergência entre o SQL versionado e o esquema JPA, a busca por nome como identidade do filme e a dependência de novas chamadas ao TMDB durante a gravação da lista. [Diagnóstico atualizado](../planes/diagnostico-atual.md)

## Guia da documentação

- [02 · Arquitetura e execução local](02-arquitetura.md)
- [03 · Backend e regras de negócio](03-backend.md)
- [04 · Banco de dados e relacionamentos](04-banco-de-dados.md)
- [05 · API e exemplos de contratos](05-api.md)
- [06 · Frontend e integração com a API](06-frontend.md)
