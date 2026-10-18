package br.com.presentedacegonha.servlet;

import br.com.presentedacegonha.dao.ProdutoDAO;
import br.com.presentedacegonha.model.Carrinho;
import br.com.presentedacegonha.model.ItemCarrinho;
import br.com.presentedacegonha.model.Produto;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet("/carrinho")
public class CarrinhoServlet extends HttpServlet {

    private static final String ATRIBUTO_CARRINHO = "carrinho";

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
        renderizar(request, response, HttpServletResponse.SC_OK, null);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        try {
            String acao = request.getParameter("acao");
            Carrinho carrinho = obterCarrinho(request);

            if ("adicionar".equals(acao)) {
                adicionar(request, carrinho);
            } else if ("remover".equals(acao)) {
                carrinho.remover(parsearId(request.getParameter("produtoId")));
            } else {
                throw new IllegalArgumentException("Ação de carrinho inválida.");
            }

            response.sendRedirect(request.getContextPath() + "/carrinho");
        } catch (IllegalArgumentException e) {
            renderizar(request, response,
                    HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (IllegalStateException e) {
            getServletContext().log("Banco indisponível ao atualizar carrinho.", e);
            renderizar(request, response,
                    HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "Banco de dados indisponível. Tente novamente.");
        } catch (SQLException e) {
            getServletContext().log("Falha ao atualizar carrinho.", e);
            renderizar(request, response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Não foi possível atualizar o carrinho.");
        }
    }

    private void adicionar(
            HttpServletRequest request,
            Carrinho carrinho) throws SQLException {
        Long produtoId = parsearId(request.getParameter("produtoId"));
        int quantidade;
        try {
            quantidade = Integer.parseInt(request.getParameter("quantidade"));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Quantidade inválida.");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva.");
        }

        Produto produto = produtoDAO.buscarPorId(produtoId);
        if (produto == null || !produto.isAtivo()) {
            throw new IllegalArgumentException(
                    "Este produto não está disponível.");
        }

        int quantidadeAtual = carrinho.getItens().getOrDefault(produtoId, 0);
        if (quantidade > produto.getEstoque() - quantidadeAtual) {
            throw new IllegalArgumentException(
                    "Quantidade maior que o estoque disponível.");
        }

        carrinho.adicionar(produtoId, quantidade);
    }

    private void renderizar(
            HttpServletRequest request,
            HttpServletResponse response,
            int status,
            String erro)
            throws ServletException, IOException {
        Carrinho carrinho = obterCarrinho(request);
        List<ItemCarrinho> itens = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        try {
            for (Map.Entry<Long, Integer> entrada
                    : carrinho.getItens().entrySet()) {
                Produto produto = produtoDAO.buscarPorId(entrada.getKey());
                if (produto == null) {
                    carrinho.remover(entrada.getKey());
                    continue;
                }
                ItemCarrinho item = new ItemCarrinho(
                        produto, entrada.getValue());
                itens.add(item);
                total = total.add(item.getSubtotal());
            }
        } catch (IllegalStateException e) {
            getServletContext().log("Banco não configurado para o carrinho.", e);
            status = HttpServletResponse.SC_SERVICE_UNAVAILABLE;
            erro = "Banco de dados indisponível. Tente novamente.";
        } catch (SQLException e) {
            getServletContext().log("Falha ao carregar o carrinho.", e);
            status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            erro = "Não foi possível carregar o carrinho.";
        }

        request.setAttribute("itensCarrinho", itens);
        request.setAttribute("totalCarrinho", total);
        request.setAttribute("erro", erro);
        response.setStatus(status);
        request.getRequestDispatcher("/carrinho.jsp").forward(request, response);
    }

    private Carrinho obterCarrinho(HttpServletRequest request) {
        HttpSession sessao = request.getSession(true);
        synchronized (sessao) {
            Object atual = sessao.getAttribute(ATRIBUTO_CARRINHO);
            if (atual instanceof Carrinho) {
                return (Carrinho) atual;
            }

            Carrinho carrinho = new Carrinho();
            sessao.setAttribute(ATRIBUTO_CARRINHO, carrinho);
            return carrinho;
        }
    }

    private Long parsearId(String texto) {
        try {
            long id = Long.parseLong(texto);
            if (id <= 0) {
                throw new NumberFormatException();
            }
            return id;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Produto inválido.");
        }
    }
}