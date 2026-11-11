package com.senai.SAEP.controller;

import com.senai.SAEP.entity.UsuarioEntity;
import com.senai.SAEP.service.MovimentacaoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MovimentacaoController {

    private final MovimentacaoService movimentacaoService;

    public MovimentacaoController(MovimentacaoService movimentacaoService) {
        this.movimentacaoService = movimentacaoService;
    }

    @PostMapping("/movimentacao")
    public String registrarMovimentacao(
            @RequestParam Long produtoId,
            @RequestParam String tipo,
            @RequestParam Integer quantidade,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        UsuarioEntity usuario =
                (UsuarioEntity) session.getAttribute("usuarioLogado");

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            String alerta = movimentacaoService.registrarMovimentacao(
                    produtoId, tipo, quantidade, usuario
            );

            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Movimentação registrada com sucesso."
            );

            if (alerta != null) {
                redirectAttributes.addFlashAttribute("aviso", alerta);
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }

        return "redirect:/gestao-estoque";
    }
}
