package com.senai.SAEP.repository;

import com.senai.SAEP.entity.ProdutoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<ProdutoEntity, Long> {

    List<ProdutoEntity> findByNomeContainingIgnoreCase(String nome);

    // Bloqueia a linha durante a movimentação para evitar atualização perdida
    // quando duas operações de estoque acontecem simultaneamente.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from ProdutoEntity p where p.id = :id")
    java.util.Optional<ProdutoEntity> findByIdForUpdate(@Param("id") Long id);
}