package com.livrariaregional.repository;

import java.util.List;

import com.livrariaregional.domain.Produto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByAtivoTrueOrderByTituloAsc();

    List<Produto> findByTituloContainingIgnoreCaseAndAtivoTrue(String trecho);
}