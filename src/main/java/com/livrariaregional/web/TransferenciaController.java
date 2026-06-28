package com.livrariaregional.web;

import com.livrariaregional.domain.Filial;
import com.livrariaregional.domain.Produto;
import com.livrariaregional.repository.FilialRepository;
import com.livrariaregional.repository.ProdutoRepository;
import com.livrariaregional.service.EstoqueService;
import com.livrariaregional.web.dto.TransferenciaForm;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller de Transferencia de Estoque entre filiais (GERENTE only).
 *
 * Rotas:
 *   GET  /dashboard/transferencia  -> form vazio
 *   POST /dashboard/transferencia  -> executa transferencia
 *
 * O SecurityConfig garante que apenas ROLE_GERENTE acessa este caminho.
 * Em caso de erro (estoque insuficiente, origem == destino etc.) o
 * service lanca excecao, a transacao sofre rollback e a mensagem
 * e exibida via flash attribute.
 */
@Controller
@RequestMapping("/dashboard/transferencia")
public class TransferenciaController {

    private final EstoqueService estoqueService;
    private final FilialRepository filialRepository;
    private final ProdutoRepository produtoRepository;

    public TransferenciaController(EstoqueService estoqueService,
                                   FilialRepository filialRepository,
                                   ProdutoRepository produtoRepository) {
        this.estoqueService = estoqueService;
        this.filialRepository = filialRepository;
        this.produtoRepository = produtoRepository;
    }

    @GetMapping
    public String form(Model model) {
        model.addAttribute("form", new TransferenciaForm());
        model.addAttribute("filiais", filialRepository.findByAtivoTrueOrderByNomeAsc());
        model.addAttribute("produtos", produtoRepository.findByAtivoTrueOrderByNomeAsc());
        return "transferencia/form";
    }

    @PostMapping
    public String transferir(@ModelAttribute("form") TransferenciaForm form,
                              RedirectAttributes flash) {
        if (form.getFilialOrigemId() == null || form.getFilialDestinoId() == null
                || form.getProdutoId() == null || form.getQuantidade() == null) {
            flash.addFlashAttribute("error", "Todos os campos sao obrigatorios.");
            return "redirect:/dashboard/transferencia";
        }
        Filial origem = filialRepository.findById(form.getFilialOrigemId()).orElse(null);
        Filial destino = filialRepository.findById(form.getFilialDestinoId()).orElse(null);
        Produto produto = produtoRepository.findById(form.getProdutoId()).orElse(null);
        if (origem == null || destino == null || produto == null) {
            flash.addFlashAttribute("error", "Filial ou produto invalido.");
            return "redirect:/dashboard/transferencia";
        }
        try {
            estoqueService.transferir(origem, destino, produto, form.getQuantidade());
            flash.addFlashAttribute("success",
                    "Transferencia de " + form.getQuantidade() + " unidade(s) de "
                            + produto.getNome() + " ("
                            + origem.getNome() + " -> " + destino.getNome() + ") concluida.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/dashboard/transferencia";
    }
}
