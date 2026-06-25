# PR2 - Roadmap de Execucao (Cronograma e Prazos)

> Este documento **NAO substitui** os docs 01-06. Ele traduz o cronograma definido em `docs/02-planejamento.md` em **estimativas de prazo por PR**, com entrega e justificativa. Pensado para um cliente final que precisa saber "quando recebo o que".

---

## 1. Resumo executivo

| Fase | Periodo | Duracao | Entrega principal |
|---|---|---|---|
| Documentacao | PR1 (concluido) | semana 1 | Escopo, requisitos, riscos, plano de testes |
| Roadmap | PR2 (este) | semana 1 | Cronograma detalhado, dependencias, criterios de aceite |
| Bootstrap | PR3 | semana 2 | Projeto Maven rodando, entidades JPA, banco populado |
| Autenticacao | PR4 | semana 3 | Tela de login funcional com 2 usuarios de teste |
| Modulo Produtos + Estoque | PR5 | semana 4 | CRUD de livros + consulta de estoque por filial |
| Modulo PDV | PR6 | semana 5 | Tela de venda com baixa automatica de estoque |
| Dashboard + Relatorios | PR7 | semana 6 | Visao geral e relatorios filtrados |
| Empacotamento | PR8 | semana 7 | Instaladores Windows/Linux, README final, smoke test |
| **Total** | **7 semanas** | | Sistema pronto para demonstracao academica |

---

## 2. Por que esse cronograma?

1. **7 semanas e o tempo real de um MAPA academico** — disciplina tem 8 semanas; reservamos a ultima para revisao e apresentacao.
2. **Cada PR cabe em 1 semana** — escopo pequeno (1-2 modulos) reduz risco de atraso e facilita correcao.
3. **Dependencias lineares (sem paralelismo)** — login depende do bootstrap; PDV depende de produtos+estoque. Sem dependencias cruzadas, sem tentar paralelizar.
4. **Ultima semana (PR8) e dedicada a empacotamento** — testes de smoke, scripts de execucao, README final. E a semana onde pegamos bugs latentes.

---

## 3. Detalhamento por PR

### PR1 — Documentacao inicial
- **Prazo**: semana 1 (dias 1-2)
- **Entrega**: 6 documentos cobrindo requisitos, planejamento, design, testes, riscos e fase critica.
- **Criterio de aceite**: todos os docs revisados e coerentes entre si.

### PR2 — Roadmap de execucao (este PR)
- **Prazo**: semana 1 (dias 3-4)
- **Entrega**: este documento com prazos, dependencias e criterios de aceite por PR.
- **Criterio de aceite**: cronograma aprovado antes de iniciar codigo.

### PR3 — Bootstrap Spring Boot + JPA
- **Prazo**: semana 2 (dias 1-5)
- **Entrega**:
  - `pom.xml` com Java 17 + Spring Boot 2.7.2 + H2 + Thymeleaf + JPA + Security + Validation
  - Classe `LivrariaRegionalApplication` (entrypoint)
  - `application.yml` com perfil H2 in-memory
  - 7 entidades JPA: `Filial`, `Usuario`, `Cliente`, `Produto`, `Estoque`, `Venda`, `ItemVenda`
  - 7 repositories Spring Data
  - `data.sql` populando 3 filiais, 10 livros, 2 usuarios (gerente e atendente)
  - Teste de context loads
- **Criterio de aceite**: `mvn spring-boot:run` sobe o app em `http://localhost:8080` e o H2 Console mostra as tabelas populadas.

### PR4 — Login + Spring Security
- **Prazo**: semana 3 (dias 1-5)
- **Entrega**:
  - `SecurityConfig` (form login + BCrypt)
  - `LoginController` (rota `/login`)
  - Template `login.html` (Thymeleaf)
  - `data.sql` ja existente gera os 2 usuarios com senha BCrypt
  - Teste de integracao: login com credenciais validas retorna 302 + cookie `JSESSIONID`; invalidas retorna erro
- **Criterio de aceite**: `gerente/admin123` entra no sistema; `atendente/atendente123` entra; acesso sem login redireciona para `/login`.

### PR5 — Produtos + Estoque
- **Prazo**: semana 4 (dias 1-7)
- **Entrega**:
  - `ProdutoController` (lista, busca, novo, editar, desativar) — apenas gerente pode escrever
  - `EstoqueController` (consulta por filial) — gerente ve todas, atendente ve so a dele
  - `ProdutoService` e `EstoqueService` com regras de negocio
  - DTOs: `ProdutoForm`
  - Templates: `produtos/lista.html`, `produtos/form.html`, `estoque.html`
  - CSS basico em `static/css/styles.css`
  - Testes unitarios nos services (calculo de estoque minimo, bloqueio de produto inativo)
  - Testes de integracao nos controllers (gerente cadastra, atendente e bloqueado, busca por nome funciona)
- **Criterio de aceite**: gerente cadastra um livro novo e ele aparece na lista de produtos e no estoque da filial selecionada.

### PR6 — PDV (Ponto de Venda)
- **Prazo**: semana 5 (dias 1-7)
- **Entrega**:
  - `PdvController` (tela de venda + endpoint de finalizacao)
  - `VendaService` (cria venda, baixa estoque atomicamente)
  - Excecao `EstoqueInsuficienteException` + handler global
  - DTOs: `ItemPdvDto`, `FinalizarVendaDto`
  - Template `pdv.html` com carrinho
  - JS minimo em `static/js/pdv.js` para o carrinho reativo
  - Teste unitario: venda sem estoque e bloqueada
  - Teste de integracao: venda com estoque decrementa o estoque
- **Criterio de aceite**: atendente registra uma venda de 2 unidades e o estoque da filial cai em 2 unidades atomicamente. Tentar vender sem estoque retorna erro claro.

### PR7 — Dashboard + Relatorios
- **Prazo**: semana 6 (dias 1-7)
- **Entrega**:
  - `DashboardService` (faturamento do dia, top 5 livros, estoque critico)
  - `RelatorioService` (vendas por periodo, vendas por filial)
  - `DashboardController` + `RelatorioController`
  - Templates: `dashboard.html`, `relatorios/vendas.html`, `relatorios/estoque-critico.html`
  - Filtros por data inicial/final e por filial
  - Testes unitarios nos services de agregacao
- **Criterio de aceite**: gerente ve o faturamento do dia atualizado; filtra vendas por periodo e por filial corretamente.

### PR8 — Empacotamento + Instaladores + Smoke Test
- **Prazo**: semana 7 (dias 1-5)
- **Entrega**:
  - `EXECUTAR.bat` (Windows) — build + run com 1 clique
  - `EXECUTAR.sh` (Linux/macOS) — equivalente
  - README final com instrucoes passo a passo de download, requisitos e execucao
  - Checklist de smoke test manual (5 fluxos)
  - Validacao completa: rodar `mvn clean test` e garantir que todos os testes passam
- **Criterio de aceite**: em uma maquina limpa (apenas com JDK 17), rodar `EXECUTAR.bat` (ou `.sh`) e o sistema abre no navegador com todas as funcionalidades descritas no checklist.

---

## 4. Dependencias entre PRs

```
PR1 -> PR2 -> PR3 -> PR4 -> PR5 -> PR6 -> PR7 -> PR8
```

- PR1 e PR2 sao independentes (documentacao).
- PR3 e pre-requisito de todos os outros (cria o esqueleto que sera preenchido).
- PR4 depende de PR3 (precisa das entidades `Usuario`).
- PR5 depende de PR4 (precisa do login para segregar permissoes gerente/atendente).
- PR6 depende de PR5 (precisa de produtos e estoque para vender).
- PR7 depende de PR6 (precisa de vendas para calcular faturamento e relatorios).
- PR8 depende de todos os anteriores (empacota tudo).

Nenhum PR pode ser pulado ou invertido sem retrabalho.

---

## 5. Criterios de aceite transversais (todos os PRs)

- [ ] Compila com `mvn clean package`
- [ ] Roda com `mvn spring-boot:run` em `http://localhost:8080`
- [ ] Pelo menos 1 teste automatizado novo
- [ ] Sem regressao nos testes anteriores
- [ ] Branch `feature/prN-<slug>` mergeada na `main` antes do proximo PR

---

## 6. Riscos do cronograma (referencia docs/05)

| Risco | Impacto no cronograma | Mitigacao |
|---|---|---|
| PR3 atrasar (problemas com JPA + H2) | Atraso em cadeia | Manter PR3 o mais simples possivel; sem logica de negocio nesse PR |
| PR5 ou PR6 crescer demais | Atraso de 1 semana | Dividir em 2 PRs se necessario (ex.: PR5a produtos, PR5b estoque) |
| PR8 exigir ajustes em PRs anteriores | Atraso de ate 1 semana | Reservei 1 semana de folga entre PR7 e PR8 implicita na duracao total |

Se um PR atrasar, o proximo PR NAO e iniciado ate o anterior estar merged.

---

## 7. Nomes de branches (a partir de agora)

- `feature/pr3-bootstrap-mvc-jpa`
- `feature/pr4-login-seguranca`
- `feature/pr5-produtos-estoque`
- `feature/pr6-pdv-vendas`
- `feature/pr7-dashboard-relatorios`
- `feature/pr8-instaladores-readme`

## 8. Titulos de PR

- `PR #3 - Bootstrap MVC + JPA (entidades + repositories + data.sql)`
- `PR #4 - Login + Spring Security (2 usuarios de teste)`
- `PR #5 - Produtos (CRUD) + Estoque (consulta por filial)`
- `PR #6 - PDV (carrinho + finalizacao + baixa automatica)`
- `PR #7 - Dashboard + Relatorios (filtros por periodo e filial)`
- `PR #8 - Instaladores (EXECUTAR.bat/sh) + README final + smoke test`
