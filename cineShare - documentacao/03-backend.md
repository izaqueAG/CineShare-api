# Backend do CineShare

Os controllers Spring MVC recebem as requisições e delegam aos serviços. Os serviços usam repositórios `JpaRepository` para acessar os dados. Os pacotes ficam em `com.cineShare`, com os DTOs no pacote `DTO`. [Arquitetura](02-arquitetura.md)

## Controllers

| Classe | Responsabilidade |
| --- | --- |
| `UserController` | Cadastra ou recupera usuários pelo e-mail, lista todos e aceita buscas por nome ou ID. |
| `TituloController` | Pesquisa um filme por nome e lista os títulos locais. |
| `ListaIndicacaoController` | Cria listas de indicações e retorna todas as listas com seus autores e títulos. |
| `CredentialController` | Arquivo com implementação comentada. A rota `/credentials` fica inativa. |

[Controllers](../src/main/java/com/cineShare/controler) · [Endpoints e respostas](05-api.md)

## Services e regras de negócio

### `UserService`

- `salvar` procura o e-mail em `UserRepository.findByEmail`. Se houver correspondência, devolve o registro existente com o nome já gravado. Caso contrário, envia a entidade recebida para `save`.
- `findByNameOrId` interpreta um trecho composto apenas de dígitos como `Long` e devolve uma lista com zero ou um usuário. Nos demais casos, busca por trecho de nome ignorando maiúsculas e minúsculas.
- `listAll` delega a `findAll`.

O cadastro recebe a entidade `User` diretamente, com `id` disponível na desserialização. O serviço não valida o formato do e-mail nem verifica sua posse. A sequência de consulta seguida de gravação pode gerar duplicatas em chamadas concorrentes se o banco estiver sem restrição única de e-mail. [Serviço de usuários](../src/main/java/com/cineShare/services/UserService.java) · [Entidade de usuário](../src/main/java/com/cineShare/model/User.java)

### `TituloService`

1. Codifica o nome em UTF-8 e consulta `GET /3/search/movie` no TMDB com `language=pt-BR` e autenticação Bearer via `TMDB_TOKEN`.
2. Usa o primeiro resultado da resposta. Se o status externo for diferente de `200` ou a lista vier vazia, lança uma exceção.
3. Procura no banco pelo `title` devolvido pelo TMDB. Se encontrar registros com aquele nome, reutiliza o primeiro.
4. Cria o título quando necessário, gravando `tmdbId`, nome, data em `LocalDate` quando presente e URL de capa no formato `https://image.tmdb.org/t/p/w500/...` quando há `poster_path`. `plataforma` recebe `Em desenvolvimento` e `diretor` fica sem preenchimento no fluxo de importação.

`findAll` lê apenas o catálogo local. O ID retornado pelo TMDB é armazenado em novos títulos, mas a regra de reuso é por nome e pode confundir filmes homônimos. A busca cria um `HttpClient` por chamada, usa `send()` síncrono e não configura timeouts explícitos. [Serviço de títulos](../src/main/java/com/cineShare/services/TituloService.java) · [DTOs externos](../src/main/java/DTO)

### `ListaIndicacaoService`

1. Exige uma coleção com um a três nomes de títulos.
2. Busca o usuário pelo `userId` do corpo e exige que o registro exista.
3. Resolve cada nome sequencialmente via `TituloService.getTitulo`. Isso repete a consulta externa feita quando o filme foi escolhido no navegador.
4. Associa usuário e títulos à nova lista e chama `listaRepository.save`.

O serviço não valida o nome da lista, não elimina títulos repetidos no corpo e não compara o autor com uma identidade autenticada. A operação não tem uma transação de serviço que cubra a criação inteira. Se uma consulta posterior ou a gravação da lista falhar, títulos já persistidos podem permanecer no catálogo. `listAll` retorna todos os registros sem filtro, paginação ou ordem explícita. [Serviço de listas](../src/main/java/com/cineShare/services/ListaIndicacaoService.java)

## Models, DTOs e repositórios

| Tipo | Componente | Papel |
| --- | --- | --- |
| Entidade | `User` | Guarda `id`, `nome` e `email`. |
| Entidade | `Titulo` | Guarda ID local, ID do TMDB, nome, plataforma, data de lançamento, URL da capa e diretor. |
| Entidade | `ListaIndicacao` | Guarda ID e nome e associa um usuário a uma coleção de títulos. |
| DTO | `ListaIndicacaoDTO` | Recebe `nome`, `userId` e `titulos` como nomes de filmes. |
| DTO | `TmdbResposta`, `TmdbResultado` | Leem a lista `results` e os dados `id`, `title`, `release_date` e `poster_path` do provedor. |
| Repositório | `UserRepository` | Persiste usuários e oferece buscas por e-mail, trecho de nome e ID. |
| Repositório | `TituloRepository` | Persiste títulos e oferece busca por nome, usada na importação. |
| Repositório | `ListaIndicacaoRepository` | Usa as operações herdadas de `JpaRepository` para listas. |

`TituloRepository` também declara `findByAnoLancamento(String)`. O campo correspondente é `LocalDate` e o método está sem uso nos fluxos atuais. A API serializa as entidades diretamente, o que inclui o e-mail do autor no JSON de lista. [Models](../src/main/java/com/cineShare/model) · [DTOs](../src/main/java/DTO) · [Repositórios](../src/main/java/com/cineShare/repository)
