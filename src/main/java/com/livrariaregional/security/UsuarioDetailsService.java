package com.livrariaregional.security;

import com.livrariaregional.domain.Usuario;
import com.livrariaregional.repository.UsuarioRepository;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Carrega o Usuario do banco (via UsuarioRepository) e o converte para
 * UserDetails do Spring Security.
 *
 * Decisao de design: ao inves de criar uma classe que implementa
 * UserDetails e armazena o Usuario inteiro (com @Entity, lazy relations,
 * etc.) na sessao, eu traduzo para o User "leve" do proprio Spring
 * Security. Isso evita que entidades JPA "grudadas" na sessao HTTP
 * sejam usadas fora de uma transacao (LazyInitializationException),
 * e mantem o principio de manter o modelo de dominio separado da
 * infraestrutura de seguranca.
 *
 * Mapping de perfis -> authorities:
 *   GERENTE    -> ROLE_GERENTE
 *   ATENDENTE  -> ROLE_ATENDENTE
 *
 * O prefixo ROLE_ e convencao do Spring Security: usado pelo hasRole(...)
 * e ja removido nas expressions. Sem o prefixo seria hasAuthority(...).
 */
@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Usuario u = usuarioRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario nao encontrado: " + login));

        // Usuario inativo (soft-delete) nao pode logar.
        if (Boolean.FALSE.equals(u.getAtivo())) {
            throw new UsernameNotFoundException(
                    "Usuario desativado: " + login);
        }

        GrantedAuthority authority = new SimpleGrantedAuthority(
                "ROLE_" + u.getPerfil().name());

        return User
                .withUsername(u.getLogin())
                .password(u.getSenha())
                .authorities(List.of(authority))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(Boolean.FALSE.equals(u.getAtivo()))
                .build();
    }
}