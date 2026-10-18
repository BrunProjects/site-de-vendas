package br.com.presentedacegonha.dao;

import br.com.presentedacegonha.config.DatabaseConnection;
import br.com.presentedacegonha.model.Produto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    public List<Produto> listarTodos() throws SQLException {
        String sql = """
            SELECT id, categoria_id, nome, descricao,
                   preco, estoque, imagem_url, ativo
            FROM produtos
            ORDER BY id DESC
            """;

        List<Produto> produtos = new ArrayList<>();

        try (Connection conexao = DatabaseConnection.getConnection();
             PreparedStatement comando = conexao.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {

            while (resultado.next()) {
                produtos.add(mapearProduto(resultado));
            }
        }

        return produtos;
    }

    public Produto buscarPorId(Long id) throws SQLException {
        validarId(id);

        String sql = """
            SELECT id, categoria_id, nome, descricao,
                   preco, estoque, imagem_url, ativo
            FROM produtos
            WHERE id = ?
            """;

        try (Connection conexao = DatabaseConnection.getConnection();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setLong(1, id);

            try (ResultSet resultado = comando.executeQuery()) {
                if (resultado.next()) {
                    return mapearProduto(resultado);
                }
            }
        }

        return null;
    }

    public void cadastrar(Produto produto) throws SQLException {
        validarProduto(produto);

        String sql = """
            INSERT INTO produtos
                (categoria_id, nome, descricao, preco,
                 estoque, imagem_url, ativo)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conexao = DatabaseConnection.getConnection();
             PreparedStatement comando = conexao.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            preencherComando(comando, produto);
            comando.executeUpdate();

            try (ResultSet chaves = comando.getGeneratedKeys()) {
                if (chaves.next()) {
                    produto.setId(chaves.getLong(1));
                }
            }
        }
    }

    public boolean atualizar(Produto produto) throws SQLException {
        validarProduto(produto);
        validarId(produto.getId());

        String sql = """
            UPDATE produtos
            SET categoria_id = ?, nome = ?, descricao = ?,
                preco = ?, estoque = ?, imagem_url = ?, ativo = ?
            WHERE id = ?
            """;

        try (Connection conexao = DatabaseConnection.getConnection();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            preencherComando(comando, produto);
            comando.setLong(8, produto.getId());

            return comando.executeUpdate() > 0;
        }
    }

    public boolean excluir(Long id) throws SQLException {
        validarId(id);

        String sql = "DELETE FROM produtos WHERE id = ?";

        try (Connection conexao = DatabaseConnection.getConnection();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setLong(1, id);
            return comando.executeUpdate() > 0;
        }
    }

    private void preencherComando(
            PreparedStatement comando,
            Produto produto) throws SQLException {

        comando.setLong(1, produto.getCategoriaId());
        comando.setString(2, produto.getNome());
        comando.setString(3, produto.getDescricao());
        comando.setBigDecimal(4, produto.getPreco());
        comando.setInt(5, produto.getEstoque());
        comando.setString(6, produto.getImagemUrl());
        comando.setBoolean(7, produto.isAtivo());
    }

    private void validarProduto(Produto produto) {
        if (produto == null) {
            throw new IllegalArgumentException("Produto obrigatório.");
        }

        if (produto.getCategoriaId() == null || produto.getCategoriaId() <= 0
                || produto.getNome() == null || produto.getNome().isBlank()
                || produto.getNome().trim().length() > 150
                || produto.getPreco() == null || produto.getPreco().signum() < 0
                || produto.getEstoque() < 0) {
            throw new IllegalArgumentException("Dados do produto inválidos.");
        }
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID do produto inválido.");
        }
    }

    private Produto mapearProduto(ResultSet resultado)
            throws SQLException {

        Produto produto = new Produto();

        produto.setId(resultado.getLong("id"));
        produto.setCategoriaId(resultado.getLong("categoria_id"));
        produto.setNome(resultado.getString("nome"));
        produto.setDescricao(resultado.getString("descricao"));
        produto.setPreco(resultado.getBigDecimal("preco"));
        produto.setEstoque(resultado.getInt("estoque"));
        produto.setImagemUrl(resultado.getString("imagem_url"));
        produto.setAtivo(resultado.getBoolean("ativo"));

        return produto;
    }
}

