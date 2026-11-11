package com.senai.SAEP.controller;

import com.senai.SAEP.entity.PerfilUsuario;
import com.senai.SAEP.entity.ProdutoEntity;
import com.senai.SAEP.entity.UsuarioEntity;
import com.senai.SAEP.service.MovimentacaoService;
import com.senai.SAEP.service.ProdutoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ProdutoController {

    private final ProdutoService produtoService;
    private final MovimentacaoService movimentacaoService;

    public ProdutoController(
            ProdutoService produtoService,
            MovimentacaoService movimentacaoService) {
        this.produtoService = produtoService;
        this.movimentacaoService = movimentacaoService;
    }

    @GetMapping("/cadastro-produto")
    public String cadastroProduto(
            @RequestParam(value = "busca", required = false) String busca,
            HttpSession session,
            Model model) {

        if (!estaAutenticado(session)) {
            return "redirect:/login";
        }

        model.addAttribute("produtos", produtoService.buscarPorNome(busca));
        model.addAttribute("produto", new ProdutoEntity());
        model.addAttribute("busca", busca);
        model.addAttribute("usuario", usuarioDaSessao(session));
        return "cadastro-produto";
    }

    @PostMapping("/produtos/salvar")
    public String salvarProduto(
            @ModelAttribute ProdutoEntity produto,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (!estaAutenticado(session)) {
            return "redirect:/login";
        }

        String erroValidacao = validarProduto(produto);
        if (erroValidacao != null) {
            model.addAttribute("erro", erroValidacao);
            model.addAttribute("produtos", produtoService.listarTodos());
            model.addAttribute("produto", produto);
            model.addAttribute("usuario", usuarioDaSessao(session));
            return "cadastro-produto";
        }

        boolean novoProduto = produto.getId() == null;
        if (!novoProduto && produtoService.buscarPorId(produto.getId()).isEmpty()) {
            model.addAttribute("erro", "Produto não encontrado para atualização.");
            model.addAttribute("produtos", produtoService.listarTodos());
            model.addAttribute("produto", produto);
            model.addAttribute("usuario", usuarioDaSessao(session));
            return "cadastro-produto";
        }

        produtoService.salvar(produto);
        redirectAttributes.addFlashAttribute(
                "sucesso",
                novoProduto
                        ? "Produto cadastrado com sucesso."
                        : "Produto atualizado com sucesso."
        );
        return "redirect:/cadastro-produto";
    }

    @GetMapping("/produtos/editar/{id}")
    public String editarProduto(
            @PathVariable Long id,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (!estaAutenticado(session)) {
            return "redirect:/login";
        }

        return produtoService.buscarPorId(id)
                .map(produto -> {
                    model.addAttribute("produto", produto);
                    model.addAttribute("produtos", produtoService.listarTodos());
                    model.addAttribute("usuario", usuarioDaSessao(session));
                    return "cadastro-produto";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("erro", "Produto não encontrado.");
                    return "redirect:/cadastro-produto";
                });
    }

    @PostMapping("/produtos/deletar/{id}")
    public String deletarProduto(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!estaAutenticado(session)) {
            return "redirect:/login";
        }

        if (usuarioDaSessao(session).getPerfil() != PerfilUsuario.ADMINISTRADOR) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Apenas administradores podem excluir produtos."
            );
            return "redirect:/cadastro-produto";
        }

        if (movimentacaoService.existeMovimentacaoParaProduto(id)) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Este produto possui movimentações no histórico e não pode ser excluído."
            );
            return "redirect:/cadastro-produto";
        }

        if (produtoService.buscarPorId(id).isEmpty()) {
            redirectAttributes.addFlashAttribute("erro", "Produto não encontrado.");
            return "redirect:/cadastro-produto";
        }

        try {
            produtoService.deletar(id);
            redirectAttributes.addFlashAttribute("sucesso", "Produto excluído com sucesso.");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Não foi possível excluir o produto porque existem registros vinculados."
            );
        }
        return "redirect:/cadastro-produto";
    }

    @GetMapping("/gestao-estoque")
    public String gestaoEstoque(
            @RequestParam(value = "ordenar", defaultValue = "nome") String ordenar,
            @RequestParam(value = "direcao", defaultValue = "asc") String direcao,
            HttpSession session,
            Model model) {

        if (!estaAutenticado(session)) {
            return "redirect:/login";
        }

        List<ProdutoEntity> produtos = produtoService.ordenarProdutos(ordenar, direcao);

        int totalEstoque = 0;
        int estoqueBaixo = 0;
        int semEstoque = 0;

        for (ProdutoEntity produto : produtos) {
            int quantidade = produto.getQuantidade() == null ? 0 : produto.getQuantidade();
            totalEstoque += quantidade;

            if (quantidade == 0) {
                semEstoque++;
            } else if (quantidade <= produto.getEstoqueMinimo()) {
                estoqueBaixo++;
            }
        }

        model.addAttribute("usuario", usuarioDaSessao(session));
        model.addAttribute("produtos", produtos);
        model.addAttribute("totalEstoque", totalEstoque);
        model.addAttribute("produtosEstoqueBaixo", estoqueBaixo);
        model.addAttribute("produtosSemEstoque", semEstoque);
        model.addAttribute("movimentacoes", movimentacaoService.listarTodas());
        model.addAttribute("ordenar", ordenar);
        model.addAttribute("direcao", direcao);

        return "gestao-estoque";
    }

    private boolean estaAutenticado(HttpSession session) {
        return usuarioDaSessao(session) != null;
    }

    private UsuarioEntity usuarioDaSessao(HttpSession session) {
        return (UsuarioEntity) session.getAttribute("usuarioLogado");
    }

    private String validarProduto(ProdutoEntity produto) {
        if (produto.getNome() == null || produto.getNome().isBlank()) {
            return "Informe o nome do produto.";
        }
        if (produto.getPreco() == null || produto.getPreco() <= 0) {
            return "O preço deve ser maior que zero.";
        }
        if (produto.getQuantidade() == null || produto.getQuantidade() < 0) {
            return "A quantidade não pode ser negativa.";
        }
        if (produto.getEstoqueMinimo() < 0) {
            return "O estoque mínimo não pode ser negativo.";
        }
        return null;
    }
}
