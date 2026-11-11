package com.senai.SAEP.service;

import com.senai.SAEP.entity.UsuarioEntity;
import com.senai.SAEP.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void deveMigrarSenhaLegadaParaBcryptNoPrimeiroLogin() {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setLogin("operador");
        usuario.setSenha("senha-antiga");
        when(usuarioRepository.findByLogin("operador")).thenReturn(Optional.of(usuario));

        Optional<UsuarioEntity> resultado =
                usuarioService.autenticar("operador", "senha-antiga");

        assertTrue(resultado.isPresent());
        assertNotEquals("senha-antiga", usuario.getSenha());
        assertTrue(new BCryptPasswordEncoder().matches("senha-antiga", usuario.getSenha()));
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deveAceitarSenhaBcryptCorreta() {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setLogin("admin");
        usuario.setSenha(new BCryptPasswordEncoder().encode("senha-segura"));
        when(usuarioRepository.findByLogin("admin")).thenReturn(Optional.of(usuario));

        assertTrue(usuarioService.autenticar("admin", "senha-segura").isPresent());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveRejeitarSenhaIncorreta() {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setLogin("admin");
        usuario.setSenha(new BCryptPasswordEncoder().encode("senha-segura"));
        when(usuarioRepository.findByLogin("admin")).thenReturn(Optional.of(usuario));

        assertTrue(usuarioService.autenticar("admin", "incorreta").isEmpty());
        verify(usuarioRepository, never()).save(any());
    }
}
