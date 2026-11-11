package com.senai.SAEP.dto;

public class UsuarioDto {

    private String login;
    private String senha;

    public UsuarioDto() {}

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}
