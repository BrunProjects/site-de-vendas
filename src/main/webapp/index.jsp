
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Presente da Cegonha</title>
    <style>
        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background-color: #fff8f5;
            color: #45343b;
        }

        header {
            padding: 24px;
            text-align: center;
            background-color: #f5dce5;
        }

        header h1 {
            margin: 0;
            color: #8b4966;
        }

        main {
            max-width: 900px;
            margin: 50px auto;
            padding: 24px;
            text-align: center;
        }

        .categorias {
            display: flex;
            justify-content: center;
            flex-wrap: wrap;
            gap: 12px;
            margin-top: 30px;
        }

        .categoria {
            padding: 16px 22px;
            background: white;
            border: 1px solid #efd5df;
            border-radius: 12px;
        }
    </style>
</head>
<body>
    <header>
        <h1>Presente da Cegonha</h1>
        <p>Carinho e cuidado em cada detalhe.</p>
    </header>

    <main>
        <h2>Bem-vindo à nossa loja!</h2>
        <p>Encontre tudo para acompanhar os primeiros momentos do seu bebê.</p>

        <div class="categorias">
            <div class="categoria">Brinquedos</div>
            <div class="categoria">Roupas</div>
            <div class="categoria">Enxoval</div>
            <div class="categoria">Higiene e Saúde</div>
        </div>
    </main>
</body>
</html>
