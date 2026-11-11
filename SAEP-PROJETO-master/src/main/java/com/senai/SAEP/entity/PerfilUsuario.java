package com.senai.SAEP.entity;

/**
 * Perfis de acesso do sistema.
 *
 * OPERADOR: consulta, cadastro e atualização de produtos e movimentações.
 * ADMINISTRADOR: possui as permissões do operador e também pode excluir produtos.
 */
public enum PerfilUsuario {
    ADMINISTRADOR,
    OPERADOR
}
