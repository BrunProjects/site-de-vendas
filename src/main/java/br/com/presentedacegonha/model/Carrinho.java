package br.com.presentedacegonha.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Carrinho {

    private final Map<Long, Integer> itens = new LinkedHashMap<>();

    public synchronized void adicionar(Long produtoId, int quantidade) {
        if (produtoId == null || produtoId <= 0 || quantidade <= 0) {
            throw new IllegalArgumentException("Item do carrinho inválido.");
        }

        itens.merge(produtoId, quantidade, Math::addExact);
    }

    public synchronized void remover(Long produtoId) {
        if (produtoId != null) {
            itens.remove(produtoId);
        }
    }

    public synchronized void limpar() {
        itens.clear();
    }

    public synchronized boolean estaVazio() {
        return itens.isEmpty();
    }

    public synchronized int getQuantidadeTotal() {
        return itens.values().stream().mapToInt(Integer::intValue).sum();
    }

    public synchronized Map<Long, Integer> getItens() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(itens));
    }
}