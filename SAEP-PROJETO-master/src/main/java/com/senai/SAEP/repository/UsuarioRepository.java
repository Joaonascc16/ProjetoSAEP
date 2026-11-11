package com.senai.SAEP.repository;

import com.senai.SAEP.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    Optional<UsuarioEntity> findByLoginAndSenha(String login, String senha);
}
