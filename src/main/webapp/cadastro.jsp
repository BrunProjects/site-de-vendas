<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="br.com.presentedacegonha.model.Usuario" %>
<%!
    private String escaparHtmlCadastro(String valor) {
        if (valor == null) return "";
        return valor.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
%>
<%
    String erro = (String) request.getAttribute("erro");
    String nome = (String) request.getAttribute("nome");
    String email = (String) request.getAttribute("email");
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
    <title>Criar conta | Presente da Cegonha</title>
    <style>
        * { box-sizing: border-box; }
        body { margin: 0; min-height: 100vh; display: grid; place-items: center; padding: 24px; font-family: Arial, sans-serif; color: #44353b; background: linear-gradient(145deg, #f6e3da, #f7f4e8 55%, #dfece4); }
        main { width: min(100%, 480px); padding: 34px; background: #fff; border: 1px solid #ded8ce; border-radius: 8px; box-shadow: 0 16px 48px #3c30221a; }
        a { color: #315a4b; }
        h1 { margin: 0 0 8px; color: #315a4b; font-size: 28px; }
        p { line-height: 1.5; }
        label { display: block; margin: 16px 0 7px; font-weight: bold; }
        input { width: 100%; padding: 12px; border: 1px solid #b9c4bb; border-radius: 4px; font: inherit; }
        button { width: 100%; margin-top: 24px; padding: 13px; border: 0; border-radius: 4px; background: #315a4b; color: white; font: inherit; font-weight: bold; cursor: pointer; }
        .alerta { padding: 12px; border-left: 4px solid #a64437; background: #fff1ed; color: #722b21; }
        .links { margin-top: 22px; }
    </style>
</head>
<body>
<main>
    <a href="<%= request.getContextPath() %>/index.jsp">Presente da Cegonha</a>
    <h1>Criar sua conta</h1>
    <p>Preencha os dados para se cadastrar.</p>
    <% if (erro != null) { %>
        <p class="alerta" role="alert"><%= escaparHtmlCadastro(erro) %></p>
    <% } %>
    <form action="<%= request.getContextPath() %>/cadastro" method="post">
        <label for="nome">Nome</label>
        <input id="nome" name="nome" type="text" autocomplete="name" maxlength="120" value="<%= escaparHtmlCadastro(nome) %>" required>
        <label for="email">E-mail</label>
        <input id="email" name="email" type="email" autocomplete="email" maxlength="254" value="<%= escaparHtmlCadastro(email) %>" required>
        <label for="senha">Senha</label>
        <input id="senha" name="senha" type="password" autocomplete="new-password" minlength="8" maxlength="256" required>
        <label for="confirmarSenha">Confirmar senha</label>
        <input id="confirmarSenha" name="confirmarSenha" type="password" autocomplete="new-password" minlength="8" maxlength="256" required>
        <button type="submit">Criar conta</button>
    </form>
    <p class="links">Já tem uma conta? <a href="<%= request.getContextPath() %>/login">Entrar</a></p>
</main>
</body>
</html>