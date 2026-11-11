package com.senai.SAEP.controller;

import com.senai.SAEP.entity.PerfilUsuario;
import com.senai.SAEP.entity.ProdutoEntity;
import com.senai.SAEP.entity.UsuarioEntity;
import com.senai.SAEP.service.MovimentacaoService;
import com.senai.SAEP.service.ProdutoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoControllerTest {

    @Mock
    private ProdutoService produtoService;

    @Mock
    private MovimentacaoService movimentacaoService;

    @InjectMocks
    private ProdutoController produtoController;

    @Test
    void operadorNaoPodeExcluirProduto() {
        MockHttpSession session = sessaoComPerfil(PerfilUsuario.OPERADOR);
        RedirectAttributesModelMap redirect = new RedirectAttributesModelMap();

        String view = produtoController.deletarProduto(1L, session, redirect);

        assertEquals("redirect:/cadastro-produto", view);
        assertEquals(
                "Apenas administradores podem excluir produtos.",
                redirect.getFlashAttributes().get("erro")
        );
        verifyNoInteractions(produtoService, movimentacaoService);
    }

    @Test
    void administradorNaoPodeExcluirProdutoComHistorico() {
        MockHttpSession session = sessaoComPerfil(PerfilUsuario.ADMINISTRADOR);
        RedirectAttributesModelMap redirect = new RedirectAttributesModelMap();
        when(movimentacaoService.existeMovimentacaoParaProduto(1L)).thenReturn(true);

        String view = produtoController.deletarProduto(1L, session, redirect);

        assertEquals("redirect:/cadastro-produto", view);
        assertTrue(redirect.getFlashAttributes().get("erro").toString()
                .contains("movimentações no histórico"));
        verify(produtoService, never()).deletar(anyLong());
    }

    @Test
    void edicaoPreservaQuantidadeEExigeMovimentacaoParaAlterarSaldo() {
        MockHttpSession session = sessaoComPerfil(PerfilUsuario.OPERADOR);
        ProdutoEntity existente = produto(1L, "Mouse", 10.0, 12, 3);
        ProdutoEntity enviado = produto(1L, "Mouse Gamer", 15.0, 999, 4);
        when(produtoService.buscarPorId(1L)).thenReturn(Optional.of(existente));

        String view = produtoController.salvarProduto(
                enviado,
                session,
                new ExtendedModelMap(),
                new RedirectAttributesModelMap()
        );

        assertEquals("redirect:/cadastro-produto", view);
        assertEquals(12, enviado.getQuantidade());
        assertEquals("Mouse Gamer", enviado.getNome());
        verify(produtoService).salvar(enviado);
    }

    private MockHttpSession sessaoComPerfil(PerfilUsuario perfil) {
        MockHttpSession session = new MockHttpSession();
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(10L);
        usuario.setLogin("teste");
        usuario.setPerfil(perfil);
        session.setAttribute("usuarioLogado", usuario);
        return session;
    }

    private ProdutoEntity produto(
            Long id, String nome, double preco, int quantidade, int estoqueMinimo) {
        ProdutoEntity produto = new ProdutoEntity();
        produto.setId(id);
        produto.setNome(nome);
        produto.setPreco(preco);
        produto.setQuantidade(quantidade);
        produto.setEstoqueMinimo(estoqueMinimo);
        return produto;
    }
}
