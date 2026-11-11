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
` ```

No Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

No Linux ou macOS:

```bash
./mvnw spring-boot:run
```

Acesse [http://localhost:8080](http://localhost:8080). O primeiro usuário precisa existir na tabela `usuarios`; esta versão do projeto ainda não possui tela pública de cadastro de usuários.

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

- Autenticação por login e senha.
- Perfis de administrador e operador.
- Cadastro, consulta, edição e exclusão autorizada de produtos.
- Pesquisa e ordenação de produtos.
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
