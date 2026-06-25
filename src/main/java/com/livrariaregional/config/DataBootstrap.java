package com.livrariaregional.config;

import com.livrariaregional.domain.Usuario;
import com.livrariaregional.repository.UsuarioRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataBootstrap implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataBootstrap.class);
    private static final String PLACEHOLDER = "__BOOTSTRAP__";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataBootstrap(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        atualizarSenhaSePlaceholder("gerente", "admin123");
        atualizarSenhaSePlaceholder("atendente", "atendente123");
    }

    private void atualizarSenhaSePlaceholder(String login, String senhaPlana) {
        usuarioRepository.findByLogin(login).ifPresent(u -> {
            if (PLACEHOLDER.equals(u.getSenha())) {
                u.setSenha(passwordEncoder.encode(senhaPlana));
                usuarioRepository.save(u);
                log.info("Senha do usuario '{}' inicializada via BCrypt (placeholder detectado).", login);
            }
        });
    }
}