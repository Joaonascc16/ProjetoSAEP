package com.senai.SAEP.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "produtos")
public class ProdutoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome deve ser obrigatorio")
    private String nome;

    @NotNull(message = "O preço é obrigatório")
    @Min(value = 0, message = "Preço inválido")
    private Double preco;

    @NotNull(message = "A quantidade é obrigatória")
    @Min(value = 0, message = "Quantidade inválida")
    private Integer quantidade;

    private int estoqueMinimo;

    public ProdutoEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Double getPreco() { return preco; }
    public void setPreco(Double preco) { this.preco = preco; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public int getEstoqueMinimo() {
        return estoqueMinimo;
    }
    public void setEstoqueMinimo(int estoqueMinimo) {
        this.estoqueMinimo = estoqueMinimo;
    }
}