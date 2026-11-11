package com.senai.SAEP.service;

import com.senai.SAEP.entity.MovimentacaoEntity;
import com.senai.SAEP.entity.ProdutoEntity;
import com.senai.SAEP.entity.UsuarioEntity;
import com.senai.SAEP.repository.MovimentacaoRepository;
import com.senai.SAEP.repository.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovimentacaoServiceTest {

    @Mock
    private MovimentacaoRepository movimentacaoRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private MovimentacaoService movimentacaoService;

    @Test
    void deveRegistrarEntradaEAtualizarHistorico() {
        ProdutoEntity produto = produtoComEstoque(5, 2);
        UsuarioEntity usuario = new UsuarioEntity();
        when(produtoRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(produto));

        String alerta = movimentacaoService.registrarMovimentacao(
                1L, "entrada", 3, usuario
        );

        assertNull(alerta);
        assertEquals(8, produto.getQuantidade());

        ArgumentCaptor<MovimentacaoEntity> captor =
                ArgumentCaptor.forClass(MovimentacaoEntity.class);
        verify(movimentacaoRepository).save(captor.capture());

        MovimentacaoEntity registrada = captor.getValue();
        assertEquals("ENTRADA", registrada.getTipo());
        assertEquals(3, registrada.getQuantidade());
        assertEquals(5, registrada.getQuantidadeAnterior());
        assertEquals(8, registrada.getQuantidadeAtual());
        assertSame(usuario, registrada.getUsuario());
    }

    @Test
    void deveImpedirSaidaAcimaDoEstoqueDisponivel() {
        ProdutoEntity produto = produtoComEstoque(2, 1);
        when(produtoRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(produto));

        IllegalArgumentException erro = assertThrows(
                IllegalArgumentException.class,
                () -> movimentacaoService.registrarMovimentacao(
                        1L, "SAIDA", 3, new UsuarioEntity()
                )
        );

        assertTrue(erro.getMessage().contains("maior que o estoque disponível"));
        assertEquals(2, produto.getQuantidade());
        verify(produtoRepository, never()).save(any());
        verify(movimentacaoRepository, never()).save(any());
    }

    @Test
    void deveRejeitarQuantidadeZero() {
        IllegalArgumentException erro = assertThrows(
                IllegalArgumentException.class,
                () -> movimentacaoService.registrarMovimentacao(
                        1L, "ENTRADA", 0, new UsuarioEntity()
                )
        );

        assertEquals("A quantidade deve ser maior que zero.", erro.getMessage());
        verifyNoInteractions(produtoRepository, movimentacaoRepository);
    }

    @Test
    void deveAlertarQuandoEstoqueChegarAoMinimo() {
        ProdutoEntity produto = produtoComEstoque(5, 3);
        when(produtoRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(produto));

        String alerta = movimentacaoService.registrarMovimentacao(
                1L, "SAIDA", 2, new UsuarioEntity()
        );

        assertNotNull(alerta);
        assertTrue(alerta.contains("estoque baixo"));
        assertEquals(3, produto.getQuantidade());
    }

    private ProdutoEntity produtoComEstoque(int quantidade, int estoqueMinimo) {
        ProdutoEntity produto = new ProdutoEntity();
        produto.setId(1L);
        produto.setNome("Produto de teste");
        produto.setPreco(10.0);
        produto.setQuantidade(quantidade);
        produto.setEstoqueMinimo(estoqueMinimo);
        return produto;
    }
}
