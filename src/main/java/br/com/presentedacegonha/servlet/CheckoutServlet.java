package br.com.presentedacegonha.servlet;

import br.com.presentedacegonha.dao.PedidoDAO;
import br.com.presentedacegonha.dao.ProdutoDAO;
import br.com.presentedacegonha.model.Carrinho;
import br.com.presentedacegonha.model.Produto;
import br.com.presentedacegonha.model.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Map;

@WebServlet(urlPatterns = {"/checkout", "/pedido-concluido"})
public class CheckoutServlet extends HttpServlet {

    private PedidoDAO pedidoDAO;
    private ProdutoDAO produtoDAO;

    @Override
    public void init() {
        pedidoDAO = new PedidoDAO();
        produtoDAO = new ProdutoDAO();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        if ("/pedido-concluido".equals(request.getServletPath())) {
            exibirConfirmacao(request, response);
            return;
        }

        Usuario usuario = usuarioLogado(request);
        if (usuario == null) {
            request.getSession(true).setAttribute("destinoAposLogin", "/checkout");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Carrinho carrinho = carrinho(request);
        if (carrinho == null || carrinho.estaVazio()) {
            response.sendRedirect(request.getContextPath() + "/carrinho");
            return;
        }
        renderizarCheckout(request, response, HttpServletResponse.SC_OK, null);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Usuario usuario = usuarioLogado(request);
        if (usuario == null) {
            request.getSession(true).setAttribute("destinoAposLogin", "/checkout");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Carrinho carrinho = carrinho(request);
        if (carrinho == null || carrinho.estaVazio()) {
            response.sendRedirect(request.getContextPath() + "/carrinho");
            return;
        }

        String destinatario = valor(request.getParameter("destinatario"));
        String telefone = valor(request.getParameter("telefone"));
        String cep = valor(request.getParameter("cep"));
        String endereco = valor(request.getParameter("endereco"));
        String numero = valor(request.getParameter("numero"));
        String complemento = valor(request.getParameter("complemento"));
        String cidade = valor(request.getParameter("cidade"));
        String estado = valor(request.getParameter("estado"));

        String erro = validarEndereco(destinatario, telefone, cep,
                endereco, numero, cidade, estado);
        if (erro != null) {
            renderizarCheckout(request, response,
                    HttpServletResponse.SC_BAD_REQUEST, erro);
            return;
        }

        try {
            long pedidoId = pedidoDAO.criarPedido(
                    usuario.getId(), carrinho, destinatario, telefone,
                    cep, endereco, numero, complemento, cidade,
                    estado.toUpperCase());
            carrinho.limpar();
            request.getSession().setAttribute("ultimoPedidoId", pedidoId);
            response.sendRedirect(request.getContextPath() + "/pedido-concluido");
        } catch (IllegalArgumentException e) {
            renderizarCheckout(request, response,
                    HttpServletResponse.SC_CONFLICT, e.getMessage());
        } catch (IllegalStateException e) {
            getServletContext().log("Banco indisponível no checkout.", e);
            renderizarCheckout(request, response,
                    HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "Banco de dados indisponível. Tente novamente.");
        } catch (SQLException e) {
            getServletContext().log("Falha ao gravar pedido.", e);
            renderizarCheckout(request, response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Não foi possível concluir o pedido.");
        }
    }

    private String validarEndereco(
            String destinatario,
            String telefone,
            String cep,
            String endereco,
            String numero,
            String cidade,
            String estado) {
        if (vazioOuMaior(destinatario, 120)
                || vazioOuMaior(telefone, 30)
                || vazioOuMaior(cep, 9)
                || !cep.matches("[0-9]{5}-?[0-9]{3}")
                || vazioOuMaior(endereco, 180)
                || vazioOuMaior(numero, 20)
                || vazioOuMaior(cidade, 100)
                || estado == null || !estado.matches("(?i)[a-z]{2}")) {
            return "Confira os dados obrigatórios do endereço de entrega.";
        }
        if (telefone.length() < 8) {
            return "Informe um telefone válido para a entrega.";
        }
        return null;
    }

    private boolean vazioOuMaior(String valor, int limite) {
        return valor == null || valor.isBlank() || valor.length() > limite;
    }

    private void renderizarCheckout(
            HttpServletRequest request,
            HttpServletResponse response,
            int status,
            String erro) throws ServletException, IOException {
        BigDecimal total = BigDecimal.ZERO;
        Carrinho carrinho = carrinho(request);
        if (carrinho != null) {
            try {
                for (Map.Entry<Long, Integer> item
                        : carrinho.getItens().entrySet()) {
                    Produto produto = produtoDAO.buscarPorId(item.getKey());
                    if (produto != null && produto.getPreco() != null) {
                        total = total.add(produto.getPreco().multiply(
                                BigDecimal.valueOf(item.getValue())));
                    }
                }
            } catch (SQLException | IllegalStateException e) {
                getServletContext().log("Falha ao calcular total do pedido.", e);
                status = HttpServletResponse.SC_SERVICE_UNAVAILABLE;
                erro = "Banco de dados indisponível. Tente novamente.";
            }
        }
        request.setAttribute("totalCheckout", total);
        request.setAttribute("erro", erro);
        response.setStatus(status);
        request.getRequestDispatcher("/checkout.jsp").forward(request, response);
    }

    private void exibirConfirmacao(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession sessao = request.getSession(false);
        Object pedidoId = sessao == null
                ? null : sessao.getAttribute("ultimoPedidoId");
        if (pedidoId == null) {
            response.sendRedirect(request.getContextPath() + "/produtos");
            return;
        }
        request.setAttribute("pedidoId", pedidoId);
        request.getRequestDispatcher("/pedido-concluido.jsp")
                .forward(request, response);
    }

    private Usuario usuarioLogado(HttpServletRequest request) {
        HttpSession sessao = request.getSession(false);
        if (sessao == null) return null;
        Object usuario = sessao.getAttribute("usuario");
        return usuario instanceof Usuario ? (Usuario) usuario : null;
    }

    private Carrinho carrinho(HttpServletRequest request) {
        HttpSession sessao = request.getSession(false);
        if (sessao == null) return null;
        Object carrinho = sessao.getAttribute("carrinho");
        return carrinho instanceof Carrinho ? (Carrinho) carrinho : null;
    }

    private String valor(String texto) {
        return texto == null ? null : texto.trim();
    }
}