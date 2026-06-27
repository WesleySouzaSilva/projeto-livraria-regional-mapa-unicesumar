package com.livrariaregional.service;

import com.livrariaregional.domain.Estoque;
import com.livrariaregional.domain.Filial;
import com.livrariaregional.domain.Produto;
import com.livrariaregional.repository.EstoqueRepository;
import com.livrariaregional.repository.ProdutoRepository;
import com.livrariaregional.web.dto.EstoqueView;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service de Estoque.
 *
 * Para a view, precisamos de uma linha por (produto, filial). Quando o
 * registro na tabela estoque nao existe (produto novo nunca foi para a
 * filial), representamos como quantidade=0 - isso e importante para a UX
 * do gerente na hora de cadastrar um livro novo e ver "0 unidades" em
 * cada filial em vez de simplesmente omitir a linha.
 *
 * O criterio de "estoque critico" (quantidade < estoqueMinimo) e
 * encapsulado em EstoqueView.from(), de modo que o template so renderiza.
 */
@Service
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;
    private final ProdutoRepository produtoRepository;

    public EstoqueService(EstoqueRepository estoqueRepository,
                          ProdutoRepository produtoRepository) {
        this.estoqueRepository = estoqueRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional(readOnly = true)
    public List<EstoqueView> listarPorFilial(Filial filial) {
        List<Produto> produtos = produtoRepository.findByAtivoTrueOrderByNomeAsc();
        List<EstoqueView> out = new ArrayList<>();
        for (Produto p : produtos) {
            Optional<Estoque> est = estoqueRepository.findByFilialAndProduto(filial, p);
            out.add(EstoqueView.from(p, est.orElse(null)));
        }
        return out;
    }

    @Transactional(readOnly = true)
    public List<EstoqueView> listarTodas() {
        List<Produto> produtos = produtoRepository.findByAtivoTrueOrderByNomeAsc();
        List<EstoqueView> out = new ArrayList<>();
        for (Produto p : produtos) {
            // Sem filial especifica -> criamos uma EstoqueView "somando" todas
            // filiais. Para a PR #5, listamos por filial individualmente;
            // o dashboard agrega em PR #7.
            for (Estoque e : estoqueRepository.findAll()) {
                if (e.getProduto().getId().equals(p.getId())) {
                    out.add(EstoqueView.from(p, e));
                }
            }
        }
        return out;
    }

    @Transactional(readOnly = true)
    public Integer quantidade(Filial filial, Produto produto) {
        return estoqueRepository.findByFilialAndProduto(filial, produto)
                .map(Estoque::getQuantidade)
                .orElse(0);
    }

    @Transactional(readOnly = true)
    public List<EstoqueView> abaixoDoMinimo(Filial filial) {
        return listarPorFilial(filial).stream()
                .filter(EstoqueView::critico)
                .toList();
    }
}