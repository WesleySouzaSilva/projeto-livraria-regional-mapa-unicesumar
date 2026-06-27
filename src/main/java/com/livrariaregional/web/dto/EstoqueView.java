package com.livrariaregional.web.dto;

import com.livrariaregional.domain.Estoque;
import com.livrariaregional.domain.Produto;

/**
 * Projection de Estoque para a view.
 *
 * Por que nao expor a entidade Estoque direto no template?
 *   - Evita que Thymeleaf acesse metodos que nao fazem sentido na UI
 *     (ex.: setters, getId puro).
 *   - Centraliza a regra "estoque critico" aqui, em vez de calcular no HTML.
 *   - Produto pode nao ter registro na tabela estoque para a filial
 *     (filial nova). A view trata ausencia como quantidade=0.
 *
 * O flag "critico" e derivado de quantidade < estoqueMinimo.
 * Isso e regra de UI / negocio basica da PR #5.
 */
public record EstoqueView(
        Long produtoId,
        String produtoCodigo,
        String produtoNome,
        Long filialId,
        String filialNome,
        Integer quantidade,
        Integer estoqueMinimo,
        boolean critico
) {

    public static EstoqueView from(Produto produto, Estoque estoque) {
        int qtd = estoque != null ? estoque.getQuantidade() : 0;
        int min = produto.getEstoqueMinimo() != null ? produto.getEstoqueMinimo() : 0;
        boolean critico = qtd < min;
        return new EstoqueView(
                produto.getId(),
                produto.getCodigo(),
                produto.getNome(),
                estoque != null ? estoque.getFilial().getId() : null,
                estoque != null ? estoque.getFilial().getNome() : "-",
                qtd,
                min,
                critico
        );
    }
}