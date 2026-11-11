package com.senai.SAEP.service;

import com.senai.SAEP.entity.MovimentacaoEntity;
import com.senai.SAEP.entity.ProdutoEntity;
import com.senai.SAEP.entity.UsuarioEntity;
import com.senai.SAEP.repository.MovimentacaoRepository;
import com.senai.SAEP.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MovimentacaoService {

    @Autowired
    private MovimentacaoRepository movimentacaoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;


    // Lista todas as movimentações (mais recentes primeiro)
    public List<MovimentacaoEntity> listarTodas() {
        return movimentacaoRepository.findAllByOrderByDataDesc();
    }


    /**
     * Registra uma entrada ou saída de estoque.
     *
     * @Transactional: atualizar o produto e gravar a movimentação acontecem
     * juntos. Se algo falhar no meio, nada é gravado (evita estoque alterado
     * sem histórico).
     *
     * @return mensagem de alerta se, após a movimentação, o produto ficou
     *         com estoque no mínimo (ou abaixo); caso contrário, null.
     */
    @Transactional
    public String registrarMovimentacao(
            Long produtoId,
            String tipo,
            Integer quantidade,
            UsuarioEntity usuario) {

        // REGRA 1: a quantidade deve ser maior que zero
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade deve ser maior que zero."
            );
        }

        // REGRA 2: o produto precisa existir
        ProdutoEntity produto = produtoRepository
                .findById(produtoId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Produto não encontrado.")
                );

        // REGRA 3: o tipo deve ser ENTRADA ou SAIDA
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo de movimentação inválido.");
        }

        tipo = tipo.trim().toUpperCase();

        if (!tipo.equals("ENTRADA") && !tipo.equals("SAIDA")) {
            throw new IllegalArgumentException("Tipo de movimentação inválido.");
        }

        // Guarda o estoque ANTES da movimentação (rastreabilidade)
        int quantidadeAnterior = produto.getQuantidade();

        // REGRA 4: ENTRADA aumenta a quantidade
        if (tipo.equals("ENTRADA")) {
            produto.setQuantidade(quantidadeAnterior + quantidade);
        }

        // REGRA 5: SAIDA diminui a quantidade (sem permitir estoque negativo)
        if (tipo.equals("SAIDA")) {

            if (quantidade > quantidadeAnterior) {
                throw new IllegalArgumentException(
                        "Não é possível realizar a saída. " +
                                "A quantidade solicitada é maior que o estoque disponível."
                );
            }

            produto.setQuantidade(quantidadeAnterior - quantidade);
        }

        int quantidadeAtual = produto.getQuantidade();

        // Atualiza o produto no banco
        produtoRepository.save(produto);

        // Cria o registro da movimentação
        MovimentacaoEntity movimentacao = new MovimentacaoEntity();

        movimentacao.setProduto(produto);
        movimentacao.setTipo(tipo);
        movimentacao.setQuantidade(quantidade);
        movimentacao.setQuantidadeAnterior(quantidadeAnterior);
        movimentacao.setQuantidadeAtual(quantidadeAtual);
        movimentacao.setUsuario(usuario);
        movimentacao.setData(LocalDateTime.now());

        movimentacaoRepository.save(movimentacao);

        // AULA 9: alerta de estoque mínimo
        return gerarAlerta(produto);
    }


    // Gera a mensagem de alerta (ou null se o estoque está normal)
    private String gerarAlerta(ProdutoEntity produto) {

        if (produto.getQuantidade() == 0) {
            return "ALERTA: o produto \"" + produto.getNome()
                    + "\" está SEM ESTOQUE.";
        }

        if (produto.getQuantidade() <= produto.getEstoqueMinimo()) {
            return "ALERTA: o produto \"" + produto.getNome()
                    + "\" está com estoque baixo (atual: " + produto.getQuantidade()
                    + ", mínimo: " + produto.getEstoqueMinimo() + ").";
        }

        return null;
    }
}