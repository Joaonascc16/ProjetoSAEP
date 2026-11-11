package com.senai.SAEP.service;

import com.senai.SAEP.entity.ProdutoEntity;
import com.senai.SAEP.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    public List<ProdutoEntity> listarTodos() {
        return produtoRepository.findAll();
    }

    public List<ProdutoEntity> buscarPorNome(String nome) {
        if (nome == null || nome.isBlank()) {
            return produtoRepository.findAll();
        }
        return produtoRepository.findByNomeContainingIgnoreCase(nome);
    }

    public void salvar(ProdutoEntity produto) {
        produtoRepository.save(produto);
    }

    public Optional<ProdutoEntity> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }

    public void deletar(Long id) {
        produtoRepository.deleteById(id);
    }


    // =========================================================
    // AULA 8 - ALGORITMO DE ORDENAÇÃO (Insertion Sort)
    // =========================================================

    /**
     * Ordena os produtos com o Insertion Sort, sem usar sort() do Java.
     *
     * Como funciona: percorre a lista da esquerda para a direita. Em cada
     * passo pega o produto atual (a "chave") e o encaixa na posição certa
     * dentro da parte da esquerda, que já está ordenada.
     *
     * @param criterio "nome", "preco" ou "quantidade"
     * @param direcao  "asc" (crescente) ou "desc" (decrescente)
     */
    public List<ProdutoEntity> ordenarProdutos(String criterio, String direcao) {

        List<ProdutoEntity> lista = new ArrayList<>(produtoRepository.findAll());

        for (int i = 1; i < lista.size(); i++) {

            ProdutoEntity chave = lista.get(i);
            int j = i - 1;

            // Empurra para a direita quem deve vir depois da chave
            while (j >= 0 && comparar(lista.get(j), chave, criterio, direcao) > 0) {
                lista.set(j + 1, lista.get(j));
                j--;
            }

            // Encaixa a chave na posição correta
            lista.set(j + 1, chave);
        }

        return lista;
    }

    // Compara dois produtos: negativo se "a" vem antes, positivo se vem depois
    private int comparar(ProdutoEntity a, ProdutoEntity b, String criterio, String direcao) {

        int resultado;

        if ("preco".equals(criterio)) {
            resultado = a.getPreco().compareTo(b.getPreco());
        } else if ("quantidade".equals(criterio)) {
            resultado = a.getQuantidade().compareTo(b.getQuantidade());
        } else {
            resultado = a.getNome().compareToIgnoreCase(b.getNome());
        }

        // Ordem decrescente = inverte o resultado
        return "desc".equals(direcao) ? -resultado : resultado;
    }
}