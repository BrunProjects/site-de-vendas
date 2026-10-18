<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.math.RoundingMode" %>
<%@ page import="br.com.presentedacegonha.model.Usuario" %>
<%!
    private String escaparHtmlCheckout(String valor) {
        if (valor == null) return "";
        return valor.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
%>
<%
    String erro = (String) request.getAttribute("erro");
    BigDecimal total = (BigDecimal) request.getAttribute("totalCheckout");
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    if (total == null) total = BigDecimal.ZERO;
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Entrega | Presente da Cegonha</title>
    <style>
        * { box-sizing: border-box; }
        body { margin: 0; font-family: Arial, sans-serif; color: #403b35; background: #f7f5ed; }
        header { padding: 18px max(22px, calc((100% - 900px) / 2)); background: #315a4b; color: white; }
        header a { color: white; }
        main { max-width: 900px; margin: 34px auto; padding: 0 22px; }
        form { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 16px; padding: 24px; background: white; border: 1px solid #d9ded6; }
        label { display: grid; gap: 6px; font-weight: bold; }
        input { width: 100%; padding: 11px; border: 1px solid #aab8ac; border-radius: 4px; font: inherit; }
        .wide { grid-column: 1 / -1; }
        button, .botao { display: inline-block; width: fit-content; padding: 12px 18px; border: 0; border-radius: 4px; background: #315a4b; color: white; text-decoration: none; font: inherit; font-weight: bold; cursor: pointer; }
        .alerta { padding: 12px; background: #fff1ed; border-left: 4px solid #a64437; color: #722b21; }
        .total { font-size: 20px; font-weight: bold; }
        @media (max-width: 620px) { form { grid-template-columns: 1fr; } .wide { grid-column: auto; } }
    </style>
</head>
<body>
<header><a href="<%= request.getContextPath() %>/carrinho">Voltar ao carrinho</a></header>
<main>
    <h1>Endereço de entrega</h1>
    <p>Pedido para <%= escaparHtmlCheckout(usuario.getNome()) %></p>
    <p class="total">Total: R$ <%= total.setScale(2, RoundingMode.HALF_UP).toPlainString().replace(".", ",") %></p>
    <% if (erro != null) { %><p class="alerta" role="alert"><%= escaparHtmlCheckout(erro) %></p><% } %>
    <form action="<%= request.getContextPath() %>/checkout" method="post">
        <label>Destinatário<input name="destinatario" maxlength="120" autocomplete="name" required></label>
        <label>Telefone<input name="telefone" maxlength="30" autocomplete="tel" required></label>
        <label>CEP<input name="cep" maxlength="9" autocomplete="postal-code" placeholder="00000-000" required></label>
        <label>Estado (UF)<input name="estado" maxlength="2" autocomplete="address-level1" required></label>
        <label class="wide">Endereço<input name="endereco" maxlength="180" autocomplete="street-address" required></label>
        <label>Número<input name="numero" maxlength="20" required></label>
        <label>Complemento<input name="complemento" maxlength="120"></label>
        <label class="wide">Cidade<input name="cidade" maxlength="100" autocomplete="address-level2" required></label>
        <button class="wide" type="submit">Confirmar pedido</button>
    </form>
</main>
</body>
</html>