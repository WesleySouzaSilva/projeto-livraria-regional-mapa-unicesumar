package com.livrariaregional.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.livrariaregional.domain.Cliente;
import com.livrariaregional.domain.Filial;
import com.livrariaregional.domain.ItemVenda;
import com.livrariaregional.domain.Usuario;
import com.livrariaregional.domain.Venda;

/**
 * View de Venda para o recibo e o historico.
 *
 * Encapsula a Venda + Filial + Usuario + Cliente (opcional) + Itens para
 * que o template nao precise navegar pelas relacoes lazy do JPA.
 *
 * O metodo estatico from() faz o snapshot a partir da Venda persistida,
 * forçando a inicializacao dos itens (venda.getItens()) DENTRO da
 * transacao do service para evitar LazyInitializationException no template.
 */
public class VendaView {

    private Long id;
    private LocalDateTime dataVenda;
    private Filial filial;
    private Usuario usuario;
    private Cliente cliente;
    private List<ItemVenda> itens;
    private BigDecimal total;
    private String status;

    public VendaView() {
    }

    public static VendaView from(Venda venda) {
        VendaView v = new VendaView();
        v.id = venda.getId();
        v.dataVenda = venda.getDataVenda();
        v.filial = venda.getFilial();
        v.usuario = venda.getUsuario();
        v.cliente = venda.getCliente();
        // Snapshot dos itens: garante inicializacao enquanto a sessao JPA esta aberta
        v.itens = new ArrayList<>(venda.getItens());
        v.total = venda.getTotal();
        v.status = venda.getStatus() != null ? venda.getStatus().name() : null;
        return v;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(LocalDateTime dataVenda) {
        this.dataVenda = dataVenda;
    }

    public Filial getFilial() {
        return filial;
    }

    public void setFilial(Filial filial) {
        this.filial = filial;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<ItemVenda> getItens() {
        return itens;
    }

    public void setItens(List<ItemVenda> itens) {
        this.itens = itens;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
