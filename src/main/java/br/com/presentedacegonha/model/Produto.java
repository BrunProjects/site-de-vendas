
package br.com.presentedacegonha.model;

import java.math.BigDecimal;

public class Produto {

    private Long id;
    private Long categoriaId;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private int estoque;
    private String imagemUrl;
    private boolean ativo;

    public Produto() {
    }

    public Produto(
            Long categoriaId,
            String nome,
            String descricao,
            BigDecimal preco,
            int estoque,
            String imagemUrl,
            boolean ativo) {
        this.categoriaId = categoriaId;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.estoque = estoque;
        this.imagemUrl = imagemUrl;
        this.ativo = ativo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public int getEstoque() {
        return estoque;
    }

    public void setEstoque(int estoque) {
        this.estoque = estoque;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
