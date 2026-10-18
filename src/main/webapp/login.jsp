<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="br.com.presentedacegonha.model.Usuario" %>
<%!
    private String escaparHtmlLogin(String valor) {
        if (valor == null) return "";
        return valor.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
%>
<%
    String erro = (String) request.getAttribute("erro");
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario != null) {
        response.sendRedirect(request.getContextPath() + "/conta");
        return;
    }
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Entrar | Presente da Cegonha</title>
    <style>
        * { box-sizing: border-box; }
        body { margin: 0; min-height: 100vh; display: grid; place-items: center; padding: 24px; font-family: Arial, sans-serif; color: #44353b; background: linear-gradient(145deg, #f6e3da, #f7f4e8 55%, #dfece4); }
        main { width: min(100%, 440px); padding: 34px; background: #fff; border: 1px solid #ded8ce; border-radius: 8px; box-shadow: 0 16px 48px #3c30221a; }
        a { color: #315a4b; }
        h1 { margin: 0 0 8px; color: #315a4b; font-size: 28px; }
        p { line-height: 1.5; }
        label { display: block; margin: 18px 0 7px; font-weight: bold; }
        input { width: 100%; padding: 12px; border: 1px solid #b9c4bb; border-radius: 4px; font: inherit; }
        button { width: 100%; margin-top: 24px; padding: 13px; border: 0; border-radius: 4px; background: #315a4b; color: white; font: inherit; font-weight: bold; cursor: pointer; }
        .alerta { padding: 12px; border-left: 4px solid #a64437; background: #fff1ed; color: #722b21; }
        .sucesso { padding: 12px; border-left: 4px solid #367350; background: #edf7ef; color: #245238; }
        .links { display: flex; justify-content: space-between; gap: 14px; margin-top: 22px; flex-wrap: wrap; }
    </style>
</head>
<body>
<main>
    <a href="<%= request.getContextPath() %>/index.jsp">Presente da Cegonha</a>
    <h1>Entrar na conta</h1>
    <p>Acesse sua conta para continuar.</p>
    <% if ("ok".equals(request.getParameter("cadastro"))) { %>
        <p class="sucesso" role="status">Conta criada. Entre com seu e-mail e senha.</p>
    <% } else if ("ok".equals(request.getParameter("logout"))) { %>
        <p class="sucesso" role="status">Você saiu da sua conta.</p>
    <% } %>
    <% if (erro != null) { %>
        <p class="alerta" role="alert"><%= escaparHtmlLogin(erro) %></p>
    <% } %>
    <form action="<%= request.getContextPath() %>/login" method="post">
        <label for="email">E-mail</label>
        <input id="email" name="email" type="email" autocomplete="email" maxlength="254" required>
        <label for="senha">Senha</label>
        <input id="senha" name="senha" type="password" autocomplete="current-password" required>
        <button type="submit">Entrar</button>
    </form>
    <div class="links">
        <a href="<%= request.getContextPath() %>/cadastro">Criar conta</a>
        <a href="<%= request.getContextPath() %>/produtos">Ver produtos</a>
    </div>
</main>
</body>
</html>