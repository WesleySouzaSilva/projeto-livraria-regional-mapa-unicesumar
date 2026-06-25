package com.livrariaregional.repository;

import java.util.List;
import java.util.Optional;

import com.livrariaregional.domain.Estoque;
import com.livrariaregional.domain.Filial;
import com.livrariaregional.domain.Produto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, Long> {

    List<Estoque> findByFilialOrderByProdutoNomeAsc(Filial filial);

    Optional<Estoque> findByFilialAndProduto(Filial filial, Produto produto);
}