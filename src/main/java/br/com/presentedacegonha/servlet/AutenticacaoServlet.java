package br.com.presentedacegonha.servlet;

import br.com.presentedacegonha.dao.UsuarioDAO;
import br.com.presentedacegonha.model.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.regex.Pattern;

@WebServlet(urlPatterns = {"/login", "/cadastro", "/conta", "/logout"})
public class AutenticacaoServlet extends HttpServlet {

    private static final Pattern EMAIL_VALIDO = Pattern.compile(
            "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private UsuarioDAO usuarioDAO;

    @Override
    public void init() {
        usuarioDAO = new UsuarioDAO();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        switch (request.getServletPath()) {
            case "/login" -> {
                if (usuarioLogado(request) != null) {
                    redirecionar(request, response, "/conta");
                } else {
                    request.getRequestDispatcher("/login.jsp")
                            .forward(request, response);
                }
            }
            case "/cadastro" -> {
                if (usuarioLogado(request) != null) {
                    redirecionar(request, response, "/conta");
                } else {
                    request.getRequestDispatcher("/cadastro.jsp")
                            .forward(request, response);
                }
            }
            case "/conta" -> exibirConta(request, response);
            case "/logout" -> response.sendError(
                    HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        try {
            switch (request.getServletPath()) {
                case "/login" -> processarLogin(request, response);
                case "/cadastro" -> processarCadastro(request, response);
                case "/logout" -> processarLogout(request, response);
                default -> response.sendError(
                        HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (IllegalStateException e) {
            getServletContext().log("Configuração do banco indisponível.", e);
            exibirErroBanco(request, response);
        } catch (SQLException e) {
            getServletContext().log("Falha no fluxo de autenticação.", e);
            exibirErroBanco(request, response);
        }
    }

    private void processarLogin(
            HttpServletRequest request,
            HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String email = normalizarEmail(request.getParameter("email"));
        String senha = request.getParameter("senha");

        if (email == null || senha == null || senha.isEmpty()) {
            exibirLoginComErro(request, response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Informe e-mail e senha.");
            return;
        }

        Usuario usuario = usuarioDAO.autenticar(email, senha);
        if (usuario == null) {
            exibirLoginComErro(request, response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "E-mail ou senha inválidos.");
            return;
        }

        HttpSession sessao = request.getSession(true);
        request.changeSessionId();
        sessao.setAttribute("usuario", usuario);
        Object destino = sessao.getAttribute("destinoAposLogin");
        sessao.removeAttribute("destinoAposLogin");
        if ("/checkout".equals(destino)) {
            redirecionar(request, response, "/checkout");
            return;
        }
        redirecionar(request, response, "/conta");
    }

    private void processarCadastro(
            HttpServletRequest request,
            HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String nome = valor(request.getParameter("nome"));
        String email = normalizarEmail(request.getParameter("email"));
        String senha = request.getParameter("senha");
        String confirmarSenha = request.getParameter("confirmarSenha");

        String erro = validarCadastro(nome, email, senha, confirmarSenha);
        if (erro != null) {
            exibirCadastroComErro(request, response,
                    HttpServletResponse.SC_BAD_REQUEST, erro, nome, email);
            return;
        }

        if (!usuarioDAO.cadastrar(nome, email, senha)) {
            exibirCadastroComErro(request, response,
                    HttpServletResponse.SC_CONFLICT,
                    "Este e-mail já possui uma conta.", nome, email);
            return;
        }

        redirecionar(request, response, "/login?cadastro=ok");
    }

    private String validarCadastro(
            String nome,
            String email,
            String senha,
            String confirmarSenha) {
        if (nome == null || nome.isBlank() || nome.length() > 120) {
            return "Informe um nome com até 120 caracteres.";
        }
        if (email == null || email.length() > 254
                || !EMAIL_VALIDO.matcher(email).matches()) {
            return "Informe um e-mail válido.";
        }
        if (senha == null || senha.length() < 8 || senha.length() > 256) {
            return "A senha deve ter entre 8 e 256 caracteres.";
        }
        if (!senha.equals(confirmarSenha)) {
            return "As senhas não coincidem.";
        }
        return null;
    }

    private void processarLogout(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        HttpSession sessao = request.getSession(false);
        if (sessao != null) {
            sessao.invalidate();
        }
        redirecionar(request, response, "/login?logout=ok");
    }

    private void exibirConta(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        Usuario usuario = usuarioLogado(request);
        if (usuario == null) {
            redirecionar(request, response, "/login");
            return;
        }

        request.setAttribute("usuario", usuario);
        request.getRequestDispatcher("/conta.jsp").forward(request, response);
    }

    private void exibirErroBanco(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        String servletPath = request.getServletPath();
        if ("/cadastro".equals(servletPath)) {
            exibirCadastroComErro(request, response,
                    HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "Banco de dados indisponível. Confira a configuração do servidor.",
                    valor(request.getParameter("nome")),
                    normalizarEmail(request.getParameter("email")));
        } else {
            exibirLoginComErro(request, response,
                    HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "Banco de dados indisponível. Confira a configuração do servidor.");
        }
    }

    private void exibirLoginComErro(
            HttpServletRequest request,
            HttpServletResponse response,
            int status,
            String erro)
            throws ServletException, IOException {
        request.setAttribute("erro", erro);
        response.setStatus(status);
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    private void exibirCadastroComErro(
            HttpServletRequest request,
            HttpServletResponse response,
            int status,
            String erro,
            String nome,
            String email)
            throws ServletException, IOException {
        request.setAttribute("erro", erro);
        request.setAttribute("nome", nome);
        request.setAttribute("email", email);
        response.setStatus(status);
        request.getRequestDispatcher("/cadastro.jsp").forward(request, response);
    }

    private Usuario usuarioLogado(HttpServletRequest request) {
        HttpSession sessao = request.getSession(false);
        if (sessao == null) {
            return null;
        }
        Object usuario = sessao.getAttribute("usuario");
        return usuario instanceof Usuario ? (Usuario) usuario : null;
    }

    private String normalizarEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    private String valor(String texto) {
        return texto == null ? null : texto.trim();
    }

    private void redirecionar(
            HttpServletRequest request,
            HttpServletResponse response,
            String destino) throws IOException {
        response.sendRedirect(request.getContextPath() + destino);
    }
}