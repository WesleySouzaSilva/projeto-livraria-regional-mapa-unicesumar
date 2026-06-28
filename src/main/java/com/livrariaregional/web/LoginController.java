package com.livrariaregional.web;

import com.livrariaregional.domain.Usuario;
import com.livrariaregional.repository.UsuarioRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller do fluxo de login + landing pages pos-auth.
 *
 * Rotas:
 *   GET /            -> redirect para /dashboard (GERENTE) ou /pdv (ATENDENTE)
 *   GET /login       -> tela de login (form vazio, com msg de erro/logout)
 *   GET /dashboard   -> landing GERENTE
 *   GET /pdv         -> agora vive em PdvController (PR #6). Foi removido
 *                       deste controller para evitar Ambiguous Mapping.
 *
 * Sobre /: por que no controller e nao no AuthenticationSuccessHandler?
 *   - O successHandler so roda na hora do login. / e uma rota que pode
 *     ser acessada a qualquer momento (link "inicio", bookmark). Entao
 *     a logica de roteamento por perfil precisa estar tambem aqui.
 *   - Mantemos a mesma politica de /dashboard e /pdv (hasRole no
 *     SecurityConfig) -> se um ATENDENTE tentar /dashboard, leva 403.
 */
@Controller
public class LoginController {

    private final UsuarioRepository usuarioRepository;

    public LoginController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/")
    public String root(Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }
        boolean isGerente = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_GERENTE".equals(a.getAuthority()));
        return "redirect:" + (isGerente ? "/dashboard" : "/pdv");
    }

    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model) {
        // O SecurityConfig joga ?error ou ?logout na URL em falha/logout.
        // Passamos flags pro template exibir mensagens amigaveis.
        if (request.getParameter("error") != null) {
            model.addAttribute("error", "Login ou senha invalidos.");
        }
        if (request.getParameter("logout") != null) {
            model.addAttribute("message", "Voce saiu do sistema.");
        }
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails principal, Model model) {
        carregarUsuario(principal, model);
        return "dashboard";
    }

    private void carregarUsuario(UserDetails principal, Model model) {
        if (principal == null) {
            return;
        }
        Usuario u = usuarioRepository.findByLogin(principal.getUsername()).orElse(null);
        if (u != null) {
            model.addAttribute("nomeUsuario", u.getNome());
            model.addAttribute("perfilUsuario", u.getPerfil().name());
            model.addAttribute("filialUsuario",
                    u.getFilial() != null ? u.getFilial().getNome() : "Todas");
        }
    }
}