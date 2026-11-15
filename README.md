# Sistema SAEP

Sistema web para cadastro de produtos, controle de estoque e histórico de movimentações. Desenvolvido com Java 21, Spring Boot, Spring Data JPA, Thymeleaf e MySQL.

## Requisitos

- JDK 21 ou superior.
- MySQL 8 ou compatível.
- Maven Wrapper incluído no projeto.

## Configuração do banco de dados

Crie o banco:

```sql
CREATE DATABASE saep_db;
```

A aplicação utiliza as variáveis de ambiente abaixo. Os valores entre parênteses são os padrões locais:

| Variável | Finalidade | Padrão |
|---|---|---|
| `DB_URL` | URL JDBC do MySQL | `jdbc:mysql://localhost:3306/saep_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true` |
| `DB_USERNAME` | Usuário do MySQL | `root` |
| `DB_PASSWORD` | Senha do MySQL | vazia |
| `DB_DRIVER` | Driver JDBC | `com.mysql.cj.jdbc.Driver` |
| `DDL_AUTO` | Estratégia de atualização do schema | `update` |
| `SHOW_SQL` | Exibir SQL no console | `false` |

Não versione credenciais reais. Configure as variáveis no ambiente em que a aplicação será executada.

## Executar localmente

No terminal, entre na pasta do projeto:

```powershell
cd SAEP-PROJETO-master
```

No Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

No Linux ou macOS:

```bash
./mvnw spring-boot:run
```

Acesse [http://localhost:8080](http://localhost:8080). O primeiro usuário precisa existir na tabela `usuarios`; esta versão do projeto ainda não possui tela pública de cadastro de usuários.

Depois da primeira inicialização (quando o Hibernate criar as tabelas), gere um hash BCrypt com a classe `BCryptPasswordEncoder` da dependência do projeto:

```java
var encoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
System.out.println(encoder.encode("SUA_SENHA"));
```

Use o hash gerado para criar a primeira conta administradora:

```sql
INSERT INTO usuarios (login, senha, perfil)
VALUES ('admin', 'COLE_AQUI_O_HASH_BCRYPT', 'ADMINISTRADOR');
```

Troque o login e a senha de exemplo antes de usar o sistema.

## Inserts iniciais de usuários e produtos

Depois que a aplicação criar as tabelas, você pode inserir dados de demonstração no banco. Os exemplos abaixo criam três usuários (um administrador e dois operadores) e três produtos. Rode os comandos uma única vez em um banco vazio ou altere os logins e nomes antes de executá-los novamente.

Gere um hash BCrypt diferente para cada usuário. Substitua as senhas de exemplo por senhas fortes escolhidas por você e execute:

```java
var encoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
System.out.println("admin-demo: " + encoder.encode("SENHA_FORTE_DO_ADMIN"));
System.out.println("operador-demo-1: " + encoder.encode("SENHA_FORTE_DO_OPERADOR_1"));
System.out.println("operador-demo-2: " + encoder.encode("SENHA_FORTE_DO_OPERADOR_2"));
```

Copie cada hash impresso para o usuário correspondente no comando SQL. Não grave as senhas originais na coluna `senha`.

```sql
INSERT INTO usuarios (login, senha, perfil) VALUES
('admin-demo', 'COLE_AQUI_O_HASH_BCRYPT_DO_ADMIN', 'ADMINISTRADOR'),
('operador-demo-1', 'COLE_AQUI_O_HASH_BCRYPT_DO_OPERADOR_1', 'OPERADOR'),
('operador-demo-2', 'COLE_AQUI_O_HASH_BCRYPT_DO_OPERADOR_2', 'OPERADOR');

INSERT INTO produtos (nome, preco, quantidade, estoque_minimo) VALUES
('Teclado USB', 89.90, 15, 5),
('Mouse óptico', 49.90, 20, 5),
('Monitor 24 polegadas', 899.90, 8, 2);
```

Substitua todos os valores `COLE_AQUI...` por hashes BCrypt reais antes de executar o INSERT de usuários. Os IDs são gerados automaticamente pelo banco.

## Perfis de acesso

- **OPERADOR:** pode consultar, cadastrar e atualizar produtos, além de registrar movimentações.
- **ADMINISTRADOR:** possui as permissões do operador e também pode excluir produtos.

Para atualizar registros antigos, primeiro atribua o perfil de operador aos usuários existentes e depois promova a conta administrativa desejada:

```sql
UPDATE usuarios
SET perfil = 'OPERADOR'
WHERE perfil IS NULL;

UPDATE usuarios
SET perfil = 'ADMINISTRADOR'
WHERE login = 'SEU_LOGIN';
```

Substitua `SEU_LOGIN` pelo login real da conta que deve ser administradora.

Senhas armazenadas em texto puro em instalações antigas continuam funcionando: no primeiro login válido, o sistema converte a senha para BCrypt. Novos cadastros de usuários devem armazenar o hash BCrypt, nunca a senha original.

## Funcionalidades

- Autenticação por login e senha com hash BCrypt e migração de senhas legadas.
- Proteção CSRF nas operações HTTP de escrita.
- Perfis de administrador e operador.
- Cadastro, consulta, edição e exclusão autorizada de produtos.
- Pesquisa e ordenação de produtos.
- Alterações de saldo de estoque somente pelo fluxo de movimentações, preservando rastreabilidade.
- Registro de entradas e saídas de estoque.
- Histórico com usuário responsável, data e quantidade anterior/posterior.
- Validação contra estoque negativo e alertas de estoque baixo ou zerado.
- Bloqueio de exclusão de produtos com movimentações vinculadas, preservando o histórico.

## Testes

Os testes utilizam H2 em memória e não dependem de um servidor MySQL.

Windows:

```powershell
.\mvnw.cmd clean verify
```

Linux ou macOS:

```bash
./mvnw clean verify
```
