package com.senai.SAEP.controller;

import com.senai.SAEP.entity.ProdutoEntity;
import com.senai.SAEP.service.MovimentacaoService;
import com.senai.SAEP.service.ProdutoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private MovimentacaoService movimentacaoService;

    @GetMapping("/cadastro-produto")
    public String cadastroProduto(@RequestParam(value = "busca", required = false) String busca, HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login";
        }

        model.addAttribute("produtos", produtoService.buscarPorNome(busca));
        model.addAttribute("produto", new ProdutoEntity());
        model.addAttribute("busca", busca);
        return "cadastro-produto";
    }

    @PostMapping("/produtos/salvar")
    public String salvarProduto(@ModelAttribute ProdutoEntity produto, HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login";
        }

        if (produto.getNome() == null || produto.getNome().trim().isEmpty() ||
                produto.getPreco() == null || produto.getPreco() < 0 ||
                produto.getQuantidade() == null || produto.getQuantidade() < 0) {

            model.addAttribute("erro", "Preencha todos os campos com valores válidos.");
            model.addAttribute("produtos", produtoService.listarTodos());
            model.addAttribute("produto", produto);
            return "cadastro-produto";
        }

        produtoService.salvar(produto);
        return "redirect:/cadastro-produto";
    }

    @GetMapping("/produtos/editar/{id}")
    public String editarProduto(@PathVariable Long id, HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login";
        }

        produtoService.buscarPorId(id).ifPresent(p -> model.addAttribute("produto", p));
        model.addAttribute("produtos", produtoService.listarTodos());
        return "cadastro-produto";
    }

    @GetMapping("/produtos/deletar/{id}")
    public String deletarProduto(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login";
        }

        produtoService.deletar(id);
        return "redirect:/cadastro-produto";
    }

    @GetMapping("/gestao-estoque")
    public String gestaoEstoque(
            @RequestParam(value = "ordenar", defaultValue = "nome") String ordenar,
            @RequestParam(value = "direcao", defaultValue = "asc") String direcao,
            HttpSession session,
            Model model) {

        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login";
        }

        // Aula 8: produtos ordenados pelo Insertion Sort
        List<ProdutoEntity> produtos = produtoService.ordenarProdutos(ordenar, direcao);

        // Cards de resumo (estoque baixo / sem estoque)
        int totalEstoque = 0;
        int estoqueBaixo = 0;
        int semEstoque = 0;

        for (ProdutoEntity p : produtos) {
            totalEstoque += p.getQuantidade();

            if (p.getQuantidade() == 0) {
                semEstoque++;
            } else if (p.getQuantidade() <= p.getEstoqueMinimo()) {
                estoqueBaixo++;
            }
        }

        model.addAttribute("usuario", session.getAttribute("usuarioLogado"));
        model.addAttribute("produtos", produtos);
        model.addAttribute("totalEstoque", totalEstoque);
        model.addAttribute("produtosEstoqueBaixo", estoqueBaixo);
        model.addAttribute("produtosSemEstoque", semEstoque);

        // Aula 9: histórico de movimentações
        model.addAttribute("movimentacoes", movimentacaoService.listarTodas());

        // Mantém a ordenação escolhida selecionada na tela
        model.addAttribute("ordenar", ordenar);
        model.addAttribute("direcao", direcao);

        return "gestao-estoque";
    }
}