# Livraria Regional

Sistema web para gestao de estoque e vendas da **Livraria Regional Ltda.** — 3 filiais, perfis gerente e atendente.

Projeto academico desenvolvido como MAPA da UniCesumar.

---

## Sobre este repositorio

Este repositorio guarda o codigo e a documentacao do projeto. A entrega sera feita em **PRs pequenos e incrementais**, conforme mostrado abaixo.

| PR | Conteudo | Status |
|---|---|---|
| PR1 | Documentacao inicial (docs/01-06) | merged |
| PR2 | Roadmap de execucao (este PR) | aberto |
| PR3 | Bootstrap MVC + JPA (entidades + repositories) | proximo |
| PR4 | Login + Spring Security | proximo |
| PR5 | Produtos (CRUD) + Estoque | proximo |
| PR6 | PDV (carrinho + finalizacao) | proximo |
| PR7 | Dashboard + Relatorios | proximo |
| PR8 | Instaladores + README final | proximo |

---

## Stack tecnologica prevista

- **Linguagem:** Java 17
- **Framework:** Spring Boot 2.7.2
- **Front-end:** Thymeleaf (server-side rendering, HTML + CSS)
- **Banco de dados:** H2 in-memory (zero instalacao)
- **Autenticacao:** Spring Security
- **Build:** Maven
- **Testes:** JUnit 5 + Mockito

---

## Como executar (previsão)

A forma definitiva de rodar o sistema sera documentada no PR9. Ate la, o basico sera:

```bash
mvn spring-boot:run
```

E abrir o navegador em `http://localhost:8080`.

---

## Usuarios de teste (previsao)

| Login | Senha | Perfil |
|---|---|---|
| `gerente` | `admin123` | GERENTE |
| `atendente` | `atendente123` | ATENDENTE |

---

## Documentacao

Os documentos detalhados estao na pasta [`docs/`](./docs/):

- [`docs/01-concepcao.md`](./docs/01-concepcao.md) — requisitos, escopo negativo
- [`docs/02-planejamento.md`](./docs/02-planejamento.md) — stack, cronograma
- [`docs/03-design-prototipacao.md`](./docs/03-design-prototipacao.md) — telas, modelo de dados
- [`docs/04-desenvolvimento-testes.md`](./docs/04-desenvolvimento-testes.md) — estrategia de testes
- [`docs/05-riscos-implantacao.md`](./docs/05-riscos-implantacao.md) — riscos e deploy
- [`docs/06-reflexao-final.md`](./docs/06-reflexao-final.md) — fase critica
- [`docs/PR2-roadmap-execucao.md`](./docs/PR2-roadmap-execucao.md) — roadmap de execucao (mapa PR x cronograma)

---

## Como contribuir via PRs

1. Cada PR e feito a partir de uma branch separada (ex: `feature/nome-da-coisa`).
2. O autor do PR faz **commit local** e abre o Pull Request no GitHub.
3. O responsavel pelo projeto revisa e faz o **merge** na `main`.
4. O proximo PR parte da `main` atualizada.

> **Convencao de commits:** `feat:`, `fix:`, `docs:`, `chore:`, `refactor:`.

---

## Licenca

Projeto academico — UniCesumar — MAPA 2026.