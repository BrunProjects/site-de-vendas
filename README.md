# site-de-vendas
avaliação de programação web 2

## Configuração do banco

A rota `/produtos` usa o banco PostgreSQL. Para rodar a aplicação localmente, crie um arquivo `.env` na raiz do projeto com uma das opções abaixo:

```env
DATABASE_URL=postgres://postgres:senha@localhost:5432/site_de_vendas
```

ou:

```env
DB_URL=jdbc:postgresql://localhost:5432/site_de_vendas
DB_USER=postgres
DB_PASSWORD=senha
```

Copie o arquivo `.env.example` para `.env` e ajuste os valores do seu ambiente. O arquivo `.env` é lido pelo processo que executa a aplicação. O Tomcat instalado separadamente não herda automaticamente o `.env` da pasta do projeto.

Para iniciar o Tomcat local usando essa configuração:

1. Gere o WAR com `mvn package`.
2. Pare o Tomcat que já estiver usando a porta 8080.
3. Copie `target/site-de-vendas.war` para `C:\tomcat\apache-tomcat-11.0.27\webapps\site-de-vendas.war`.
4. Execute `./run-tomcat.ps1 -TomcatHome C:\tomcat\apache-tomcat-11.0.27` no PowerShell.

O script lê `DATABASE_URL` do `.env` sem exibi-la e inicia o Tomcat em primeiro plano.

## Estrutura da aplicação

- `/index.jsp`: página inicial
- `/produtos`: servlet responsável por listar e cadastrar produtos
- `/produtos.jsp`: tela de gerenciamento de produtos
- `/cadastro` e `/login`: criação e acesso de contas
- `/conta`: área da conta autenticada
- `/logout`: encerra a sessão por POST
- `/carrinho`: carrinho persistido na sessão, com inclusão e remoção de produtos
- `/checkout`: finalização autenticada com endereço de entrega
- `/pedido-concluido`: confirmação do pedido

## Contas

O primeiro cadastro cria automaticamente a tabela `usuarios` no PostgreSQL. As senhas são armazenadas com PBKDF2, salt aleatório e nunca em texto puro. O login normaliza o e-mail e cria uma sessão após autenticação.

O carrinho fica associado à sessão HTTP, continua disponível entre páginas e é preservado quando o cliente entra na conta. O checkout registra pedido, itens e endereço de entrega em transação; o estoque só é reduzido quando o pedido é concluído.

## Observação

Se a variável do banco não estiver configurada, a aplicação responderá com `503 Service Unavailable` e mostrará uma mensagem na tela, em vez de exibir a página genérica de erro do Tomcat.
