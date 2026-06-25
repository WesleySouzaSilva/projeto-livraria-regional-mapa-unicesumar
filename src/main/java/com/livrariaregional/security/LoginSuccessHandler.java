package com.livrariaregional.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Decide o destino apos login com base no perfil do usuario.
 *
 * Por que custom? O defaultSuccessUrl("/", true) do Spring Security sempre
 * manda pra raiz, e o "/"" do LoginController ja redireciona certo, mas
 * nesse caminho intermediaria a ida do navegador ate "/" antes do
 * segundo redirect. Com o handler direto, vai pra area certa em 1
 * unico redirect (menos round-trip, URL final mais limpa).
 *
 * Regras:
 *   GERENTE   -> /dashboard
 *   ATENDENTE -> /pdv
 *   outros    -> / (seguranca: area neutra ate o admin definir destino)
 *
 * Obs.: authorities vem com prefixo "ROLE_" porque o UsuarioDetailsService
 * retorna GrantedAuthority com role() prefixada, e o SecurityContext
 * mantem o prefixo. Entao comparo com hasAuthority("ROLE_GERENTE").
 */
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String target = "/";
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority();
            if ("ROLE_GERENTE".equals(role)) {
                target = "/dashboard";
                break;
            } else if ("ROLE_ATENDENTE".equals(role)) {
                target = "/pdv";
                break;
            }
        }
        redirectStrategy.sendRedirect(request, response, target);
    }
}