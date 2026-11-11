package com.senai.SAEP.controller;

import com.senai.SAEP.entity.UsuarioEntity;
import com.senai.SAEP.service.MovimentacaoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MovimentacaoController {

    @Autowired
    private MovimentacaoService movimentacaoService;


    @PostMapping("/movimentacao")
    public String registrarMovimentacao(
            @RequestParam Long produtoId,
            @RequestParam String tipo,
            @RequestParam Integer quantidade,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        UsuarioEntity usuario =
                (UsuarioEntity) session.getAttribute("usuarioLogado");


        // Usuário não está logado

        if (usuario == null) {

            return "redirect:/login";
        }


        try {

            String alerta = movimentacaoService.registrarMovimentacao(
                    produtoId,
                    tipo,
                    quantidade,
                    usuario
            );

            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Movimentação registrada com sucesso."
            );

            // Alerta de estoque mínimo (aula 9)
            if (alerta != null) {
                redirectAttributes.addFlashAttribute("aviso", alerta);
            }

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "erro",
                    e.getMessage()
            );
        }


        return "redirect:/gestao-estoque";
    }
}