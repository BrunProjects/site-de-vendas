package br.com.presentedacegonha.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.UUID;

import org.junit.Test;

import br.com.presentedacegonha.config.DatabaseConnection;
import br.com.presentedacegonha.model.Produto;

public class ProdutoDAOTest {

    @Test
    public void deveListarProdutosDoBanco() throws Exception {
        List<Produto> produtos = new ProdutoDAO().listarTodos();

        assertNotNull("A consulta deve retornar uma lista.", produtos);
    }

    @Test
    public void deveInserirProdutoDentroDeTransacaoRevertida() throws Exception {
        String sql = """
                INSERT INTO produtos
                    (categoria_id, nome, descricao, preco,
                     estoque, imagem_url, ativo)
                VALUES (1, ?, NULL, 0.00, 0, NULL, TRUE)
                RETURNING id
                """;

        try (Connection conexao = DatabaseConnection.getConnection()) {
            conexao.setAutoCommit(false);
            try {
                try (PreparedStatement comando = conexao.prepareStatement(sql)) {
                    comando.setString(1, "teste-conexao-" + UUID.randomUUID());
                    try (ResultSet resultado = comando.executeQuery()) {
                        assertTrue(
                                "O INSERT deve retornar o ID do produto.",
                                resultado.next());
                        assertTrue("O ID gerado deve ser positivo.",
                                resultado.getLong("id") > 0);
                    }
                }
            } finally {
                conexao.rollback();
            }
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void deveRejeitarCadastroSemNome() throws Exception {
        Produto produto = new Produto();
        produto.setCategoriaId(1L);
        produto.setPreco(java.math.BigDecimal.ZERO);

        new ProdutoDAO().cadastrar(produto);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deveRejeitarIdInvalido() throws Exception {
        new ProdutoDAO().buscarPorId(0L);
    }
}