package com.senai.SAEP.service;

import com.senai.SAEP.entity.UsuarioEntity;
import com.senai.SAEP.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Optional<UsuarioEntity> autenticar(String login, String senha) {
        return usuarioRepository.findByLoginAndSenha(login, senha);
    }
}
