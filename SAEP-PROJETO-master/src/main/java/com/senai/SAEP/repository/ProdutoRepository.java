package com.senai.SAEP.repository;

import com.senai.SAEP.entity.ProdutoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<ProdutoEntity, Long> {

    List<ProdutoEntity> findByNomeContainingIgnoreCase(String nome);
}