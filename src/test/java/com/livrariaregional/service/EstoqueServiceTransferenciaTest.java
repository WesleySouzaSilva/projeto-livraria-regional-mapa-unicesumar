package com.livrariaregional.service;

import com.livrariaregional.domain.Estoque;
import com.livrariaregional.domain.Filial;
import com.livrariaregional.domain.Produto;
import com.livrariaregional.repository.EstoqueRepository;
import com.livrariaregional.repository.ProdutoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes unitarios (Mockito) das operacoes de transferencia do EstoqueService.
 *
 * Cenarios:
 *  - feliz: transfere 5 unidades de A para B (debita A, credita B)
 *  - erro: estoque origem insuficiente -> destino NAO e alterado
 *  - erro: origem == destino -> IllegalArgumentException
 *  - erro: filial null -> IllegalArgumentException
 *  - erro: quantidade invalida -> IllegalArgumentException
 */
@ExtendWith(MockitoExtension.class)
class EstoqueServiceTransferenciaTest {

    @Mock private EstoqueRepository estoqueRepository;
    @Mock private ProdutoRepository produtoRepository;

    @InjectMocks private EstoqueService estoqueService;

    private Filial origem;
    private Filial destino;
    private Produto produto;
    private Estoque estoqueOrigem;

    @BeforeEach
    void setUp() {
        origem = new Filial("Centro", "Rua X, 100");
        origem.setId(1L);
        destino = new Filial("Zona Norte", "Av Y, 200");
        destino.setId(2L);

        produto = new Produto();
        produto.setId(10L);
        produto.setCodigo("LV001");
        produto.setNome("Livro A");
        produto.setPreco(new BigDecimal("25.00"));

        estoqueOrigem = new Estoque(origem, produto, 8);
    }

    @Test
    void transfereComSucessoDebitaOrigemECreditaDestino() {
        when(estoqueRepository.findByFilialAndProduto(origem, produto))
                .thenReturn(Optional.of(estoqueOrigem));
        Estoque estoqueDestino = new Estoque(destino, produto, 2);
        when(estoqueRepository.findByFilialAndProduto(destino, produto))
                .thenReturn(Optional.of(estoqueDestino));

        estoqueService.transferir(origem, destino, produto, 5);

        // Origem: 8 -> 3
        assertThat(estoqueOrigem.getQuantidade()).isEqualTo(3);
        // Destino: 2 -> 7
        assertThat(estoqueDestino.getQuantidade()).isEqualTo(7);
        verify(estoqueRepository, times(2)).save(any(Estoque.class));
    }

    @Test
    void creditaDestinoQuandoNaoHaRegistroPrevio() {
        when(estoqueRepository.findByFilialAndProduto(origem, produto))
                .thenReturn(Optional.of(estoqueOrigem));
        when(estoqueRepository.findByFilialAndProduto(destino, produto))
                .thenReturn(Optional.empty()); // nao ha estoque previo

        estoqueService.transferir(origem, destino, produto, 3);

        assertThat(estoqueOrigem.getQuantidade()).isEqualTo(5);
        // 2 saves: 1 do debitar (origem), 1 do creditar (destino novo)
        verify(estoqueRepository, times(2)).save(any(Estoque.class));
    }

    @Test
    void estoqueInsuficienteNaOrigemLancaExceptionEDestinoIntacto() {
        when(estoqueRepository.findByFilialAndProduto(origem, produto))
                .thenReturn(Optional.of(estoqueOrigem));
        Estoque estoqueDestino = new Estoque(destino, produto, 7);

        // Tenta transferir 100, mas origem so tem 8 — destino NAO deve ser tocado
        assertThatThrownBy(() ->
                estoqueService.transferir(origem, destino, produto, 100))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Estoque insuficiente");

        // Origem NAO foi decrementada
        assertThat(estoqueOrigem.getQuantidade()).isEqualTo(8);
        // Destino NAO foi alterado
        assertThat(estoqueDestino.getQuantidade()).isEqualTo(7);
        verify(estoqueRepository, never()).save(any(Estoque.class));
    }

    @Test
    void origemIgualDestinoLancaIllegalArgument() {
        assertThatThrownBy(() ->
                estoqueService.transferir(origem, origem, produto, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("diferentes");
        verify(estoqueRepository, never()).findByFilialAndProduto(any(), any());
    }

    @Test
    void filialNullLancaIllegalArgument() {
        assertThatThrownBy(() ->
                estoqueService.transferir(null, destino, produto, 1))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() ->
                estoqueService.transferir(origem, null, produto, 1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void quantidadeInvalidaLancaIllegalArgument() {
        assertThatThrownBy(() ->
                estoqueService.transferir(origem, destino, produto, 0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() ->
                estoqueService.transferir(origem, destino, produto, -1))
                .isInstanceOf(IllegalArgumentException.class);
        verify(estoqueRepository, never()).save(any(Estoque.class));
    }
}
