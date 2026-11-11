package com.senai.SAEP.controller;

import com.senai.SAEP.dto.UsuarioDto;
import com.senai.SAEP.entity.UsuarioEntity;
import com.senai.SAEP.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/")
    public String inicio(HttpSession session) {
        return session.getAttribute("usuarioLogado") == null
                ? "redirect:/login"
                : "redirect:/home";
    }

    @GetMapping("/login")
    public String exibirLogin(HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogado") != null) {
            return "redirect:/home";
        }

        model.addAttribute("usuarioDto", new UsuarioDto());
        return "login";
    }

    @PostMapping("/login")
    public String processarLogin(
            @ModelAttribute UsuarioDto usuarioDto,
            Model model,
            HttpServletRequest request) {

        if (usuarioDto.getLogin() == null || usuarioDto.getLogin().isBlank()
                || usuarioDto.getSenha() == null || usuarioDto.getSenha().isEmpty()) {
            usuarioDto.setSenha(null);
            model.addAttribute("erro", "Preencha o login e a senha.");
            return "login";
        }

        Optional<UsuarioEntity> usuarioOpt =
                usuarioService.autenticar(usuarioDto.getLogin(), usuarioDto.getSenha());

        // Não manter a senha submetida no objeto retornado à tela.
        usuarioDto.setSenha(null);

        if (usuarioOpt.isEmpty()) {
            model.addAttribute("erro", "Login ou senha inválidos.");
            return "login";
        }

        // Renova a sessão após autenticação para reduzir o risco de session fixation.
        HttpSession sessaoAnterior = request.getSession(false);
        if (sessaoAnterior != null) {
            sessaoAnterior.invalidate();
        }

        HttpSession novaSessao = request.getSession(true);
        UsuarioEntity usuarioAutenticado = usuarioOpt.get();
        // O hash permanece no banco, mas não precisa ficar na sessão HTTP.
        usuarioAutenticado.setSenha(null);
        novaSessao.setAttribute("usuarioLogado", usuarioAutenticado);

        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        UsuarioEntity usuario = (UsuarioEntity) session.getAttribute("usuarioLogado");
        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuario);
        return "home";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
