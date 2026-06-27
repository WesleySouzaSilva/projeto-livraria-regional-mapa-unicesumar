package com.livrariaregional.web;

import com.livrariaregional.domain.Filial;
import com.livrariaregional.domain.Usuario;
import com.livrariaregional.repository.FilialRepository;
import com.livrariaregional.repository.UsuarioRepository;
import com.livrariaregional.service.EstoqueService;
import com.livrariaregional.web.dto.EstoqueView;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

/**
 * Controller de Estoque.
 *
 * Regra de visibilidade (PR #5):
 *   - GERENTE: ve todas as filiais. Pode filtrar por ?filialId=...
 *   - ATENDENTE: ve SOMENTE a sua filial (ignora ?filialId).
 *
 * Lancamos erro explicito se o atendente nao tem filial associada
 * (estado inconsistente de dados; na pratica o seed garante filial).
 */
@Controller
@RequestMapping("/dashboard/estoque")
public class EstoqueController {

    private final EstoqueService estoqueService;
    private final FilialRepository filialRepository;
    private final UsuarioRepository usuarioRepository;

    public EstoqueController(EstoqueService estoqueService,
                             FilialRepository filialRepository,
                             UsuarioRepository usuarioRepository) {
        this.estoqueService = estoqueService;
        this.filialRepository = filialRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public String estoque(@RequestParam(name = "filialId", required = false) Long filialId,
                          Authentication authentication,
                          Model model) {
        boolean isGerente = temPerfil(authentication, "GERENTE");

        if (isGerente) {
            // GERENTE: lista todas filiais (dropdown) e, se filialId vier,
            // filtra; senao mostra todas as filiais (lista expandida).
            List<Filial> filiais = filialRepository.findByAtivoTrueOrderByNomeAsc();
            model.addAttribute("filiais", filiais);
            model.addAttribute("isGerente", true);

            if (filialId != null) {
                Optional<Filial> f = filialRepository.findById(filialId);
                if (f.isPresent()) {
                    model.addAttribute("filialSelecionada", f.get());
                    model.addAttribute("linhas", estoqueService.listarPorFilial(f.get()));
                } else {
                    model.addAttribute("linhas", List.<EstoqueView>of());
                }
            } else {
                // Sem filtro: agrega todas as filiais em uma lista
                model.addAttribute("linhas", estoqueService.listarTodas());
            }
        } else {
            // ATENDENTE: pega a filial do usuario
            Usuario u = usuarioRepository.findByLogin(authentication.getName())
                    .orElseThrow(() -> new IllegalStateException("Usuario logado nao encontrado: " + authentication.getName()));
            Filial filial = u.getFilial();
            if (filial == null) {
                throw new IllegalStateException("Atendente sem filial associada: " + u.getLogin());
            }
            model.addAttribute("isGerente", false);
            model.addAttribute("filialSelecionada", filial);
            model.addAttribute("linhas", estoqueService.listarPorFilial(filial));
        }

        return "estoque";
    }

    private boolean temPerfil(Authentication authentication, String perfil) {
        if (authentication == null) return false;
        return authentication.getAuthorities().stream()
                .anyMatch(a -> ("ROLE_" + perfil).equals(a.getAuthority()));
    }
}