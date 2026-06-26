package com.livrariaregional.config;

import com.livrariaregional.security.LoginSuccessHandler;
import com.livrariaregional.security.UsuarioDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Configuracao do Spring Security para a Livraria Regional.
 *
 * Decisao de design (PR #4): usar o **controle de login basico** que o
 * Spring Security ja entrega pronto:
 *   - formLogin()        -> tela /login customizada em Thymeleaf
 *   - BCryptPasswordEncoder -> ja disponivel via SecurityBeans
 *   - DaoAuthenticationProvider + UsuarioDetailsService -> usuarios no H2/MySQL
 *   - Regras hasRole(...) -> separa areas do GERENTE e do ATENDENTE
 *
 * Por que essa abordagem e nao OAuth desde ja?
 *   OAuth (Google, Microsoft) traz dependencia externa, cadastro de app
 *   no provider, fluxo de redirect, e complica o deploy do MVP. Para um
 *   sistema interno de livraria com 3 filiais e ~poucos atendentes, o
 *   custo > beneficio. O caminho basico (login + senha + BCrypt) ja
 *   cobre a regra do trabalho: "autenticar usuarios com perfis distintos".
 *
 * Evolucao futura: se o cliente quiser SSO (login unificado com conta
 * Google/Microsoft da empresa), basta adicionar
 * spring-boot-starter-oauth2-client e registrar os providers. O form
 * login continua funcionando como fallback ou para usuarios locais.
 *
 * Rotas desta PR #4:
 *   /login, /logout, /css/**, /h2-console/** -> publicos
 *   /dashboard/**   -> so GERENTE
 *   /pdv/**         -> so ATENDENTE
 *   Qualquer outra  -> exige autenticacao
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UsuarioDetailsService usuarioDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final LoginSuccessHandler loginSuccessHandler;

    public SecurityConfig(UsuarioDetailsService usuarioDetailsService,
                          PasswordEncoder passwordEncoder,
                          LoginSuccessHandler loginSuccessHandler) {
        this.usuarioDetailsService = usuarioDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.loginSuccessHandler = loginSuccessHandler;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(usuarioDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        auth.authenticationProvider(provider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            // CSRF fica habilitado (default). Desabilito so no /h2-console
            // para o console funcionar dentro do iframe do Spring.
            .csrf(csrf -> csrf.ignoringAntMatchers("/h2-console/**"))
            .headers(headers -> headers.frameOptions().sameOrigin())
            .authorizeRequests(authz -> authz
                // Recursos publicos
                .antMatchers("/login", "/css/**", "/js/**", "/images/**",
                             "/webjars/**", "/h2-console/**", "/error").permitAll()
                // Areas por perfil
                .antMatchers("/dashboard/**").hasRole("GERENTE")
                .antMatchers("/pdv/**").hasRole("ATENDENTE")
                // Demais rotas exigem login
                .anyRequest().authenticated())
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("login")
                .passwordParameter("senha")
                // successHandler custom decide destino por perfil: GERENTE -> /dashboard,
                // ATENDENTE -> /pdv. Evita um redirect extra via "/".
                .successHandler(loginSuccessHandler)
                .failureUrl("/login?error")
                .permitAll())
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll());
    }

    @Bean
    @Override
    public org.springframework.security.authentication.AuthenticationManager
            authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}