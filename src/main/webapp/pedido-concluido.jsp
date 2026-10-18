<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Pedido confirmado | Presente da Cegonha</title>
    <style>
        * { box-sizing: border-box; }
        body { margin: 0; min-height: 100vh; display: grid; place-items: center; padding: 24px; font-family: Arial, sans-serif; color: #403b35; background: #f7f5ed; }
        main { width: min(100%, 560px); padding: 32px; background: white; border: 1px solid #d9ded6; border-radius: 6px; }
        h1 { color: #315a4b; }
        .botao { display: inline-block; margin-top: 16px; padding: 12px 18px; border-radius: 4px; background: #315a4b; color: white; text-decoration: none; font-weight: bold; }
    </style>
</head>
<body>
<main>
    <h1>Pedido confirmado</h1>
    <p>Número do pedido: <strong><%= request.getAttribute("pedidoId") %></strong></p>
    <p>O endereço informado foi registrado para a entrega.</p>
    <a class="botao" href="<%= request.getContextPath() %>/produtos">Voltar à loja</a>
</main>
</body>
</html>