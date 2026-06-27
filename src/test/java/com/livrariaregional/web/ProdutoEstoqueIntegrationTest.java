package com.livrariaregional.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProdutoEstoqueIntegrationTest {

    @Autowired
    private MockMvc mvc;

    // ============== PRODUTOS ==============

    @Test
    @WithMockUser(username = "gerente", roles = "GERENTE")
    void gerenteAcessaListaDeProdutos() throws Exception {
        mvc.perform(get("/dashboard/produtos"))
                .andExpect(status().isOk())
                .andExpect(view().name("produtos/lista"))
                .andExpect(content().contentTypeCompatibleWith("text/html"));
    }

    @Test
    @WithMockUser(username = "gerente", roles = "GERENTE")
    void gerenteAcessaFormularioDeNovoProduto() throws Exception {
        mvc.perform(get("/dashboard/produtos/novo"))
                .andExpect(status().isOk())
                .andExpect(view().name("produtos/form"));
    }

    @Test
    @WithMockUser(username = "gerente", roles = "GERENTE")
    void gerenteCriaNovoProdutoComSucesso() throws Exception {
        // Codigo unico (UUID) para evitar colisao entre runs do surefire.
        String codigo = "T" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        mvc.perform(post("/dashboard/produtos/novo")
                        .with(csrf())
                        .param("codigo", codigo)
                        .param("nome", "Livro Integ Test")
                        .param("autor", "Autor Teste")
                        .param("isbn", "978-85-00000-00-0")
                        .param("categoria", "Teste")
                        .param("preco", "29.90")
                        .param("estoqueMinimo", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard/produtos"));
    }

    @Test
    @WithMockUser(username = "atendente", roles = "ATENDENTE")
    void atendenteNaoPodeCriarProduto() throws Exception {
        String codigo = "T" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        mvc.perform(post("/dashboard/produtos/novo")
                        .with(csrf())
                        .param("codigo", codigo)
                        .param("nome", "Bloqueado")
                        .param("categoria", "Teste")
                        .param("preco", "29.90")
                        .param("estoqueMinimo", "2"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "atendente", roles = "ATENDENTE")
    void atendenteNaoPodeAcessarListaDeProdutos() throws Exception {
        // /dashboard/** esta com hasRole GERENTE no SecurityConfig
        mvc.perform(get("/dashboard/produtos"))
                .andExpect(status().isForbidden());
    }

    // ============== ESTOQUE ==============

    @Test
    @WithMockUser(username = "gerente", roles = "GERENTE")
    void gerenteAcessaEstoqueGeral() throws Exception {
        mvc.perform(get("/dashboard/estoque"))
                .andExpect(status().isOk())
                .andExpect(view().name("estoque"));
    }

    @Test
    @WithMockUser(username = "atendente", roles = "ATENDENTE")
    void atendenteAcessaEstoqueDaPropriaFilial() throws Exception {
        // /dashboard/estoque esta liberado para GERENTE e ATENDENTE.
        // O atendente do seed tem filial=1; o controller limita a visao.
        mvc.perform(get("/dashboard/estoque"))
                .andExpect(status().isOk())
                .andExpect(view().name("estoque"));
    }
}