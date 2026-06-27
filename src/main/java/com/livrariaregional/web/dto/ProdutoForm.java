package com.livrariaregional.web.dto;

import com.livrariaregional.domain.Produto;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Form de cadastro/edicao de Produto.
 *
 * Usado pelo ProdutoController em /dashboard/produtos/novo e
 * /dashboard/produtos/{id}/editar. Mantem a forma minima do MODELO DE DADOS
 * (codigo, nome, categoria, preco) mais os campos enriquecidos do MVP
 * (autor, isbn, estoqueMinimo).
 *
 * O Bean Validation garante preco > 0 e estoqueMinimo >= 0 antes de chegar
 * ao Service. A unicidade do codigo e checada no Service (captura
 * DataIntegrityViolationException do banco).
 */
public class ProdutoForm {

    @NotBlank
    @Size(max = 20)
    private String codigo;

    @NotBlank
    @Size(max = 200)
    private String nome;

    @Size(max = 120)
    private String autor;

    @Size(max = 20)
    private String isbn;

    @Size(max = 60)
    private String categoria;

    @NotNull
    @DecimalMin(value = "0.01", message = "Preco deve ser maior que zero")
    private BigDecimal preco;

    @NotNull
    @Min(value = 0, message = "Estoque minimo nao pode ser negativo")
    private Integer estoqueMinimo;

    public ProdutoForm() {
    }

    public static ProdutoForm fromEntity(Produto p) {
        ProdutoForm f = new ProdutoForm();
        f.codigo = p.getCodigo();
        f.nome = p.getNome();
        f.autor = p.getAutor();
        f.isbn = p.getIsbn();
        f.categoria = p.getCategoria();
        f.preco = p.getPreco();
        f.estoqueMinimo = p.getEstoqueMinimo();
        return f;
    }

    public void applyTo(Produto p) {
        p.setCodigo(this.codigo);
        p.setNome(this.nome);
        p.setAutor(this.autor);
        p.setIsbn(this.isbn);
        p.setCategoria(this.categoria);
        p.setPreco(this.preco);
        p.setEstoqueMinimo(this.estoqueMinimo);
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public Integer getEstoqueMinimo() { return estoqueMinimo; }
    public void setEstoqueMinimo(Integer estoqueMinimo) { this.estoqueMinimo = estoqueMinimo; }
}