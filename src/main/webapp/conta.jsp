<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="br.com.presentedacegonha.model.Usuario" %>
<%!
    private String escaparHtmlConta(String valor) {
        if (valor == null) return "";
        return valor.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
%>
<%
    Usuario usuario = (Usuario) request.getAttribute("usuario");
    if (usuario == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Minha conta | Presente da Cegonha</title>
    <style>
        * { box-sizing: border-box; }
        body { margin: 0; min-height: 100vh; font-family: Arial, sans-serif; color: #44353b; background: #f5f4ec; }
        header { display: flex; justify-content: space-between; align-items: center; gap: 16px; flex-wrap: wrap; padding: 20px max(24px, calc((100% - 1040px) / 2)); background: #315a4b; color: white; }
        header a { color: white; }
        main { max-width: 1040px; margin: 42px auto; padding: 0 24px; }
        h1 { color: #315a4b; }
        .acoes { display: flex; gap: 12px; flex-wrap: wrap; margin-top: 28px; }
        .botao { display: inline-block; padding: 12px 18px; border-radius: 4px; background: #315a4b; color: white; text-decoration: none; font-weight: bold; }
        button { padding: 12px 18px; border: 1px solid #a64437; border-radius: 4px; background: transparent; color: #8c3429; font: inherit; font-weight: bold; cursor: pointer; }
    </style>
</head>
<body>
<header>
    <a href="<%= request.getContextPath() %>/index.jsp">Presente da Cegonha</a>
    <span>Conta conectada: <%= escaparHtmlConta(usuario.getEmail()) %></span>
</header>
<main>
    <h1>Olá, <%= escaparHtmlConta(usuario.getNome()) %></h1>
    <p>Sua conta está ativa.</p>
    <div class="acoes">
        <a class="botao" href="<%= request.getContextPath() %>/produtos">Ver produtos</a>
        <a class="botao" href="<%= request.getContextPath() %>/carrinho">Ver carrinho</a>
        <form action="<%= request.getContextPath() %>/logout" method="post">
            <button type="submit">Sair da conta</button>
        </form>
    </div>
</main>
</body>
</html>