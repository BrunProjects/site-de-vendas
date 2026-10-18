package br.com.presentedacegonha.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CarrinhoTest {

    @Test
    public void deveSomarUnidadesEPermitirRemocao() {
        Carrinho carrinho = new Carrinho();
        carrinho.adicionar(10L, 2);
        carrinho.adicionar(10L, 3);
        carrinho.adicionar(11L, 1);

        assertEquals(6, carrinho.getQuantidadeTotal());
        carrinho.remover(10L);
        assertEquals(Integer.valueOf(1), carrinho.getItens().get(11L));
        assertEquals(1, carrinho.getQuantidadeTotal());
    }

    @Test
    public void deveEsvaziarOCarrinho() {
        Carrinho carrinho = new Carrinho();
        carrinho.adicionar(10L, 1);
        carrinho.limpar();

        assertTrue(carrinho.estaVazio());
    }
}