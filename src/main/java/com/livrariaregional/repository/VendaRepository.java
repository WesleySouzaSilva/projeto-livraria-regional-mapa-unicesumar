package com.livrariaregional.repository;

import java.util.List;

import com.livrariaregional.domain.Venda;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {

    List<Venda> findByUsuarioIdOrderByDataVendaDesc(Long usuarioId);
}