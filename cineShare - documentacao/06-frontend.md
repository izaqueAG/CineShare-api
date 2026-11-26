# Frontend do CineShare

A interface está em `src/main/resources/static`. Os três HTMLs definem idioma `pt-BR`, título, codificação UTF-8 e viewport. Eles carregam a mesma folha `cineshare.css` e o script `cineshare.js`, responsável pela marcação do conteúdo, pelos eventos e pela integração com a API. [Pasta de recursos estáticos](../src/main/resources/static)

## Páginas e navegação

| Caminho | Tela | Chamada à API |
| --- | --- | --- |
| `/` ou `/index.html` | Exibe o formulário de identificação quando falta `userId` na aba. Com o ID, mostra a Home, atalhos, prateleira e estojo de filme. | `POST /user` na identificação e `GET /listas` na Home. |
| `/listas.html` | Carrega a prateleira de listas. O botão **Buscar** aplica um filtro local por trecho do nome da lista ou do autor, ignorando maiúsculas e minúsculas. Clicar numa lista mostra seus filmes. | `GET /listas` ao entrar na página. O filtro e o detalhe reaproveitam a resposta. |
| `/lista.html` | Monta uma lista. `Enter` no campo de título consulta o filme, que pode ser visto no estojo e incluído na seleção. É possível remover itens antes de salvar. | `GET /titulos/buscar?nome=…` e `POST /listas/create`. |

O cabeçalho inclui links para Home, busca de listas e indicação de filmes, além do seletor de iluminação e do botão **Sair**. As páginas de consulta e criação abrem por URL direta. O ID local é exigido ao clicar em **Salvar Lista**. [Home e identificação](../src/main/resources/static/index.html) · [Busca de listas](../src/main/resources/static/listas.html) · [Criação](../src/main/resources/static/lista.html)

## CSS e componentes visuais

- A folha compartilhada usa variáveis de cor, tipografia Arial e Georgia, Grid para as seções principais e Flexbox para navegação, ações e prateleira.
- As lâmpadas usam pseudoelementos para representar bulbos e feixes. A classe `active` indica a página atual e `body.light` altera a paleta do tema.
- O estojo Blu-ray tem estados aberto e fechado. O aberto exibe um disco com imagem de capa ou arte de fallback e uma ficha. A direção e a data mostram `Não informado` quando ausentes.
- As regras para larguras de até `800px` empilham as colunas e ajustam cabeçalho e estojo. A prateleira usa rolagem horizontal para os destaques.

[Folha de estilos](../src/main/resources/static/cineshare.css)

## JavaScript, dados e estado

| Código ou armazenamento | Responsabilidade |
| --- | --- |
| `shell()` | Insere a estrutura comum de cabeçalho, área de conteúdo e rodapé. Configura iluminação e saída. |
| `api()` | Usa `fetch` com caminhos na mesma origem. Converte respostas de sucesso em JSON e apresenta mensagem genérica em respostas HTTP de erro. |
| `caseView()` e `bindCase()` | Montam o estojo com os dados do filme e conectam o botão de fechamento. |
| `homePage()` | Combina títulos de todas as listas, elimina duplicatas pelo ID ou nome e exibe até cinco. Em caso de erro ou ausência de títulos, usa cinco itens de demonstração com imagens do Unsplash. |
| `listsPage()`, `renderLists()` e `showList()` | Guardam a listagem em memória, aplicam o filtro e renderizam o detalhe a partir dos objetos já carregados. |
| `createPage()` | Mantém a seleção num array em memória, confere o limite visual, evita repetir filme pelo ID ou nome e envia `nome`, `userId` e os nomes dos filmes. |
| `sessionStorage` | Guarda `userId` e `userName` para a aba e é limpo ao clicar em **Sair**. |
| `localStorage` | Guarda o tema na chave `cineshare-theme` e mantém a preferência após recarregar ou sair. |

Após salvar, a interface monta um resumo com o nome da lista, a seleção local e a URL de `listas.html`. O botão **Compartilhar Indicações** tenta copiar esse texto pela API de clipboard do navegador. O endereço abre a listagem geral. [JavaScript compartilhado](../src/main/resources/static/cineshare.js) · [Contratos da API](05-api.md)

## Diagnóstico da interface

- Ao refazer o filtro, a tela atualiza os resultados e mantém o detalhe aberto anteriormente, mesmo se a lista ficar fora do novo filtro.
- O resumo de compartilhamento contém sequências `\n` literais no lugar de quebras de linha. A tentativa de copiar depende da disponibilidade da API de clipboard e não oferece confirmação na tela.
- O limite local é conferido antes da resposta da busca. Buscas simultâneas podem ultrapassar três itens na seleção, e cliques seguidos em **Salvar Lista** podem enviar requisições duplicadas. O servidor aplica seu próprio limite.
- O script renderiza marcação via `innerHTML` e usa `esc()` para interpolar valores. Os nomes aparecem em contexto HTML e as URLs de capa também entram em estilos inline, o que exige cuidados de renderização específicos de cada contexto.

Os fluxos principais, o fallback e o empilhamento em viewport estreito foram exercitados no Chromium com respostas HTTP simuladas. A integração com MySQL e TMDB reais, a experiência em dispositivos físicos e a cópia efetiva para a área de transferência ficaram fora dessa verificação. [JavaScript compartilhado](../src/main/resources/static/cineshare.js) · [Diagnóstico atualizado](../planes/diagnostico-atual.md)
