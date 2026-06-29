package com.livrariaregional.web.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper do carrinho de compras para uso com @SessionAttributes.
 *
 * O Spring MVC usa o objeto referenciado por @SessionAttributes("carrinho")
 * para persistir estado entre requests sem precisar de HttpSession manual.
 *
 * O carrinho contem apenas os identificadores e quantidades (ItemCarrinho).
 * Nomes, precos etc. sao resolvidos na renderizacao do template via
 * lookup em mapa produtoId -> Produto (carregado pelo controller).
 *
 * Helper total() soma subtotais para exibicao no rodape.
 */
public class CarrinhoView {

    private List<ItemCarrinho> itens = new ArrayList<>();

    public CarrinhoView() {
    }

    public List<ItemCarrinho> getItens() {
        return itens;
    }

    public void setItens(List<ItemCarrinho> itens) {
        this.itens = itens;
    }

    /**
     * Adiciona um item novo ou soma a quantidade se o produto ja estiver no carrinho.
     */
    public void addItem(Long produtoId, Integer quantidade, BigDecimal precoUnitario) {
        for (ItemCarrinho item : itens) {
            if (item.getProdutoId().equals(produtoId)) {
                item.setQuantidade(item.getQuantidade() + quantidade);
                return;
            }
        }
        itens.add(new ItemCarrinho(produtoId, quantidade, precoUnitario));
    }

    public void remover(int index) {
        if (index < 0 || index >= itens.size()) {
            return;
        }
        itens.remove(index);
    }

    public boolean isVazio() {
        return itens == null || itens.isEmpty();
    }

    public BigDecimal total() {
        BigDecimal soma = BigDecimal.ZERO;
        for (ItemCarrinho item : itens) {
            soma = soma.add(item.getSubtotal());
        }
        return soma;
    }
}
