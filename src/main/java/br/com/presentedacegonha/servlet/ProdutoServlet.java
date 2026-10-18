
package br.com.presentedacegonha.servlet;

import br.com.presentedacegonha.dao.ProdutoDAO;
import br.com.presentedacegonha.model.Produto;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/produtos")
public class ProdutoServlet extends HttpServlet {

    private ProdutoDAO produtoDAO;

    @Override
    public void init() {
        produtoDAO = new ProdutoDAO();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        exibirPagina(request, response, HttpServletResponse.SC_OK, null);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String acao = request.getParameter("acao");

        try {
            if ("cadastrar".equals(acao)) {
                cadastrarProduto(request);
            } else {
                exibirPagina(request, response,
                        HttpServletResponse.SC_BAD_REQUEST, "Ação inválida.");
                return;
            }

            response.sendRedirect(
                    request.getContextPath() + "/produtos");

        } catch (IllegalArgumentException e) {
            exibirPagina(request, response,
                    HttpServletResponse.SC_BAD_REQUEST, e.getMessage());

        } catch (IllegalStateException e) {
            getServletContext().log("Banco indisponível ao salvar produto.", e);
            exibirPagina(request, response,
                    HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "Banco de dados indisponível. Confira a configuração do servidor.");

        } catch (SQLException e) {
            getServletContext().log("Falha ao salvar produto.", e);
            exibirPagina(request, response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Não foi possível salvar o produto. Confira os dados e tente novamente.");
        }
    }

    private void exibirPagina(
            HttpServletRequest request,
            HttpServletResponse response,
            int status,
            String mensagem)
            throws ServletException, IOException {

        List<Produto> produtos = new ArrayList<>();
        int statusFinal = status;
        String mensagemFinal = mensagem;

        try {
            produtos = produtoDAO.listarTodos();
        } catch (IllegalStateException e) {
            getServletContext().log("Banco não configurado para a página de produtos.", e);
            statusFinal = HttpServletResponse.SC_SERVICE_UNAVAILABLE;
            mensagemFinal = "Banco de dados indisponível. Confira a configuração do servidor.";
        } catch (SQLException e) {
            getServletContext().log("Falha ao listar produtos.", e);
            statusFinal = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            mensagemFinal = "Não foi possível carregar os produtos.";
        }

        request.setAttribute("produtos", produtos);
        request.setAttribute("erro", mensagemFinal);
        response.setStatus(statusFinal);
        request.getRequestDispatcher("/produtos.jsp").forward(request, response);
    }

    private void cadastrarProduto(
            HttpServletRequest request) throws SQLException {

        String nome = request.getParameter("nome");
        String categoriaTexto = request.getParameter("categoriaId");
        String precoTexto = request.getParameter("preco");
        String estoqueTexto = request.getParameter("estoque");

        if (nome == null || nome.isBlank()
                || categoriaTexto == null
                || precoTexto == null
                || estoqueTexto == null) {
            throw new IllegalArgumentException(
                    "Preencha os campos obrigatórios.");
        }

        try {
                long categoriaId = Long.parseLong(categoriaTexto.trim());
                BigDecimal preco = new BigDecimal(
                    precoTexto.trim().replace(',', '.'));
            int estoque = Integer.parseInt(estoqueTexto);

            if (categoriaId <= 0 || preco.signum() < 0
                    || preco.scale() > 2 || estoque < 0
                    || nome.trim().length() > 150) {
                throw new IllegalArgumentException(
                    "Confira categoria, nome, preço e estoque.");
            }

                Produto produto = new Produto(
                    categoriaId,
                    nome.trim(),
                    valorOpcional(request.getParameter("descricao")),
                    preco,
                    estoque,
                    valorOpcional(request.getParameter("imagemUrl")),
                    true);

            produtoDAO.cadastrar(produto);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Confira os valores de categoria, preço e estoque.");
        }
    }

    private String valorOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }
}
