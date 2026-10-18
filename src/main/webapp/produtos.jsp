
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="br.com.presentedacegonha.model.Produto" %>
<%@ page import="java.math.RoundingMode" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="br.com.presentedacegonha.model.Usuario" %>
<%@ page import="br.com.presentedacegonha.model.Carrinho" %>

<%!
    private String escaparHtml(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private String nomeCategoria(Long categoriaId) {
        if (categoriaId == null) return "Sem categoria";
        return switch (categoriaId.intValue()) {
            case 1 -> "Brinquedos";
            case 2 -> "Roupas";
            case 3 -> "Enxoval";
            case 4 -> "Higiene e Saúde";
            default -> "Outros";
        };
    }
%>

<%
    List<Produto> produtos =
        (List<Produto>) request.getAttribute("produtos");
    String erro = (String) request.getAttribute("erro");
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    Carrinho carrinho = (Carrinho) session.getAttribute("carrinho");
    int quantidadeCarrinho = carrinho == null ? 0 : carrinho.getQuantidadeTotal();
%>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Produtos | Presente da Cegonha</title>

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background: #fff8fa;
            color: #59434b;
        }

        header {
            background: #f4cbd8;
            padding: 22px 7%;
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 15px;
        }

        header h1 {
            margin: 0;
            color: #713d52;
            font-size: 25px;
        }

        header a {
            color: #713d52;
            text-decoration: none;
            font-weight: bold;
        }

        .navegacao {
            display: flex;
            align-items: center;
            gap: 18px;
            flex-wrap: wrap;
        }

        main {
            max-width: 1100px;
            margin: 35px auto;
            padding: 0 20px;
        }

        h2 {
            color: #874d65;
        }

        .painel {
            background: white;
            border: 1px solid #f0dce3;
            border-radius: 14px;
            padding: 25px;
            margin: 25px 0;
            box-shadow: 0 4px 14px #713d520d;
        }

        .formulario {
            display: grid;
            grid-template-columns: repeat(2, minmax(0, 1fr));
            gap: 16px;
        }

        .campo {
            display: flex;
            flex-direction: column;
            gap: 7px;
        }

        .completo {
            grid-column: 1 / -1;
        }

        label {
            font-size: 14px;
            font-weight: bold;
        }

        input, select, textarea {
            width: 100%;
            padding: 11px;
            border: 1px solid #e3cbd4;
            border-radius: 8px;
            font: inherit;
            background: #fffdfd;
        }

        textarea {
            resize: vertical;
            min-height: 85px;
        }

        button, .botao {
            display: inline-block;
            border: none;
            border-radius: 8px;
            padding: 12px 20px;
            background: #bd7893;
            color: white;
            font-weight: bold;
            text-decoration: none;
            cursor: pointer;
        }

        button:hover, .botao:hover {
            background: #a7617e;
        }

        .tabela-container {
            overflow-x: auto;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            background: white;
        }

        th, td {
            padding: 13px;
            border-bottom: 1px solid #f0e1e6;
            text-align: left;
        }

        th {
            background: #fbeaf0;
            color: #713d52;
        }

        .vazio {
            padding: 24px;
            text-align: center;
            color: #8a737c;
        }

        footer {
            text-align: center;
            padding: 25px;
            color: #8a737c;
            font-size: 13px;
        }

        @media (max-width: 650px) {
            .formulario {
                grid-template-columns: 1fr;
            }

            .completo {
                grid-column: auto;
            }

            header {
                flex-direction: column;
                align-items: flex-start;
            }
        }
    </style>
</head>

<body>

    <header>
        <h1>Presente da Cegonha</h1>

        <nav class="navegacao">
            <a href="<%= request.getContextPath() %>/index.jsp">Início</a>
            <a href="<%= request.getContextPath() %>/carrinho">Carrinho (<%= quantidadeCarrinho %>)</a>
            <% if (usuario == null) { %>
                <a href="<%= request.getContextPath() %>/login">Entrar</a>
            <% } else { %>
                <a href="<%= request.getContextPath() %>/conta">Minha conta</a>
            <% } %>
        </nav>
    </header>

    <main>
        <h2>Gerenciamento de produtos</h2>

        <p>
            Cadastre e consulte os produtos disponíveis na loja.
        </p>

        <% if (erro != null && !erro.isBlank()) { %>
        <p role="alert" style="padding: 12px; border: 1px solid #b42318; color: #8f1d14; background: #fff1f0;">
            <%= escaparHtml(erro) %>
        </p>
        <% } %>

        <section class="painel">
            <h2>Cadastrar produto</h2>

            <form class="formulario"
                  action="<%= request.getContextPath() %>/produtos"
                  method="post">

                <input type="hidden" name="acao" value="cadastrar">

                <div class="campo completo">
                    <label for="nome">Nome do produto *</label>
                    <input type="text"
                           id="nome"
                           name="nome"
                           maxlength="150"
                           required>
                </div>

                <div class="campo">
                    <label for="categoriaId">Categoria *</label>

                    <select id="categoriaId"
                            name="categoriaId"
                            required>
                        <option value="">Selecione uma categoria</option>
                        <option value="1">Brinquedos</option>
                        <option value="2">Roupas</option>
                        <option value="3">Enxoval</option>
                        <option value="4">Higiene e Saúde</option>
                    </select>
                </div>

                <div class="campo">
                    <label for="preco">Preço (R$) *</label>
                    <input type="number"
                           id="preco"
                           name="preco"
                           min="0"
                           step="0.01"
                           placeholder="0.00"
                           required>
                </div>

                <div class="campo">
                    <label for="estoque">Quantidade em estoque *</label>
                    <input type="number"
                           id="estoque"
                           name="estoque"
                           min="0"
                           step="1"
                           value="0"
                           required>
                </div>

                <div class="campo">
                    <label for="imagemUrl">URL da imagem</label>
                    <input type="url"
                           id="imagemUrl"
                           name="imagemUrl"
                           placeholder="https://exemplo.com/imagem.jpg">
                </div>

                <div class="campo completo">
                    <label for="descricao">Descrição</label>
                    <textarea id="descricao"
                              name="descricao"
                              placeholder="Descreva o produto..."></textarea>
                </div>

                <div class="campo completo">
                    <button type="submit">Cadastrar produto</button>
                </div>
            </form>
        </section>

        <section class="painel">
            <h2>Produtos cadastrados</h2>

            <div class="tabela-container">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Produto</th>
                            <th>Categoria</th>
                            <th>Preço</th>
                            <th>Estoque</th>
                            <th>Status</th>
                            <th>Comprar</th>
                        </tr>
                    </thead>

                    <tbody>
                        <%
                            if (produtos != null && !produtos.isEmpty()) {
                                for (Produto produto : produtos) {
                                    BigDecimal precoProduto = produto.getPreco() == null
                                            ? BigDecimal.ZERO : produto.getPreco();
                        %>
                        <tr>
                            <td><%= escaparHtml(String.valueOf(produto.getId())) %></td>
                            <td><%= escaparHtml(produto.getNome()) %></td>
                            <td><%= nomeCategoria(produto.getCategoriaId()) %></td>
                            <td>
                                R$ <%= precoProduto
                                    .setScale(2, RoundingMode.HALF_UP)
                                    .toString()
                                    .replace(".", ",") %>
                            </td>
                            <td><%= produto.getEstoque() %></td>
                            <td>
                                <%= produto.isAtivo()
                                    ? "Ativo" : "Inativo" %>
                            </td>
                            <td>
                                <% if (produto.isAtivo() && produto.getEstoque() > 0) { %>
                                <form action="<%= request.getContextPath() %>/carrinho" method="post">
                                    <input type="hidden" name="acao" value="adicionar">
                                    <input type="hidden" name="produtoId" value="<%= produto.getId() %>">
                                    <input type="hidden" name="quantidade" value="1">
                                    <button type="submit">Adicionar</button>
                                </form>
                                <% } else { %>
                                    <span>Indisponível</span>
                                <% } %>
                            </td>
                        </tr>
                        <%
                                }
                            } else {
                        %>
                        <tr>
                            <td colspan="7" class="vazio">
                                Nenhum produto encontrado.
                            </td>
                        </tr>
                        <%
                            }
                        %>
                    </tbody>
                </table>
            </div>
        </section>
    </main>

    <footer>
        Presente da Cegonha &copy; 2026 — Loja de produtos infantis
    </footer>

</body>
</html>
