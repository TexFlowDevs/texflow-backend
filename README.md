# TexFlow - Backend

Backend do TexFlow (gestão de processos produtivos para empresas têxteis), feito em **Spring Boot** + **PostgreSQL**.

## Arquitetura

- O banco de dados é **compartilhado** com o projeto BriqueApp: mesma instância Postgres no Render, banco `brique_app`.
- As tabelas do TexFlow ficam isoladas num **schema próprio**, chamado `textil`, separado do `public` (onde estão as tabelas do BriqueApp). Um não interfere no outro.
- As tabelas são criadas/atualizadas automaticamente pelo Hibernate a partir das classes `@Entity` (`spring.jpa.hibernate.ddl-auto=update`) - não precisa escrever `CREATE TABLE` na mão.

## Pré-requisitos

Antes de rodar o projeto, cada pessoa precisa instalar na própria máquina:

- **JDK 21** - recomendado [Eclipse Temurin 21](https://adoptium.net/temurin/releases/?version=21)
- **Maven** - baixe em [maven.apache.org](https://maven.apache.org/download.cgi) (ou use uma IDE como VS Code/IntelliJ com o suporte a Java/Maven instalado, que já resolve isso)

Pra conferir se já está tudo certo:
```bash
java -version
mvn -version
```

## Configuração do banco (uma vez só, por pessoa)

O `application.properties` já vem com a URL e o usuário do banco configurados (não são segredo). **A senha não vai para o Git** - por isso é obrigatório configurá-la localmente antes de rodar, senão a aplicação recusa subir.

1. Peça a senha do usuário `brique_app_user` pra quem já tem acesso (time do projeto) - **nunca cole ela em nenhum arquivo do repositório**.
2. Defina como variável de ambiente do usuário, no Windows (PowerShell):
   ```powershell
   [System.Environment]::SetEnvironmentVariable("DATABASE_PASSWORD", "SUA_SENHA_AQUI", "User")
   ```
   No Linux/Mac, adicione no `~/.bashrc` ou `~/.zshrc`:
   ```bash
   export DATABASE_PASSWORD="SUA_SENHA_AQUI"
   ```
3. **Feche e abra de novo o terminal/IDE** depois de definir a variável, senão ela não é reconhecida.

⚠️ Como o banco é **compartilhado** (mesma instância remota pra todo o time, não um banco local de cada um), dados criados por uma pessoa aparecem pra todo mundo. Cuidado ao testar criações/exclusões.

## Rodando o projeto

```bash
cd texflow-backend
mvn spring-boot:run
```

Se subir certo, o log termina com algo como:
```
Started BackendApplication in X seconds
```

E o servidor fica disponível em `http://localhost:8080`.

Teste rápido, no navegador ou Postman:
```
GET http://localhost:8080/api/empresas
```
Deve retornar `[]` (lista vazia) - confirma que a API conectou no banco corretamente.

## Estrutura do projeto

```
src/main/java/com/texflow/backend/
├── BackendApplication.java   # classe principal (ponto de entrada)
├── model/                    # entidades JPA (tabelas do banco)
│   ├── Usuario.java
│   ├── Empresa.java
│   ├── Contato.java          # sem tabela própria, embutido em Empresa
│   ├── Referencia.java
│   ├── ItemGrade.java        # sem tabela própria, usado nas grades da Operacao
│   ├── ProcessoProdutivo.java
│   ├── Operacao.java
│   ├── UserType.java         # enum: GESTOR, OPERADOR
│   └── Status.java           # enum: NAO_INICIADO, EM_ANDAMENTO, CONCLUIDO, CANCELADO
├── repository/                # interfaces JpaRepository - CRUD automático por entidade
└── controller/                 # endpoints REST (API que o app Flutter consome)
```

Os modelos foram baseados na documentação em [`../TexFlow/docs/models/models_docs.md`](../TexFlow/docs/models/models_docs.md).

## Problemas comuns

**`password authentication failed for user "brique_app_user"`**
A variável `DATABASE_PASSWORD` não foi definida, está errada, ou o terminal/IDE foi aberto antes de você configurá-la. Feche e abra de novo o terminal/IDE depois de definir a variável.

**Aplicação não inicia / erro de compilação**
Confira se `java -version` mostra a versão 21 e se `mvn -version` está usando esse mesmo JDK.
