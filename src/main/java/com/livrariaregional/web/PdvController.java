package com.livrariaregional.web;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.livrariaregional.domain.Cliente;
import com.livrariaregional.domain.Filial;
import com.livrariaregional.domain.Produto;
import com.livrariaregional.domain.Usuario;
import com.livrariaregional.domain.Venda;
import com.livrariaregional.repository.ClienteRepository;
import com.livrariaregional.repository.ProdutoRepository;
import com.livrariaregional.repository.UsuarioRepository;
import com.livrariaregional.service.VendaService;
import com.livrariaregional.web.dto.CarrinhoView;
import com.livrariaregional.web.dto.ItemCarrinho;
import com.livrariaregional.web.dto.VendaView;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller do PDV (Ponto de Venda).
 *
 * Rotas (todas exigem login — SecurityConfig limita /pdv/** a ATENDENTE/GERENTE):
 *   GET  /pdv                       -> catalogo + carrinho
 *   POST /pdv/adicionar             -> adiciona item (produtoId, quantidade)
 *   POST /pdv/remover/{index}       -> remove item do carrinho
 *   POST /pdv/finalizar             -> chama VendaService.finalizar
 *   GET  /pdv/recibo/{id}           -> exibe recibo da venda
 *   GET  /pdv/historico             -> vendas do atendente logado
 *
 * O carrinho (CarrinhoView) e mantido em @SessionAttributes("carrinho"),
 * o que faz o Spring guarda-lo na HttpSession entre requests. Quando a
 * venda e finalizada, SessionStatus.setComplete() remove o atributo
 * da sessao, "esvaziando" o carrinho para a proxima venda.
 *
 * Decisao de UX: o atendente vende SEM cliente por padrao (clienteId
 * opcional). Isso evita friccao em compras de balcao onde o cliente
 * nao quer se identificar.
 */
@Controller
@RequestMapping("/pdv")
@SessionAttributes("carrinho")
public class PdvController {

    private final VendaService vendaService;
    private final ProdutoRepository produtoRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;

    public PdvController(VendaService vendaService,
                         ProdutoRepository produtoRepository,
                         ClienteRepository clienteRepository,
                         UsuarioRepository usuarioRepository) {
        this.vendaService = vendaService;
        this.produtoRepository = produtoRepository;
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Garante que toda sessao HTTP tenha um CarrinhoView disponivel.
     * O Spring invoca este metodo antes de qualquer handler que tenha
     * @ModelAttribute("carrinho") explicito ou @SessionAttributes("carrinho").
     */
    @org.springframework.web.bind.annotation.ModelAttribute("carrinho")
    public CarrinhoView carrinho() {
        return new CarrinhoView();
    }

    @GetMapping
    public String index(Model model, @AuthenticationPrincipal UserDetails principal) {
        // Catalogo: todos os produtos ativos (atendente filtra na memoria se quiser)
        List<Produto> produtos = produtoRepository.findByAtivoTrueOrderByNomeAsc();
        Map<Long, Produto> mapa = new LinkedHashMap<>();
        for (Produto p : produtos) {
            mapa.put(p.getId(), p);
        }
        model.addAttribute("produtos", produtos);
        model.addAttribute("mapaProdutos", mapa);

        // Lista de clientes para o select (opcional)
        model.addAttribute("clientes", clienteRepository.findByAtivoTrueOrderByNomeAsc());

        // Filial do atendente para exibicao
        Usuario u = usuarioRepository.findByLogin(principal.getUsername()).orElse(null);
        model.addAttribute("atendente", u);

        return "pdv/index";
    }

    @PostMapping("/adicionar")
    public String adicionar(@RequestParam Long produtoId,
                            @RequestParam Integer quantidade,
                            @org.springframework.web.bind.annotation.ModelAttribute("carrinho") CarrinhoView carrinho,
                            RedirectAttributes flash) {
        if (quantidade == null || quantidade <= 0) {
            flash.addFlashAttribute("error", "Quantidade deve ser maior que zero.");
            return "redirect:/pdv";
        }
        Produto p = produtoRepository.findById(produtoId).orElse(null);
        if (p == null) {
            flash.addFlashAttribute("error", "Produto nao encontrado: id=" + produtoId);
            return "redirect:/pdv";
        }
        carrinho.addItem(produtoId, quantidade, p.getPreco());
        flash.addFlashAttribute("success", p.getNome() + " adicionado ao carrinho.");
        return "redirect:/pdv";
    }

    @PostMapping("/remover/{index}")
    public String remover(@PathVariable int index,
                          @org.springframework.web.bind.annotation.ModelAttribute("carrinho") CarrinhoView carrinho,
                          RedirectAttributes flash) {
        carrinho.remover(index);
        flash.addFlashAttribute("success", "Item removido.");
        return "redirect:/pdv";
    }

    @PostMapping("/finalizar")
    public String finalizar(@RequestParam(name = "clienteId", required = false) Long clienteId,
                            @org.springframework.web.bind.annotation.ModelAttribute("carrinho") CarrinhoView carrinho,
                            @AuthenticationPrincipal UserDetails principal,
                            RedirectAttributes flash,
                            SessionStatus sessionStatus) {
        if (carrinho.isVazio()) {
            flash.addFlashAttribute("error", "Carrinho vazio. Adicione produtos antes de finalizar.");
            return "redirect:/pdv";
        }
        Usuario usuario = usuarioRepository.findByLogin(principal.getUsername())
                .orElseThrow(() -> new IllegalStateException("Usuario logado nao encontrado: " + principal.getUsername()));
        Filial filial = usuario.getFilial();
        if (filial == null) {
            flash.addFlashAttribute("error", "Usuario sem filial vinculada. Contate o gerente.");
            return "redirect:/pdv";
        }
        Cliente cliente = null;
        if (clienteId != null) {
            cliente = clienteRepository.findById(clienteId).orElse(null);
        }
        try {
            List<ItemCarrinho> itens = carrinho.getItens();
            Venda venda = vendaService.finalizar(filial, usuario, cliente, itens);
            // Limpa carrinho da sessao
            sessionStatus.setComplete();
            flash.addFlashAttribute("success", "Venda #" + venda.getId() + " concluida.");
            return "redirect:/pdv/recibo/" + venda.getId();
        } catch (IllegalStateException | IllegalArgumentException ex) {
            // Estoque insuficiente / produto inexistente / qtd invalida — rollback ja feito
            flash.addFlashAttribute("error", ex.getMessage());
            return "redirect:/pdv";
        }
    }

    @GetMapping("/recibo/{id}")
    public String recibo(@PathVariable Long id, Model model, RedirectAttributes flash) {
        try {
            Venda venda = vendaService.buscarPorId(id);
            model.addAttribute("venda", VendaView.from(venda));
            return "pdv/recibo";
        } catch (IllegalStateException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
            return "redirect:/pdv";
        }
    }

    @GetMapping("/historico")
    public String historico(@AuthenticationPrincipal UserDetails principal, Model model) {
        Usuario usuario = usuarioRepository.findByLogin(principal.getUsername())
                .orElseThrow(() -> new IllegalStateException("Usuario logado nao encontrado"));
        List<Venda> vendas = vendaService.listarPorUsuario(usuario.getId());
        model.addAttribute("vendas", vendas);
        return "pdv/historico";
    }
}
