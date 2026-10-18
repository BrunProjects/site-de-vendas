
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="br.com.presentedacegonha.model.Usuario" %>
<%
    Usuario usuarioLogado = (Usuario) session.getAttribute("usuario");
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Presente da Cegonha | Loja Infantil</title>

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            font-family: Arial, sans-serif;
            color: #59434b;
            background: #fff8fa;
        }

        header {
            background: #f4cbd8;
            padding: 22px 7%;
            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 20px;
            flex-wrap: wrap;
        }

        .logo {
            color: #713d52;
            font-size: 26px;
            font-weight: bold;
            text-decoration: none;
        }

        nav {
            display: flex;
            gap: 20px;
            align-items: center;
            flex-wrap: wrap;
        }

        nav a {
            color: #713d52;
            text-decoration: none;
            font-weight: bold;
        }

        nav a:hover {
            color: #a7617e;
        }

        .destaque {
            padding: 75px 20px;
            text-align: center;
            background: linear-gradient(135deg, #fbe5ed, #fff8fa);
        }

        .destaque h1 {
            margin: 0 0 18px;
            color: #874d65;
            font-size: clamp(30px, 5vw, 46px);
        }

        .destaque p {
            max-width: 620px;
            margin: 0 auto 28px;
            line-height: 1.7;
            font-size: 17px;
        }

        .botao {
            display: inline-block;
            padding: 13px 22px;
            border-radius: 9px;
            background: #bd7893;
            color: white;
            text-decoration: none;
            font-weight: bold;
        }

        .botao:hover {
            background: #a7617e;
        }

        .categorias {
            max-width: 1100px;
            margin: 45px auto;
            padding: 0 20px;
            text-align: center;
        }

        .categorias h2 {
            color: #874d65;
            margin-bottom: 28px;
        }

        .grade {
            display: grid;
            grid-template-columns: repeat(4, minmax(0, 1fr));
            gap: 18px;
        }

        .categoria {
            background: white;
            border: 1px solid #f0dce3;
            border-radius: 14px;
            padding: 28px 15px;
            box-shadow: 0 4px 14px #713d520d;
        }

        .categoria .icone {
            font-size: 35px;
            margin-bottom: 12px;
        }

        .categoria h3 {
            margin: 0;
            color: #874d65;
            font-size: 17px;
        }

        .sobre {
            max-width: 850px;
            margin: 50px auto;
            padding: 30px 20px;
            text-align: center;
            line-height: 1.8;
        }

        .sobre h2 {
            color: #874d65;
        }

        footer {
            padding: 25px 15px;
            text-align: center;
            background: #f4cbd8;
            color: #713d52;
            font-size: 14px;
        }

        @media (max-width: 750px) {
            .grade {
                grid-template-columns: repeat(2, minmax(0, 1fr));
            }

            header {
                flex-direction: column;
                align-items: flex-start;
            }
        }

        @media (max-width: 400px) {
            .grade {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>

<body>

    <header>
        <a class="logo"
           href="<%= request.getContextPath() %>/index.jsp">
            Presente da Cegonha
        </a>

        <nav>
            <a href="#categorias">Categorias</a>
            <a href="#sobre">Sobre a loja</a>
            <a href="<%= request.getContextPath() %>/produtos">
                Produtos
            </a>
            <a href="<%= request.getContextPath() %>/carrinho">Carrinho</a>
            <% if (usuarioLogado == null) { %>
                <a href="<%= request.getContextPath() %>/login">Entrar</a>
                <a href="<%= request.getContextPath() %>/cadastro">Criar conta</a>
            <% } else { %>
                <a href="<%= request.getContextPath() %>/conta">Minha conta</a>
            <% } %>
        </nav>
    </header>

    <main>
        <section class="destaque">
            <h1>Um mundo de carinho para os pequenos</h1>

            <p>
                Encontre roupas, brinquedos, itens de enxoval e produtos
                de higiene para acompanhar cada momento especial da infância.
            </p>

            <a class="botao"
               href="<%= request.getContextPath() %>/produtos">
                Conhecer produtos
            </a>
        </section>

        <section class="categorias" id="categorias">
            <h2>Nossas categorias</h2>

            <div class="grade">
                <article class="categoria">
                    <div class="icone">🧸</div>
                    <h3>Brinquedos</h3>
                </article>

                <article class="categoria">
                    <div class="icone">👕</div>
                    <h3>Roupas</h3>
                </article>

                <article class="categoria">
                    <div class="icone">🍼</div>
                    <h3>Enxoval</h3>
                </article>

                <article class="categoria">
                    <div class="icone">🧴</div>
                    <h3>Higiene e Saúde</h3>
                </article>
            </div>
        </section>

        <section class="sobre" id="sobre">
            <h2>Sobre a Presente da Cegonha</h2>

            <p>
                Nossa loja foi pensada para facilitar a busca por produtos
                infantis, reunindo praticidade, cuidado e carinho em um
                só lugar. Queremos tornar cada escolha mais especial
                para as famílias.
            </p>

            <a class="botao"
               href="<%= request.getContextPath() %>/produtos">
                Acessar produtos
            </a>
        </section>
    </main>

    <footer>
        <p>
            Presente da Cegonha &copy; 2026 —
            Todos os direitos reservados.
        </p>
    </footer>

</body>
</html>
