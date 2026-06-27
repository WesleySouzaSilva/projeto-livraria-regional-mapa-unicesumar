package com.livrariaregional.service;

import com.livrariaregional.domain.Produto;
import com.livrariaregional.repository.ProdutoRepository;
import com.livrariaregional.service.ProdutoService.CodigoDuplicadoException;
import com.livrariaregional.web.dto.ProdutoForm;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
// @DirtiesContext forc a criacao de um ApplicationContext novo APOS cada teste,
// descartando o H2 in-memory junto. Sem isso, testes que desativam/criam
// produtos vazam estado para os proximos testes da mesma classe (mesma JVM,
// mesmo context cacheado). Custo: ~2s por teste, OK para 5 testes.
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProdutoServiceTest {

    @Autowired
    private ProdutoService service;

    @Autowired
    private ProdutoRepository repository;

    @Test
    void deveListarApenasProdutosAtivosOrdenadosPorNome() {
        var ativos = service.listarAtivos();
        assertThat(ativos).hasSize(10); // seed inicial (10 produtos LV001-LV010)
        assertThat(ativos).isSortedAccordingTo((a, b) -> a.getNome().compareTo(b.getNome()));
    }

    @Test
    void deveBuscarPorNomeCaseInsensitive() {
        var achados = service.buscarPorNome("sertao");
        assertThat(achados).hasSize(1);
        assertThat(achados.get(0).getNome()).containsIgnoringCase("Sertao");
    }

    @Test
    void deveCriarProdutoComCodigoUnico() {
        ProdutoForm form = new ProdutoForm();
        // Codigo unico (UUID) para evitar colisao com runs anteriores
        // do surefire que compartilham o H2 in-memory via DB_CLOSE_DELAY=-1.
        form.setCodigo("T" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        form.setNome("Livro de Teste");
        form.setCategoria("Teste");
        form.setPreco(new BigDecimal("19.90"));
        form.setEstoqueMinimo(1);

        Produto criado = service.criar(form);

        assertThat(criado.getId()).isNotNull();
        assertThat(criado.getAtivo()).isTrue();
    }

    @Test
    void deveFalharAoCriarComCodigoDuplicado() {
        // Cria com codigo novo (UUID), depois tenta criar OUTRO com o mesmo
        // codigo. A UNIQUE constraint do banco deve disparar
        // CodigoDuplicadoException na segunda chamada.
        String codigo = "T" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        ProdutoForm form = new ProdutoForm();
        form.setCodigo(codigo);
        form.setNome("Duplicado");
        form.setCategoria("Teste");
        form.setPreco(new BigDecimal("10.00"));
        form.setEstoqueMinimo(1);

        service.criar(form); // primeira chamada: sucesso
        assertThatThrownBy(() -> service.criar(form))
                .isInstanceOf(CodigoDuplicadoException.class);
    }

    @Test
    void deveDesativarProduto() {
        // pega o primeiro produto do seed
        Produto p = service.listarAtivos().get(0);
        Long id = p.getId();
        service.desativar(id);
        Optional<Produto> depois = repository.findById(id);
        assertThat(depois).isPresent();
        assertThat(depois.get().getAtivo()).isFalse();
    }
}