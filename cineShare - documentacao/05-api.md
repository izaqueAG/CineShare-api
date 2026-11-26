# API HTTP do CineShare

A API usa a mesma origem das páginas. Em execução local, a URL base padrão é `http://localhost:8080`. Envie `Content-Type: application/json` nas requisições com corpo. As rotas atuais não exigem cabeçalho de autenticação. [Arquitetura e execução](02-arquitetura.md)

## Endpoints

| Método e rota | Parâmetros | Resposta de sucesso |
| --- | --- | --- |
| `GET /user` | Nenhum. | `200` com um array de usuários. |
| `POST /user` | JSON com `nome` e `email` para o fluxo de cadastro. O controller desserializa diretamente em `User`. | `200` com o usuário cadastrado ou o registro existente para o e-mail fornecido. |
| `GET /users/{buscar}` | `buscar` no caminho. Uma sequência de dígitos é consultada como ID. Outros valores são consultados como trecho do nome, sem distinção entre maiúsculas e minúsculas. | `200` com um array de usuários. Um ID sem registro ou um nome sem correspondências devolve `[]`. |
| `GET /titulos/buscar?nome=Matrix` | `nome` obrigatório na query string. Codifique o valor na URL. | `200` com um título. A consulta ao TMDB pode gravar um novo registro no catálogo. |
| `GET /titulos/mostrar` | Nenhum. | `200` com um array de títulos armazenados localmente. |
| `POST /listas/create` | JSON com `nome`, `userId` de um usuário existente e `titulos`, um array com um a três nomes. | `200` com a lista gravada, incluindo o usuário e os títulos resolvidos. |
| `GET /listas` | Nenhum parâmetro reconhecido. O filtro por nome ou autor é feito pelo frontend. | `200` com um array de listas completas. Retorna `[]` quando o banco está vazio. |

As listagens usam `findAll` sem parâmetros de paginação ou ordenação. A interface abre detalhes com os dados recebidos em `GET /listas`. O backend atual não oferece `GET /listas/{id}` nem rotas de edição, exclusão ou publicação. [Controller de usuários](../src/main/java/com/cineShare/controler/UserController.java) · [Controller de títulos](../src/main/java/com/cineShare/controler/TituloController.java) · [Controller de listas](../src/main/java/com/cineShare/controler/ListaIndicacaoController.java)

## Exemplos de JSON

### Cadastro ou identificação

O cliente envia este corpo em `POST /user`.

```json
{
  "nome": "Ana",
  "email": "ana@example.test"
}
```

`POST /user` devolve um objeto como o exemplo abaixo. `GET /user` e `GET /users/{buscar}` devolvem arrays desses objetos.

```json
{
  "id": 7,
  "nome": "Ana",
  "email": "ana@example.test"
}
```

### Criação de lista

Em `POST /listas/create`, `userId` é o ID local retornado pelo cadastro. Cada item de `titulos` é um nome de filme.

```json
{
  "nome": "Ficção científica",
  "userId": 7,
  "titulos": ["Matrix"]
}
```

Exemplo de resposta de sucesso. `GET /listas` devolve um array dessas listas. A busca de título devolve apenas um objeto com os campos do item de `titulos`, e a rota de catálogo devolve um array de títulos.

```json
{
  "id": 15,
  "nome": "Ficção científica",
  "user": {
    "id": 7,
    "nome": "Ana",
    "email": "ana@example.test"
  },
  "titulos": [
    {
      "id": 42,
      "nome": "Matrix",
      "plataforma": "Em desenvolvimento",
      "anoLancamento": "1999-03-31",
      "tmdbId": 603,
      "capaUrl": null,
      "diretor": null
    }
  ]
}
```

`id` do título identifica a linha local. `tmdbId` identifica o filme no provedor. `anoLancamento` vem como data `AAAA-MM-DD` quando preenchido. Os campos de data, ID externo, capa e diretor podem estar nulos conforme o registro. O e-mail também aparece no objeto `user` de cada lista. [Modelos de dados](04-banco-de-dados.md) · [Regras de importação](03-backend.md#tituloservice)

## Erros

| Situação | Comportamento atual |
| --- | --- |
| `nome` ausente na busca de títulos | O Spring MVC devolve `400`. |
| JSON ausente ou malformado em rota que requer um corpo | O Spring MVC devolve `400`. |
| Rota inexistente | O Spring MVC devolve `404`. |
| Coleção de títulos vazia ou com mais de três entradas, usuário inválido ou erro do TMDB | O servidor lança exceções sem tradução específica para erros de negócio. No ambiente local, a lista vazia foi confirmada como `500`. |

O projeto usa o tratamento de erros padrão do Spring Boot. O formato e o detalhe do corpo podem variar pela negociação de conteúdo e pela configuração do ambiente. O helper do frontend exibe uma mensagem genérica para respostas fora da faixa `2xx`. [Serviço de listas](../src/main/java/com/cineShare/services/ListaIndicacaoService.java) · [Serviço de títulos](../src/main/java/com/cineShare/services/TituloService.java) · [Integração do frontend](06-frontend.md)
