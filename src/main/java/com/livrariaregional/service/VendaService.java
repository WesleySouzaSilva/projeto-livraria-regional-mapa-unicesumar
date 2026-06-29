package com.livrariaregional.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.livrariaregional.domain.Cliente;
import com.livrariaregional.domain.Filial;
import com.livrariaregional.domain.ItemVenda;
import com.livrariaregional.domain.Produto;
import com.livrariaregional.domain.StatusVenda;
import com.livrariaregional.domain.Usuario;
import com.livrariaregional.domain.Venda;
import com.livrariaregional.repository.ClienteRepository;
import com.livrariaregional.repository.ItemVendaRepository;
import com.livrariaregional.repository.ProdutoRepository;
import com.livrariaregional.repository.UsuarioRepository;
import com.livrariaregional.repository.VendaRepository;
import com.livrariaregional.web.dto.ItemCarrinho;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service de Venda (PDV).
 *
 * Responsabilidades:
 *  - Finalizar venda: persistir Venda + Itens + baixar estoque da filial
 *    em UMA unica transacao (atomicidade).
 *  - Listar vendas por usuario (historico do atendente).
 *
 * Regras:
 *  - precoUnitario e capturado do produto no momento da venda (preco vigente).
 *    Futuras alteracoes de preco nao retroagem em vendas antigas.
 *  - Se qualquer item nao puder ser debitado do estoque, a venda inteira
 *    sofre rollback (via @Transactional).
 */
@Service
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ItemVendaRepository itemVendaRepository;
    private final EstoqueService estoqueService;
    private final ProdutoRepository produtoRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;

    public VendaService(VendaRepository vendaRepository,
                        ItemVendaRepository itemVendaRepository,
                        EstoqueService estoqueService,
                        ProdutoRepository produtoRepository,
                        ClienteRepository clienteRepository,
                        UsuarioRepository usuarioRepository) {
        this.vendaRepository = vendaRepository;
        this.itemVendaRepository = itemVendaRepository;
        this.estoqueService = estoqueService;
        this.produtoRepository = produtoRepository;
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Finaliza uma venda: cria a Venda, seus Itens e baixa o estoque.
     *
     * @param filial filial onde a venda ocorre (== filial do atendente)
     * @param usuario atendente logado
     * @param cliente cliente (nullable). Se null, venda sem cliente identificado.
     * @param itens carrinho com produtoId + quantidade
     * @return Venda persistida (com id)
     */
    @Transactional
    public Venda finalizar(Filial filial,
                            Usuario usuario,
                            Cliente cliente,
                            List<ItemCarrinho> itens) {
        if (filial == null) {
            throw new IllegalArgumentException("Filial obrigatoria");
        }
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario obrigatorio");
        }
        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("Carrinho vazio");
        }

        Venda venda = new Venda(filial, cliente, usuario);
        venda.setDataVenda(LocalDateTime.now());
        venda.setStatus(StatusVenda.CONCLUIDA);
        venda.setTotal(BigDecimal.ZERO);

        BigDecimal total = BigDecimal.ZERO;
        for (ItemCarrinho item : itens) {
            Produto produto = produtoRepository.findById(item.getProdutoId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Produto nao encontrado: id=" + item.getProdutoId()));

            if (item.getQuantidade() == null || item.getQuantidade() <= 0) {
                throw new IllegalArgumentException(
                        "Quantidade invalida para produto " + produto.getCodigo());
            }

            // precoUnitario capturado AGORA (preco vigente)
            BigDecimal precoUnitario = produto.getPreco();
            ItemVenda iv = new ItemVenda(venda, produto, item.getQuantidade(), precoUnitario);
            venda.getItens().add(iv);

            // Baixar estoque ANTES de contar o total. Se falhar, rollback.
            estoqueService.debitar(filial, produto, item.getQuantidade());

            total = total.add(iv.getSubtotal());
        }
        venda.setTotal(total);

        return vendaRepository.save(venda);
    }

    @Transactional(readOnly = true)
    public List<Venda> listarPorUsuario(Long usuarioId) {
        return vendaRepository.findByUsuarioIdOrderByDataVendaDesc(usuarioId);
    }

    @Transactional(readOnly = true)
    public Venda buscarPorId(Long id) {
        return vendaRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Venda nao encontrada: id=" + id));
    }
}
