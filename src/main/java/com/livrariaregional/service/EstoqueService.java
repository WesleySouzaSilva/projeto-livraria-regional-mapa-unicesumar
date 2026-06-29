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

    /**
     * Debita quantidade do estoque da filial para o produto.
     * Se nao existir registro de Estoque (filial, produto), considera quantidade=0.
     * Lancamento de estoque negativo NAO permitido (ha check explicito).
     *
     * Metodo transacional — quem chama (VendaService.finalizar) ja roda em
     * transacao propria; este metodo usa REQUIRED (default) para aderir a ela.
     */
    @Transactional
    public void debitar(Filial filial, Produto produto, Integer quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade a debitar deve ser positiva: " + quantidade);
        }
        Estoque e = estoqueRepository.findByFilialAndProduto(filial, produto)
                .orElseThrow(() -> new IllegalStateException(
                        "Estoque insuficiente: nao ha registro para produto " + produto.getCodigo()
                                + " na filial " + filial.getNome()));
        if (e.getQuantidade() < quantidade) {
            throw new IllegalStateException(
                    "Estoque insuficiente: disponivel=" + e.getQuantidade()
                            + ", solicitado=" + quantidade
                            + " (produto " + produto.getCodigo() + ", filial " + filial.getNome() + ")");
        }
        e.setQuantidade(e.getQuantidade() - quantidade);
        estoqueRepository.save(e);
    }

    /**
     * Credita (soma) quantidade ao estoque. Cria registro se nao existir.
     */
    @Transactional
    public void creditar(Filial filial, Produto produto, Integer quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade a creditar deve ser positiva: " + quantidade);
        }
        Estoque e = estoqueRepository.findByFilialAndProduto(filial, produto)
                .orElseGet(() -> new Estoque(filial, produto, 0));
        e.setQuantidade(e.getQuantidade() + quantidade);
        estoqueRepository.save(e);
    }

    /**
     * Transfere quantidade entre filiais de forma atomica.
     * - origem e destino devem ser diferentes
     * - estoque na origem deve ser suficiente
     * - cria registro de estoque no destino se nao existir
     *
     * Em caso de falha em qualquer etapa, a transacao outer garante rollback
     * total (debito desfeito + credito desfeito).
     */
    @Transactional
    public void transferir(Filial origem, Filial destino, Produto produto, Integer quantidade) {
        if (origem == null || destino == null) {
            throw new IllegalArgumentException("Filial origem e destino sao obrigatorias");
        }
        if (origem.getId().equals(destino.getId())) {
            throw new IllegalArgumentException("Filial origem e destino devem ser diferentes");
        }
        debitar(origem, produto, quantidade);
        creditar(destino, produto, quantidade);
    }
}