# Plano de ajustes do CineShare API

Este roteiro parte do [diagnóstico de 23 de novembro de 2026](diagnostico-atual.md). A ordem prioriza a proteção de dados, a criação reproduzível do banco, a correção dos contratos e a validação dos fluxos na interface. A execução dos ajustes é uma etapa posterior à entrega destes documentos.

## 1. Proteger credenciais, autoria e dados pessoais

**Achados de origem.** A configuração contém uma senha literal do MySQL. A API aceita a identidade do autor pelo `userId` do corpo da requisição e expõe e-mails ao serializar entidades. O cadastro pode devolver um usuário existente só pela correspondência do e-mail. [Rotas e proteção](diagnostico-atual.md#6-rotas-e-navegação) · [Riscos de entrada e configuração](diagnostico-atual.md#8-problemas-de-tipagem-e-possíveis-erros)

**Ajustes**

- Ler as credenciais do MySQL e do TMDB a partir da configuração externa. Usar uma conta de aplicação com privilégios restritos e rotacionar a senha versionada caso ainda esteja em uso. Documentar as variáveis com valores fictícios.
- Adotar autenticação para as operações protegidas e obter o usuário autor da identidade autenticada no servidor. Separar cadastro de autenticação e remover a dependência de `userId` do cliente para atribuir autoria.
- Usar DTOs para cadastro e respostas públicas. Restringir os campos públicos de usuário ao necessário e garantir que a criação de uma conta não aceite um ID definido pelo cliente.

**Dependências.** O ambiente precisa fornecer credenciais externas. Se a autenticação exigir novos dados persistidos, registrar essa necessidade no esquema da etapa 2 e validar a implantação conjunta.

**Critérios de aceite.** A aplicação usa uma conta de banco restrita. O repositório não contém credenciais ativas na configuração atual. Um cliente sem autenticação não cria listas. Um usuário autenticado só cria listas em seu próprio nome, inclusive quando o corpo da requisição contém um ID diferente. As respostas públicas de usuários e listas omitem e-mail. Reutilizar um e-mail no cadastro não autentica o solicitante.

## 2. Alinhar banco e configuração

**Achados de origem.** O V1 cria tabelas e restrições divergentes do JPA, `ddl-auto=update` é o mecanismo atual de manutenção do esquema e há atualizações disponíveis na linha Spring Boot 4.0. [Arquitetura e esquema](diagnostico-atual.md#1-arquitetura-e-organização-de-pastas) · [Dependências e versões](diagnostico-atual.md#2-tecnologias-dependências-e-versões)

**Ajustes**

- Inventariar os esquemas existentes antes de escolher a transição. Alinhar entidades e migrações para usuários, listas, títulos e suas associações. Aplicar migrações versionadas no MySQL e usar validação de esquema no Hibernate. Se o V1 já estiver registrado como aplicado em algum ambiente, preservar seu conteúdo e reconciliar o esquema em uma migração posterior.
- Garantir a unicidade de e-mail no banco. Verificar se há duplicatas em dados existentes e documentar a resolução antes de habilitar a restrição.
- Atualizar o gerenciador de dependências para o patch aplicável da linha Spring Boot 4.0. A referência da auditoria é 4.0.10. Conferir as versões resolvidas e remover declarações de starters já cobertas transitivamente.

**Dependências.** Usar as configurações externas da etapa 1 e um MySQL descartável para validar a criação do zero. Para ambientes com dados, preparar um backup e testar a migração em cópia representativa antes de aplicá-la.

**Critérios de aceite.** As migrações são executadas em um banco vazio e o aplicativo inicia com validação do esquema. Reiniciar a aplicação preserva os dados e não exige DDL manual. A transição em uma cópia do banco existente preserva usuários e associações. A restrição de e-mail é exercitada em um teste concorrente. A versão do Spring Boot e a árvore de dependências resolvidas são registradas junto à verificação do build.

## 3. Completar os contratos da API

**Achados de origem.** A busca de listas ignora `nome`, o detalhe por ID está ausente e a listagem retorna todas as entidades. Buscar filmes pode gravar no banco, e criar lista repete a consulta ao TMDB por nome. Erros de entrada, ausência de recurso e falhas externas não têm tratamento distinto. [Componentização](diagnostico-atual.md#4-componentização-e-reutilização) · [Rotas atuais](diagnostico-atual.md#6-rotas-e-navegação) · [Riscos de execução](diagnostico-atual.md#8-problemas-de-tipagem-e-possíveis-erros) · [Chamadas duplicadas](diagnostico-atual.md#9-código-duplicado-e-débito-técnico)

**Ajustes**

- Implementar `GET /listas` com filtro opcional por trecho de `nome` e paginação. Retornar resumos com os dados necessários à listagem. Implementar `GET /listas/{id}` para retornar proprietário público e títulos, com `404` para ID inexistente.
- Validar os corpos de cadastro e criação. Exigir nome válido e de um a três títulos na lista. Padronizar respostas de erro para entrada inválida, recurso inexistente e indisponibilidade do TMDB.
- Tornar a pesquisa de filmes uma operação de leitura. Permitir que a interface selecione o resultado e envie sua identidade estável do TMDB. Usar um cliente HTTP reutilizável com timeouts. Resolver os filmes já conhecidos no banco e persistir os títulos necessários e a lista na mesma transação, após as consultas externas necessárias.

**Dependências.** Usar os DTOs públicos e o contexto autenticado da etapa 1. A etapa 2 fornece as restrições do banco e o caminho de migração para persistir o ID externo. Coordenar as mudanças nos formatos de listagem e seleção de filmes com os consumidores da etapa 4.

**Critérios de aceite.** A listagem geral e a filtrada paginam os resultados, e o detalhe distingue IDs existentes e ausentes. A pesquisa não grava títulos. Dois filmes com o mesmo nome e IDs externos distintos podem ser selecionados separadamente. Criar uma lista com títulos já conhecidos não repete a pesquisa externa. Uma falha durante a criação não deixa gravações parciais. Os testes cobrem entrada inválida, limites de um e três títulos, falhas e timeout do TMDB e a serialização das respostas sem dados privados.

## 4. Ajustar a interface e documentar a execução

**Achados de origem.** Dois fluxos fixam `localhost`, a consulta não tem link nas páginas anteriores e requisições simultâneas podem misturar resultados ou duplicar envios. Há diferenças de responsividade e limitações de navegação por teclado. [Navegação atual](diagnostico-atual.md#6-rotas-e-navegação) · [Interface e responsividade](diagnostico-atual.md#7-responsividade-e-organização-de-estilos) · [Concorrência no navegador](diagnostico-atual.md#8-problemas-de-tipagem-e-possíveis-erros) · [Fluxos incompletos](diagnostico-atual.md#10-funcionalidades-existentes-e-estado-de-implementação)

**Ajustes**

- Usar URLs relativas em todas as páginas e integrar a navegação até a consulta. Atualizar as páginas para o fluxo de autenticação, o contrato paginado da busca, o detalhe por ID e a seleção de filmes pelo ID do TMDB.
- Impedir envios duplicados enquanto houver uma gravação em andamento. Exibir carregamento e erros, descartar respostas de buscas já substituídas e manter a seleção no máximo de três títulos durante buscas simultâneas.
- Remover as cercas de Markdown do cadastro, declarar `viewport` nas três páginas e compartilhar estilos de formulário e feedback. Usar rótulos, controles operáveis por teclado e anúncio de mensagens dinâmicas.
- Documentar os pré-requisitos, as variáveis de ambiente, a inicialização do banco e os comandos de execução e teste a partir de um checkout limpo.

**Dependências.** A entrega conjunta com os contratos da etapa 3 mantém as telas sincronizadas com a API. A execução documentada precisa das etapas 1 e 2.

**Critérios de aceite.** O fluxo de cadastro, autenticação, criação e consulta funciona em uma origem com host ou porta diferente do padrão. A pessoa consegue chegar à consulta, filtrar e abrir detalhes usando teclado e em tela estreita. Cliques repetidos durante um envio não geram uma segunda solicitação, e a busca apresenta apenas os resultados da consulta mais recente. Outra pessoa consegue iniciar o projeto e executar os testes seguindo o guia.
