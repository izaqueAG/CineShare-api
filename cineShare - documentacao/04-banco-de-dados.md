# Banco de dados do CineShare

A aplicação usa MySQL por padrão, com o esquema `cineshare`. As entidades JPA definem o modelo utilizado pelas consultas. O Hibernate está configurado com `spring.jpa.hibernate.ddl-auto=update` e `spring.jpa.show-sql=true`. A conexão precisa encontrar o esquema antes de o Hibernate criar ou atualizar tabelas. [Configuração](../src/main/resources/application.properties)

## Tabelas do modelo JPA

Os nomes abaixo seguem os mapeamentos e a convenção de nomes aplicada pelo Spring Boot. A geração dessas tabelas e chaves estrangeiras foi observada no H2 dos testes. Os tipos efetivos e restrições já instaladas em um MySQL existente dependem de seu esquema.

| Tabela | Origem | Colunas e restrições do mapeamento |
| --- | --- | --- |
| `usuarios` | `User` | `id` é a chave primária do tipo `BIGINT` com geração por identidade. `nome` é obrigatório. `email` é texto, sem restrição de unicidade ou de nulabilidade na entidade. |
| `titulo` | `Titulo` | `id` é a chave primária do tipo `BIGINT` com geração por identidade. `nome`, `plataforma`, `capa_url` e `diretor` são textos. `ano_lancamento` é `DATE` e `tmdb_id` é `BIGINT`. O mapeamento permite valores nulos nesses campos e não define unicidade para `tmdb_id`. |
| `lista_indicacao` | `ListaIndicacao` | `id` é a chave primária do tipo `BIGINT` com geração por identidade. `nome` é texto e admite valor nulo. `user_id` é uma chave estrangeira obrigatória para `usuarios.id`. |
| `lista_titulos` | `@JoinTable` de `ListaIndicacao.titulos` | `lista_id` referencia `lista_indicacao.id` e `titulos_id` referencia `titulo.id`. O mapeamento não declara coluna de ordem ou restrição de unicidade para o par. |

[Entidade de usuário](../src/main/java/com/cineShare/model/User.java) · [Entidade de título](../src/main/java/com/cineShare/model/Titulo.java) · [Entidade de lista](../src/main/java/com/cineShare/model/ListaIndicacao.java)

## Relacionamentos e consistência

- **Usuário e lista.** Um usuário pode ser autor de várias listas. Cada lista tem um único usuário associado por `@ManyToOne` e `user_id`.
- **Lista e título.** Uma lista reúne vários títulos e um mesmo título pode ser reutilizado por várias listas. A associação `@ManyToMany` usa `lista_titulos`. O limite de um a três títulos é aplicado no serviço e a ordem de leitura após persistência não está definida no mapeamento.
- **Gravação.** Títulos novos podem ser gravados durante a pesquisa e durante a resolução dos nomes. A lista e suas associações são gravadas depois. O fluxo completo não está envolvido em uma única transação de serviço.

[Entidade de lista](../src/main/java/com/cineShare/model/ListaIndicacao.java) · [Serviço de listas](../src/main/java/com/cineShare/services/ListaIndicacaoService.java)

## Script SQL V1

O arquivo `V1__criacao_tabelas.sql` descreve outro desenho do banco. Ele usa a estrutura de diretórios e a convenção de nomes do Flyway, mas o POM não declara esse mecanismo. A inicialização padrão usa Hibernate. [SQL V1](../src/main/resources/db/migration/V1__criacao_tabelas.sql) · [POM](../pom.xml)

| Tabela no V1 | Diferença em relação ao modelo usado pelo Java |
| --- | --- |
| `usuarios` | Exige `data_cadastro`, faz `email` obrigatório e único e define `nome` como `VARCHAR(150)`. A entidade `User` não grava `data_cadastro`, o que pode impedir novos cadastros em um banco que tenha essa coluna obrigatória sem valor padrão. |
| `listas` | Usa `usuario_id`, `titulo`, descrição, datas e status `RASCUNHO`, `PUBLICADA` ou `ARQUIVADA`. A entidade usa `lista_indicacao`, `user_id` e `nome`. |
| `filmes` | Vincula um filme a uma única lista por `lista_id`, usa ano inteiro e exige comentário de pelo menos dez caracteres. O modelo JPA usa a associação reutilizável `lista_titulos`, armazena a data completa e não implementa esse comentário. |

Aplicar o V1 isoladamente não reproduz o esquema necessário à aplicação. A auditoria verificou o modelo em H2. A criação do zero e a compatibilidade com bases existentes seguem pendentes de validação em MySQL. [Arquitetura e execução](02-arquitetura.md) · [Diagnóstico atualizado](../planes/diagnostico-atual.md)
