package com.livrariaregional.web.dto;

import java.math.BigDecimal;

/**
 * Item do carrinho de compras (PDV).
 *
 * POJO puro (sem anotacoes JPA) usado para trafegar entre
 * o form HTML, a sessao HTTP (@SessionAttributes) e o service.
 *
 * Campos:
 *  - produtoId: id do Produto selecionado
 *  - quantidade: unidades desejadas
 *  - precoUnitario: capturado do produto NO MOMENTO da venda (preco vigente)
 *  - subtotal: precoUnitario * quantidade (helper, nao persistido)
 *
 * O precoUnitario e setado pelo VendaService ao instanciar ItemVenda;
 * aqui guardamos para exibir no recibo e calcular subtotal sem
 * precisar recarregar o produto.
 */
public class ItemCarrinho {

    private Long produtoId;
    private Integer quantidade;
    private BigDecimal precoUnitario;

    public ItemCarrinho() {
    }

    public ItemCarrinho(Long produtoId, Integer quantidade, BigDecimal precoUnitario) {
        this.produtoId = produtoId;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public BigDecimal getSubtotal() {
        if (precoUnitario == null || quantidade == null) {
            return BigDecimal.ZERO;
        }
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }
}
