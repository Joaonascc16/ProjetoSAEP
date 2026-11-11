package com.senai.SAEP.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String login;
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PerfilUsuario perfil = PerfilUsuario.OPERADOR;

    public UsuarioEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public PerfilUsuario getPerfil() {
        // Registros criados antes da inclusão dos perfis permanecem como operadores.
        return perfil == null ? PerfilUsuario.OPERADOR : perfil;
    }

    public void setPerfil(PerfilUsuario perfil) {
        this.perfil = perfil == null ? PerfilUsuario.OPERADOR : perfil;
    }
}
