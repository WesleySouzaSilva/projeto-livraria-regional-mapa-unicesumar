package com.livrariaregional.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entidade Filial.
 *
 * Modelo de dados (alinhado ao TRABALHO):
 *   id, nome, endereco
 *
 * Campos adicionais (enriquecimento do MVP):
 *   - ativo: soft-delete (filial nao pode ser excluida se tem vendas)
 *
 * Decisao: campo "telefone" foi removido na update 4.1 para casar
 * exatamente com o MODELO DE DADOS. Telefone de filial raramente e
 * usado no PDV (cliente liga direto pra loja). Pode voltar via
 * nova PR se o cliente pedir.
 */
@Entity
@Table(name = "filial")
public class Filial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String nome;

    @Column(nullable = false, length = 200)
    private String endereco;

    @Column(nullable = false)
    private Boolean ativo = true;

    public Filial() {
    }

    public Filial(String nome, String endereco) {
        this.nome = nome;
        this.endereco = endereco;
        this.ativo = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}