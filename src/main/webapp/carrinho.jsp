<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.math.RoundingMode" %>
<%@ page import="br.com.presentedacegonha.model.ItemCarrinho" %>
<%@ page import="br.com.presentedacegonha.model.Usuario" %>
<%!
    private String escaparHtmlCarrinho(String valor) {
        if (valor == null) return "";
        return valor.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
%>
<%
    List<ItemCarrinho> itens =
            (List<ItemCarrinho>) request.getAttribute("itensCarrinho");
    BigDecimal total = (BigDecimal) request.getAttribute("totalCarrinho");
    String erro = (String) request.getAttribute("erro");
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (total == null) total = BigDecimal.ZERO;
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Carrinho | Presente da Cegonha</title>
    <style>
        * { box-sizing: border-box; }
        body { margin: 0; font-family: Arial, sans-serif; color: #403b35; background: #f7f5ed; }
        header { display: flex; justify-content: space-between; align-items: center; gap: 16px; flex-wrap: wrap; padding: 18px max(22px, calc((100% - 1080px) / 2)); background: #315a4b; color: #fff; }
        header a { color: #fff; }
        nav { display: flex; gap: 18px; flex-wrap: wrap; }
        main { max-width: 1080px; margin: 36px auto; padding: 0 22px; }
        h1 { color: #315a4b; }
        table { width: 100%; border-collapse: collapse; background: #fff; }
        th, td { padding: 14px; border-bottom: 1px solid #dedbd2; text-align: left; }
        th { background: #e8eee7; }
        .total { margin: 24px 0; text-align: right; font-size: 20px; font-weight: bold; }
        button, .botao { display: inline-block; border: 0; border-radius: 4px; padding: 11px 16px; background: #315a4b; color: #fff; font: inherit; text-decoration: none; cursor: pointer; }
        .remover { background: #9b3f32; }
        .acoes { display: flex; justify-content: flex-end; gap: 12px; flex-wrap: wrap; }
        .vazio, .alerta { padding: 18px; background: #fff; border-left: 4px solid #b88b38; }
        .alerta { border-color: #a64437; color: #722b21; }
        @media (max-width: 600px) { th:nth-child(2), td:nth-child(2) { display: none; } }
    </style>
</head>
<body>
<header>
    <strong>Presente da Cegonha</strong>
    <nav>
        <a href="<%= request.getContextPath() %>/produtos">Continuar comprando</a>
        <% if (usuario == null) { %>
            <a href="<%= request.getContextPath() %>/login">Entrar</a>
        <% } else { %>
            <a href="<%= request.getContextPath() %>/conta">Minha conta</a>
        <% } %>
    </nav>
</header>
<main>
    <h1>Seu carrinho</h1>
    <% if (erro != null) { %><p class="alerta" role="alert"><%= escaparHtmlCarrinho(erro) %></p><% } %>
    <% if (itens == null || itens.isEmpty()) { %>
        <p class="vazio">Seu carrinho está vazio.</p>
        <a class="botao" href="<%= request.getContextPath() %>/produtos">Ver produtos</a>
    <% } else { %>
        <table>
            <thead><tr><th>Produto</th><th>Descrição</th><th>Preço unitário</th><th>Qtd.</th><th>Subtotal</th><th></th></tr></thead>
            <tbody>
            <% for (ItemCarrinho item : itens) { %>
                <tr>
                    <td><%= escaparHtmlCarrinho(item.getProduto().getNome()) %></td>
                    <td><%= escaparHtmlCarrinho(item.getProduto().getDescricao()) %></td>
                    <td>R$ <%= item.getProduto().getPreco().setScale(2, RoundingMode.HALF_UP).toPlainString().replace(".", ",") %></td>
                    <td><%= item.getQuantidade() %></td>
                    <td>R$ <%= item.getSubtotal().setScale(2, RoundingMode.HALF_UP).toPlainString().replace(".", ",") %></td>
                    <td>
                        <form action="<%= request.getContextPath() %>/carrinho" method="post">
                            <input type="hidden" name="acao" value="remover">
                            <input type="hidden" name="produtoId" value="<%= item.getProduto().getId() %>">
                            <button class="remover" type="submit">Remover</button>
                        </form>
                    </td>
                </tr>
            <% } %>
            </tbody>
        </table>
        <p class="total">Total: R$ <%= total.setScale(2, RoundingMode.HALF_UP).toPlainString().replace(".", ",") %></p>
        <div class="acoes">
            <a class="botao" href="<%= request.getContextPath() %>/produtos">Continuar comprando</a>
            <a class="botao" href="<%= request.getContextPath() %>/checkout">Finalizar compra</a>
        </div>
    <% } %>
</main>
</body>
</html>