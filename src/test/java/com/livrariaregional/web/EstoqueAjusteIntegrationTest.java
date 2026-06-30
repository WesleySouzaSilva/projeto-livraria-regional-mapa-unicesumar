package com.livrariaregional.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Testes de integracao para a feature "Ajustar Estoque" (sub-rota de /dashboard/estoque).
 *
 * Cenarios:
 *   1. GERENTE acessa o formulario de ajuste (200 + view estoque-ajustar)
 *   2. GERENTE ajusta a quantidade absoluta com sucesso (302 redirect com flash de sucesso)
 *   3. GERENTE tenta definir quantidade negativa (302 redirect com flash de erro)
 *   4. ATENDENTE NAO consegue acessar o formulario (403)
 *   5. ATENDENTE NAO consegue submeter o POST (403)
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class EstoqueAjusteIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @WithMockUser(username = "gerente", roles = "GERENTE")
    void gerenteAcessaFormularioDeAjuste() throws Exception {
        mvc.perform(get("/dashboard/estoque/ajustar"))
                .andExpect(status().isOk())
                .andExpect(view().name("estoque-ajustar"));
    }

    @Test
    @WithMockUser(username = "gerente", roles = "GERENTE")
    void gerenteAjustaQuantidadeComSucesso() throws Exception {
        // Filial 1 (Centro), Produto 1 (LV001). Seed inicial = 15 unidades.
        mvc.perform(post("/dashboard/estoque/ajustar")
                        .with(csrf())
                        .param("filialId", "1")
                        .param("produtoId", "1")
                        .param("quantidade", "42"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard/estoque?filialId=1"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    @WithMockUser(username = "gerente", roles = "GERENTE")
    void gerenteNaoPodeDefinirQuantidadeNegativa() throws Exception {
        mvc.perform(post("/dashboard/estoque/ajustar")
                        .with(csrf())
                        .param("filialId", "1")
                        .param("produtoId", "1")
                        .param("quantidade", "-1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard/estoque?filialId=1"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    @WithMockUser(username = "atendente", roles = "ATENDENTE")
    void atendenteNaoPodeAcessarFormularioDeAjuste() throws Exception {
        mvc.perform(get("/dashboard/estoque/ajustar"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "atendente", roles = "ATENDENTE")
    void atendenteNaoPodeSubmeterAjuste() throws Exception {
        mvc.perform(post("/dashboard/estoque/ajustar")
                        .with(csrf())
                        .param("filialId", "1")
                        .param("produtoId", "1")
                        .param("quantidade", "10"))
                .andExpect(status().isForbidden());
    }
}
