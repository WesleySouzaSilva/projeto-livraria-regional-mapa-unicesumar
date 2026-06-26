package com.livrariaregional.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Testes de integracao do fluxo de login.
 *
 * Cobertura:
 *   1. Acesso anonimo a /dashboard -> redirecionado para /login.
 *   2. Login com gerente/admin123  -> redirecionado para /dashboard.
 *   3. Login com atendente/atendente123 -> redirecionado para /pdv.
 *   4. Login com senha invalida    -> redirecionado para /login?error.
 *   5. Tela /login renderiza normalmente.
 *
 * Decisao sobre assertivas de redirect:
 *   - Em testes de MockMvc, redirectedUrl(...) e redirectedUrlPattern(...)
 *     comparam EXATAMENTE a URL do Location header. Aqui o chain de
 *     redirect (handler -> /dashboard -> ...) as vezes aparece so a URL
 *     final no getRedirectedUrl(). Usamos endsWith() no path para ser
 *     robustos a scheme/host/port.
 *   - O sucesso de /login retorna 302 e o Location e "/dashboard" ou
 *     "/pdv" (sem scheme porque o handler usa response.sendRedirect
 *     com contextPath + path). EndsWith casa com isso.
 */
@SpringBootTest
@ActiveProfiles("test")
class LoginFlowIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private MockMvc mockMvc() {
        if (mockMvc == null) {
            mockMvc = MockMvcBuilders
                    .webAppContextSetup(context)
                    .apply(springSecurity())
                    .build();
        }
        return mockMvc;
    }

    @Test
    void dashboardAnonimoRedirecionaParaLogin() throws Exception {
        MvcResult result = mockMvc().perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String location = result.getResponse().getRedirectedUrl();
        assertNotNull(location, "Location header ausente");
        assertTrue(location.contains("/login"),
                "Anonimo deveria ser mandado para /login, mas foi para: " + location);
    }

    @Test
    void gerenteAutenticaECaiNoDashboard() throws Exception {
        MvcResult result = mockMvc().perform(post("/login")
                        .with(csrf())
                        .param("login", "gerente")
                        .param("senha", "admin123"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String location = result.getResponse().getRedirectedUrl();
        assertNotNull(location, "Location header ausente no redirect pos-login");
        assertTrue(location.endsWith("/dashboard"),
                "Gerente deveria cair em /dashboard, mas foi para: " + location);
    }

    @Test
    void atendenteAutenticaECaiNoPdv() throws Exception {
        MvcResult result = mockMvc().perform(post("/login")
                        .with(csrf())
                        .param("login", "atendente")
                        .param("senha", "atendente123"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String location = result.getResponse().getRedirectedUrl();
        assertNotNull(location, "Location header ausente no redirect pos-login");
        assertTrue(location.endsWith("/pdv"),
                "Atendente deveria cair em /pdv, mas foi para: " + location);
    }

    @Test
    void loginComSenhaInvalidaVoltaParaLoginComErro() throws Exception {
        MvcResult result = mockMvc().perform(post("/login")
                        .with(csrf())
                        .param("login", "gerente")
                        .param("senha", "senhaErrada"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String location = result.getResponse().getRedirectedUrl();
        assertNotNull(location, "Location header ausente no redirect de erro");
        assertTrue(location.contains("/login") && location.contains("error"),
                "Falha de login deveria voltar para /login?error, foi para: " + location);
    }

    @Test
    void telaLoginRenderiza() throws Exception {
        mockMvc().perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }
}