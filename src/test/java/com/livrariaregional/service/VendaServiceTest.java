package com.livrariaregional.service;

import com.livrariaregional.domain.Filial;
import com.livrariaregional.domain.Produto;
import com.livrariaregional.domain.Usuario;
import com.livrariaregional.domain.Venda;
import com.livrariaregional.repository.ClienteRepository;
import com.livrariaregional.repository.ItemVendaRepository;
import com.livrariaregional.repository.ProdutoRepository;
import com.livrariaregional.repository.UsuarioRepository;
import com.livrariaregional.repository.VendaRepository;
import com.livrariaregional.web.dto.ItemCarrinho;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes unitarios (Mockito puro, sem Spring) do VendaService.
 *
 * Foco nas regras de negocio:
 *  1. precoUnitario e capturado AGORA do produto (preco vigente)
 *  2. atomicidade: se EstoqueService.debitar lanca, a venda NAO e persistida
 *  3. validacoes de entrada (filial/usuario/carrinho obrigatorios)
 */
@ExtendWith(MockitoExtension.class)
class VendaServiceTest {

    @Mock private VendaRepository vendaRepository;
    @Mock private ItemVendaRepository itemVendaRepository;
    @Mock private EstoqueService estoqueService;
    @Mock private ProdutoRepository produtoRepository;
    @Mock private ClienteRepository clienteRepository;
    @Mock private UsuarioRepository usuarioRepository;

    @InjectMocks private VendaService vendaService;

    private Filial filial;
    private Usuario usuario;
    private Produto p1;
    private Produto p2;

    @BeforeEach
    void setUp() {
        filial = new Filial("Centro", "Rua X, 100");
        filial.setId(1L);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setLogin("atendente1");
        usuario.setNome("Atendente");

        p1 = new Produto();
        p1.setId(10L);
        p1.setCodigo("LV001");
        p1.setNome("Livro A");
        p1.setPreco(new BigDecimal("25.00"));

        p2 = new Produto();
        p2.setId(11L);
        p2.setCodigo("LV002");
        p2.setNome("Livro B");
        p2.setPreco(new BigDecimal("40.00"));
    }

    @Test
    void finalizarVendaCom2ItensCalculaTotalECapturaPrecoVigente() {
        when(produtoRepository.findById(10L)).thenReturn(Optional.of(p1));
        when(produtoRepository.findById(11L)).thenReturn(Optional.of(p2));
        // Estoque debita OK (no-op neste mock)
        when(vendaRepository.save(any(Venda.class))).thenAnswer(inv -> {
            Venda v = inv.getArgument(0);
            v.setId(99L);
            return v;
        });

        List<ItemCarrinho> itens = List.of(
                new ItemCarrinho(10L, 2, null),
                new ItemCarrinho(11L, 1, null));

        Venda venda = vendaService.finalizar(filial, usuario, null, itens);

        // Total: 2 * 25 + 1 * 40 = 90
        assertThat(venda.getTotal()).isEqualByComparingTo("90.00");
        assertThat(venda.getItens()).hasSize(2);
        // precoUnitario foi capturado AGORA do produto (preco vigente)
        assertThat(venda.getItens().get(0).getPrecoUnitario()).isEqualByComparingTo("25.00");
        assertThat(venda.getItens().get(1).getPrecoUnitario()).isEqualByComparingTo("40.00");
        // Estoque foi debitado 2x
        verify(estoqueService, times(2)).debitar(any(), any(), any(Integer.class));
        verify(vendaRepository, times(1)).save(any(Venda.class));
    }

    @Test
    void seEstoqueServiceFalharVendaNaoEPersistida() {
        when(produtoRepository.findById(10L)).thenReturn(Optional.of(p1));
        // debitar lanca — simula "estoque insuficiente" no segundo item
        when(produtoRepository.findById(11L)).thenReturn(Optional.of(p2));
        // Cenario: primeiro item debita OK, segundo item lanca IllegalStateException
        // Para isso o mock do debitar lanca na segunda chamada
        org.mockito.Mockito.doNothing()
                .doThrow(new IllegalStateException("Estoque insuficiente"))
                .when(estoqueService).debitar(any(), any(), any(Integer.class));

        List<ItemCarrinho> itens = List.of(
                new ItemCarrinho(10L, 2, null),
                new ItemCarrinho(11L, 1, null));

        assertThatThrownBy(() -> vendaService.finalizar(filial, usuario, null, itens))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Estoque insuficiente");

        // Venda NAO foi persistida (rollback implicito pelo @Transactional + exception)
        verify(vendaRepository, never()).save(any(Venda.class));
    }

    @Test
    void filialObrigatoria() {
        assertThatThrownBy(() ->
                vendaService.finalizar(null, usuario, null, List.of(new ItemCarrinho(1L, 1, null))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Filial");
    }

    @Test
    void usuarioObrigatorio() {
        assertThatThrownBy(() ->
                vendaService.finalizar(filial, null, null, List.of(new ItemCarrinho(1L, 1, null))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Usuario");
    }

    @Test
    void carrinhoVazioRejeitado() {
        assertThatThrownBy(() ->
                vendaService.finalizar(filial, usuario, null, List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Carrinho");
    }

    @Test
    void quantidadeInvalidaRejeitada() {
        when(produtoRepository.findById(10L)).thenReturn(Optional.of(p1));
        assertThatThrownBy(() ->
                vendaService.finalizar(filial, usuario, null,
                        List.of(new ItemCarrinho(10L, 0, null))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantidade");
        verify(vendaRepository, never()).save(any(Venda.class));
    }
}
