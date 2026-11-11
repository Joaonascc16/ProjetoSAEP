package com.senai.SAEP.service;

import com.senai.SAEP.entity.ProdutoEntity;
import com.senai.SAEP.repository.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoService produtoService;

    @Test
    void deveOrdenarProdutosPorNomeEmOrdemCrescente() {
        ProdutoEntity produtoC = produto("Caderno", 15.0, 5);
        ProdutoEntity produtoA = produto("Apontador", 2.0, 20);
        ProdutoEntity produtoB = produto("Borracha", 3.0, 10);
        when(produtoRepository.findAll()).thenReturn(
                List.of(produtoC, produtoA, produtoB)
        );

        List<ProdutoEntity> resultado =
                produtoService.ordenarProdutos("nome", "asc");

        assertEquals(
                List.of("Apontador", "Borracha", "Caderno"),
                resultado.stream().map(ProdutoEntity::getNome).toList()
        );
    }

    @Test
    void deveOrdenarProdutosPorPrecoEmOrdemDecrescente() {
        ProdutoEntity produtoBarato = produto("Barato", 2.0, 5);
        ProdutoEntity produtoCaro = produto("Caro", 20.0, 2);
        ProdutoEntity produtoMedio = produto("Médio", 10.0, 8);
        when(produtoRepository.findAll()).thenReturn(
                List.of(produtoBarato, produtoCaro, produtoMedio)
        );

        List<ProdutoEntity> resultado =
                produtoService.ordenarProdutos("preco", "desc");

        assertEquals(
                List.of("Caro", "Médio", "Barato"),
                resultado.stream().map(ProdutoEntity::getNome).toList()
        );
    }

    private ProdutoEntity produto(String nome, double preco, int quantidade) {
        ProdutoEntity produto = new ProdutoEntity();
        produto.setNome(nome);
        produto.setPreco(preco);
        produto.setQuantidade(quantidade);
        return produto;
    }
}
