package com.senai.SAEP.service;

import com.senai.SAEP.entity.UsuarioEntity;
import com.senai.SAEP.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UsuarioService {

    private static final Pattern BCRYPT_PATTERN =
            Pattern.compile("^\\$2[aby]\\$\\d{2}\\$[./A-Za-z0-9]{53}$");

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Confere a senha com BCrypt. Para manter contas antigas acessíveis,
     * senhas legadas em texto puro são convertidas para BCrypt no primeiro login.
     */
    @Transactional
    public Optional<UsuarioEntity> autenticar(String login, String senha) {
        if (login == null || login.isBlank() || senha == null || senha.isEmpty()) {
            return Optional.empty();
        }

        Optional<UsuarioEntity> usuarioOpt =
                usuarioRepository.findByLogin(login.trim());

        if (usuarioOpt.isEmpty()) {
            return Optional.empty();
        }

        UsuarioEntity usuario = usuarioOpt.get();
        String senhaArmazenada = usuario.getSenha();

        if (senhaArmazenada == null || senhaArmazenada.isBlank()) {
            return Optional.empty();
        }

        if (BCRYPT_PATTERN.matcher(senhaArmazenada).matches()) {
            return passwordEncoder.matches(senha, senhaArmazenada)
                    ? Optional.of(usuario)
                    : Optional.empty();
        }

        // Migração transparente de contas já cadastradas com senha em texto puro.
        if (senhaArmazenada.equals(senha)) {
            usuario.setSenha(passwordEncoder.encode(senha));
            usuarioRepository.save(usuario);
            return Optional.of(usuario);
        }

        return Optional.empty();
    }
}
