
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

Use o terminal do ambiente em que quer rodar o Spring Boot. No VS Code conectado ao Ubuntu por WSL, o terminal integrado executa os comandos no Linux. Para rodar no Windows, abra um PowerShell do Windows na pasta do projeto.

Você precisa de Java 17 e do MySQL em execução, com o banco `cineshare` criado. Na raiz do projeto, configure o arquivo local `.env`, que é compartilhado pelos dois terminais quando eles usam a mesma pasta e é ignorado pelo Git. Use suas credenciais sem aspas.

```properties
SPRING_DATASOURCE_USERNAME=cineshare_app
SPRING_DATASOURCE_PASSWORD=sua-senha
TMDB_TOKEN=seu-token
TMDB_API_KEY=sua-chave
```

O Spring Boot também aceita essas variáveis diretamente do terminal. É possível sobrescrever a URL completa com `SPRING_DATASOURCE_URL`.

#### Rodar no Windows

No PowerShell, execute o comando abaixo. A aplicação usa `127.0.0.1:3306` por padrão para acessar o MySQL.

```powershell
.\mvnw.cmd spring-boot:run
```

#### Rodar no WSL

Se o MySQL estiver no Windows e o WSL 2 estiver no modo NAT, obtenha o endereço do Windows pela rota padrão do WSL antes de iniciar a aplicação.

```bash
DB_HOST=$(ip -4 route show default | awk 'NR == 1 {print $3}') bash ./mvnw spring-boot:run
```

Se o MySQL também estiver no WSL ou se o WSL estiver em modo espelhado, rode `bash ./mvnw spring-boot:run` para usar `127.0.0.1:3306`.

Para uma instalação nova com NAT, o usuário do MySQL precisa ter uma conta local e outra autorizada para a sub-rede do WSL, ambas com acesso ao banco `cineshare`. Se aparecer o erro MySQL 1130 (`Host ... is not allowed to connect`), confira a sub-rede com `ip -4 route` e atualize a autorização da conta da aplicação para essa faixa. A origem da conexão pode mudar ao reiniciar o WSL. [Contas e hosts no MySQL](https://dev.mysql.com/doc/refman/8.4/en/account-names.html)

#### Acessar o site

Espere a mensagem `Started CineShareApiApplication` antes de abrir o site. `Tomcat initialized with port 8080` aparece antes de o Spring terminar a inicialização e ainda pode ser seguida por um erro de conexão com o banco. Use `Ctrl+C` para parar a instância atual antes de iniciar outra na porta 8080.

| Spring Boot rodando em | Endereço no Windows | Endereço no WSL 2 em modo NAT |
| --- | --- | --- |
| WSL | `http://localhost:8080/` | `http://localhost:8080/` |
| Windows | `http://localhost:8080/` | `http://<IP-do-Windows>:8080/` |

Para consultar do WSL a instância rodando no Windows, use `curl "http://$(ip -4 route show default | awk 'NR == 1 {print $3}'):8080/"`. Em modo espelhado, use `http://127.0.0.1:8080/` em ambos os ambientes. [Rede no WSL](https://learn.microsoft.com/windows/wsl/networking)

Se o site abrir no Windows mas a conexão expirar no WSL, abra um PowerShell como administrador e autorize a porta 8080 na interface virtual do WSL. [Regras do Firewall do Windows](https://learn.microsoft.com/powershell/module/netsecurity/new-netfirewallrule)

```powershell
$wslInterface = (Get-NetAdapter -Name 'vEthernet (WSL*' -ErrorAction Stop).Name
New-NetFirewallRule -Name 'CineShare-WSL-TCP-8080' -DisplayName 'CineShare 8080 (WSL)' `
  -Direction Inbound -Action Allow -Protocol TCP -LocalPort 8080 `
  -InterfaceAlias $wslInterface -RemoteAddress LocalSubnet
```

Com o Spring rodando no Windows, também é possível usar `curl.exe http://localhost:8080/` a partir do terminal do WSL. O executável usa a rede do Windows.

Para gerar o pacote executável, use `bash ./mvnw clean package` no Linux/macOS ou `.\mvnw.cmd clean package` no PowerShell. O arquivo gerado fica em `target/`.

## Status

Projeto em desenvolvimento.
