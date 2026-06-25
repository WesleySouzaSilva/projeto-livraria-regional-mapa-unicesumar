package com.livrariaregional.repository;

import java.util.List;

import com.livrariaregional.domain.Filial;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilialRepository extends JpaRepository<Filial, Long> {

    List<Filial> findByAtivoTrueOrderByNomeAsc();
}