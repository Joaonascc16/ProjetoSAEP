package com.senai.SAEP.repository;

import com.senai.SAEP.entity.MovimentacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimentacaoRepository
        extends JpaRepository<MovimentacaoEntity, Long> {

    List<MovimentacaoEntity> findAllByOrderByDataDesc();

    List<MovimentacaoEntity> findByProdutoIdOrderByDataDesc(Long produtoId);
}