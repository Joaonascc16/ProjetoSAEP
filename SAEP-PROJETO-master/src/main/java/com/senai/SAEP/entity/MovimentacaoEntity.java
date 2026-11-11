package com.senai.SAEP.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "movimentacoes")
public class MovimentacaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private ProdutoEntity produto;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false)
    private LocalDateTime data;

    // Rastreabilidade: estoque do produto antes e depois da movimentação
    // (Integer, pois registros antigos não possuem esses valores)
    private Integer quantidadeAnterior;

    private Integer quantidadeAtual;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;


    public Long getId() {
        return id;
    }

    public ProdutoEntity getProduto() {
        return produto;
    }

    public void setProduto(ProdutoEntity produto) {
        this.produto = produto;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public Integer getQuantidadeAnterior() {
        return quantidadeAnterior;
    }

    public void setQuantidadeAnterior(Integer quantidadeAnterior) {
        this.quantidadeAnterior = quantidadeAnterior;
    }

    public Integer getQuantidadeAtual() {
        return quantidadeAtual;
    }

    public void setQuantidadeAtual(Integer quantidadeAtual) {
        this.quantidadeAtual = quantidadeAtual;
    }

    public UsuarioEntity getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioEntity usuario) {
        this.usuario = usuario;
    }
}