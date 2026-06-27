package com.livrariaregional.service;

import com.livrariaregional.domain.Produto;
import com.livrariaregional.repository.ProdutoRepository;
import com.livrariaregional.web.dto.ProdutoForm;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service de Produto.
 *
 * Regras de negocio concentradas aqui:
 *   - Listar apenas produtos ativos (soft-delete respeitado)
 *   - Busca por nome case-insensitive
 *   - Codigo unico (constraint no banco; aqui capturamos a excecao
 *     para devolver mensagem amigavel ao controller)
 *   - Desativar preserva o historico (ativo=false, nao remove)
 */
@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @Transactional(readOnly = true)
    public List<Produto> listarAtivos() {
        return produtoRepository.findByAtivoTrueOrderByNomeAsc();
    }

    @Transactional(readOnly = true)
    public List<Produto> buscarPorNome(String trecho) {
        if (trecho == null || trecho.isBlank()) {
            return listarAtivos();
        }
        return produtoRepository.findByNomeContainingIgnoreCaseAndAtivoTrue(trecho.trim());
    }

    @Transactional(readOnly = true)
    public Optional<Produto> obter(Long id) {
        return produtoRepository.findById(id);
    }

    /**
     * Cria um novo produto a partir do form. Retorna o produto persistido.
     *
     * @throws CodigoDuplicadoException se o codigo ja existir (UNIQUE violation)
     */
    @Transactional
    public Produto criar(ProdutoForm form) {
        Produto p = new Produto();
        form.applyTo(p);
        p.setAtivo(true);
        try {
            // saveAndFlush() força o INSERT agora (dentro do try) - sem ele o
            // Hibernate defere para o commit e a DataIntegrityViolationException
            // escapa do catch.
            return produtoRepository.saveAndFlush(p);
        } catch (DataIntegrityViolationException ex) {
            throw new CodigoDuplicadoException(form.getCodigo());
        }
    }

    @Transactional
    public Produto atualizar(Long id, ProdutoForm form) {
        Produto p = produtoRepository.findById(id)
                .orElseThrow(() -> new ProdutoNaoEncontradoException(id));
        form.applyTo(p);
        try {
            return produtoRepository.saveAndFlush(p);
        } catch (DataIntegrityViolationException ex) {
            throw new CodigoDuplicadoException(form.getCodigo());
        }
    }

    @Transactional
    public void desativar(Long id) {
        Produto p = produtoRepository.findById(id)
                .orElseThrow(() -> new ProdutoNaoEncontradoException(id));
        p.setAtivo(false);
        produtoRepository.save(p);
    }

    /** Excecao de codigo duplicado - mapeada para 400 com mensagem amigavel. */
    public static class CodigoDuplicadoException extends RuntimeException {
        public CodigoDuplicadoException(String codigo) {
            super("Ja existe um produto com o codigo " + codigo);
        }
    }

    /** Excecao de produto nao encontrado - mapeada para 404. */
    public static class ProdutoNaoEncontradoException extends RuntimeException {
        public ProdutoNaoEncontradoException(Long id) {
            super("Produto " + id + " nao encontrado");
        }
    }
}