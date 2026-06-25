package com.livrariaregional.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Bean de PasswordEncoder disponibilizado desde o bootstrap.
 *
 * A configuracao completa do Spring Security (form login, autorizacao por
 * perfil, etc.) sera adicionada na PR #4. Por enquanto, exponho apenas o
 * encoder para que o DataBootstrap consiga gerar hashes BCrypt e o teste de
 * context loads consiga subir o contexto sem erros.
 */
@Configuration
public class SecurityBeans {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}