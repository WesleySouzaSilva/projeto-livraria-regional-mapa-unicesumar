package com.livrariaregional.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Entidade Produto.
 *
 * Modelo de dados (alinhado ao TRABALHO):
 *   id, codigo, nome, categoria, preco
 *
 * Campos adicionais (enriquecimento do MVP):
 *   - autor, isbn: identificacao bibliografica (livro e o principal produto da livraria)
 *   - estoqueMinimo: regra de negocio para alerta de reposicao
 *   - ativo: soft-delete (produto nao some do historico de vendas)
 *
 * Decisao: campo "nome" foi mapeado como "titulo" no banco (column annotation)
 * para manter semantica de livro, mas o getter/setter exposto chama "nome"
 * para casar com o MODELO DE DADOS. Assim o codigo de tela e servico
 * referencia getNome()/setNome() igual ao TRABALHO, sem perder o detalhe
 * bibliografico.
 */
@Entity
@Table(name = "produto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "produto_seq")
    @SequenceGenerator(name = "produto_seq", sequenceName = "produto_seq", allocationSize = 1)
    private Long id;

    @Column(name = "codigo", nullable = false, length = 20, unique = true)
    private String codigo;

    @Column(name = "nome", nullable = false, length = 200)
    private String nome;

    @Column(name = "categoria", length = 60)
    private String categoria;

    @Column(name = "preco", nullable = false, precision = 12, scale = 2)
    private BigDecimal preco;

    // --- campos enriquecidos (alem do MODELO DE DADOS) ---

    @Column(name = "autor", length = 120)
    private String autor;

    @Column(name = "isbn", length = 20)
    private String isbn;

    @Column(name = "estoque_minimo", nullable = false)
    private Integer estoqueMinimo = 5;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    public Produto() {
    }

    public Produto(String codigo, String nome, String categoria, BigDecimal preco) {
        this.codigo = codigo;
        this.nome = nome;
        this.categoria = categoria;
        this.preco = preco;
        this.estoqueMinimo = 5;
        this.ativo = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getEstoqueMinimo() {
        return estoqueMinimo;
    }

    public void setEstoqueMinimo(Integer estoqueMinimo) {
        this.estoqueMinimo = estoqueMinimo;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}