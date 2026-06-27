package com.livrariaregional.web;

import com.livrariaregional.domain.Produto;
import com.livrariaregional.service.ProdutoService;
import com.livrariaregional.service.ProdutoService.CodigoDuplicadoException;
import com.livrariaregional.service.ProdutoService.ProdutoNaoEncontradoException;
import com.livrariaregional.web.dto.ProdutoForm;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

/**
 * Controller de Produto (GERENTE only - garantido pelo SecurityConfig em
 * /dashboard/**).
 *
 * Rotas:
 *   GET  /dashboard/produtos                 -> lista (com ?q= busca)
 *   GET  /dashboard/produtos/novo            -> form vazio
 *   POST /dashboard/produtos/novo            -> cria
 *   GET  /dashboard/produtos/{id}/editar     -> form preenchido
 *   POST /dashboard/produtos/{id}/editar     -> atualiza
 *   POST /dashboard/produtos/{id}/desativar  -> soft-delete
 */
@Controller
@RequestMapping("/dashboard/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public String listar(@RequestParam(name = "q", required = false) String q, Model model) {
        model.addAttribute("produtos", produtoService.buscarPorNome(q));
        model.addAttribute("q", q == null ? "" : q);
        return "produtos/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("form", new ProdutoForm());
        model.addAttribute("modoEdicao", false);
        return "produtos/form";
    }

    @PostMapping("/novo")
    public String criar(@Valid @ModelAttribute("form") ProdutoForm form,
                        BindingResult binding,
                        RedirectAttributes flash) {
        if (binding.hasErrors()) {
            return "produtos/form";
        }
        try {
            Produto p = produtoService.criar(form);
            flash.addFlashAttribute("success", "Produto " + p.getCodigo() + " cadastrado.");
            return "redirect:/dashboard/produtos";
        } catch (CodigoDuplicadoException ex) {
            binding.rejectValue("codigo", "codigo.duplicado", ex.getMessage());
            return "produtos/form";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes flash) {
        return produtoService.obter(id)
                .map(p -> {
                    model.addAttribute("form", ProdutoForm.fromEntity(p));
                    model.addAttribute("modoEdicao", true);
                    model.addAttribute("produtoId", p.getId());
                    return "produtos/form";
                })
                .orElseGet(() -> {
                    flash.addFlashAttribute("error", "Produto " + id + " nao encontrado.");
                    return "redirect:/dashboard/produtos";
                });
    }

    @PostMapping("/{id}/editar")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") ProdutoForm form,
                            BindingResult binding,
                            RedirectAttributes flash) {
        if (binding.hasErrors()) {
            return "produtos/form";
        }
        try {
            produtoService.atualizar(id, form);
            flash.addFlashAttribute("success", "Produto atualizado.");
            return "redirect:/dashboard/produtos";
        } catch (ProdutoNaoEncontradoException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
            return "redirect:/dashboard/produtos";
        } catch (CodigoDuplicadoException ex) {
            binding.rejectValue("codigo", "codigo.duplicado", ex.getMessage());
            return "produtos/form";
        }
    }

    @PostMapping("/{id}/desativar")
    public String desativar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            produtoService.desativar(id);
            flash.addFlashAttribute("success", "Produto desativado.");
        } catch (ProdutoNaoEncontradoException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/dashboard/produtos";
    }
}