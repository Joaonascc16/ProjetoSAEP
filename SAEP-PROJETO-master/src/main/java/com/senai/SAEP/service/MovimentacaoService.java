package com.senai.SAEP.service;

import com.senai.SAEP.entity.MovimentacaoEntity;
import com.senai.SAEP.entity.ProdutoEntity;
import com.senai.SAEP.entity.UsuarioEntity;
import com.senai.SAEP.repository.MovimentacaoRepository;
import com.senai.SAEP.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class MovimentacaoService {

    private final MovimentacaoRepository movimentacaoRepository;
    private final ProdutoRepository produtoRepository;

    public MovimentacaoService(
            MovimentacaoRepository movimentacaoRepository,
            ProdutoRepository produtoRepository) {
        this.movimentacaoRepository = movimentacaoRepository;
        this.produtoRepository = produtoRepository;
    }

    public List<MovimentacaoEntity> listarTodas() {
        return movimentacaoRepository.findAllByOrderByDataDesc();
    }

    public boolean existeMovimentacaoParaProduto(Long produtoId) {
        return produtoId != null && movimentacaoRepository.existsByProduto_Id(produtoId);
    }

    /**
     * Atualiza o estoque e grava o histórico na mesma transação.
     * O bloqueio pessimista evita que duas requisições concorrentes sobrescrevam
     * a quantidade uma da outra.
     */
    @Transactional
    public String registrarMovimentacao(
            Long produtoId,
            String tipo,
            Integer quantidade,
            UsuarioEntity usuario) {

        if (usuario == null) {
            throw new IllegalArgumentException("É necessário estar autenticado para movimentar o estoque.");
        }

        if (produtoId == null) {
            throw new IllegalArgumentException("Selecione um produto válido.");
        }

        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }

        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("Tipo de movimentação inválido.");
        }

        tipo = tipo.trim().toUpperCase(Locale.ROOT);
        if (!tipo.equals("ENTRADA") && !tipo.equals("SAIDA")) {
            throw new IllegalArgumentException("Tipo de movimentação inválido.");
        }

        ProdutoEntity produto = produtoRepository.findByIdForUpdate(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

        Integer quantidadeRegistrada = produto.getQuantidade();
        if (quantidadeRegistrada == null || quantidadeRegistrada < 0) {
            throw new IllegalArgumentException("O estoque atual do produto está inválido.");
        }

        int quantidadeAnterior = quantidadeRegistrada;
        int quantidadeAtual;

        if (tipo.equals("ENTRADA")) {
            try {
                quantidadeAtual = Math.addExact(quantidadeAnterior, quantidade);
            } catch (ArithmeticException e) {
                throw new IllegalArgumentException("A quantidade ultrapassa o limite permitido de estoque.");
            }
        } else {
            if (quantidade > quantidadeAnterior) {
                throw new IllegalArgumentException(
                        "Não é possível realizar a saída. A quantidade solicitada é maior que o estoque disponível."
                );
            }
            quantidadeAtual = quantidadeAnterior - quantidade;
        }

        produto.setQuantidade(quantidadeAtual);
        produtoRepository.save(produto);

        MovimentacaoEntity movimentacao = new MovimentacaoEntity();
        movimentacao.setProduto(produto);
        movimentacao.setTipo(tipo);
        movimentacao.setQuantidade(quantidade);
        movimentacao.setQuantidadeAnterior(quantidadeAnterior);
        movimentacao.setQuantidadeAtual(quantidadeAtual);
        movimentacao.setUsuario(usuario);
        movimentacao.setData(LocalDateTime.now());

        movimentacaoRepository.save(movimentacao);

        return gerarAlerta(produto);
    }

    private String gerarAlerta(ProdutoEntity produto) {
        int quantidadeAtual = produto.getQuantidade();

        if (quantidadeAtual == 0) {
            return "ALERTA: o produto \"" + produto.getNome() + "\" está SEM ESTOQUE.";
        }

        if (quantidadeAtual <= produto.getEstoqueMinimo()) {
            return "ALERTA: o produto \"" + produto.getNome()
                    + "\" está com estoque baixo (atual: " + quantidadeAtual
                    + ", mínimo: " + produto.getEstoqueMinimo() + ").";
        }

        return null;
    }
}
