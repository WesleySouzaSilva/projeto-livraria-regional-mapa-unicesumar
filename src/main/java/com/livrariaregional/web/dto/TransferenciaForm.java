package com.livrariaregional.web.dto;

/**
 * Form bean para a transferencia de estoque entre filiais.
 *
 * Apenas IDs — o controller carrega as entidades Filial/Produto pelos
 * repositories antes de chamar EstoqueService.transferir.
 *
 * Campos:
 *  - filialOrigemId: filial de onde sai o estoque
 *  - filialDestinoId: filial que recebe o estoque
 *  - produtoId: produto transferido
 *  - quantidade: unidades a transferir (> 0)
 */
public class TransferenciaForm {

    private Long filialOrigemId;
    private Long filialDestinoId;
    private Long produtoId;
    private Integer quantidade;

    public TransferenciaForm() {
    }

    public Long getFilialOrigemId() {
        return filialOrigemId;
    }

    public void setFilialOrigemId(Long filialOrigemId) {
        this.filialOrigemId = filialOrigemId;
    }

    public Long getFilialDestinoId() {
        return filialDestinoId;
    }

    public void setFilialDestinoId(Long filialDestinoId) {
        this.filialDestinoId = filialDestinoId;
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
}
