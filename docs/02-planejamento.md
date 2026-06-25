# 02 - Planejamento

## 2.1 Stack Tecnologica

| Camada | Tecnologia | Justificativa |
|---|---|---|
| Linguagem | Java 17 | LTS, estavel, exigido pelo Spring Boot moderno |
| Framework | Spring Boot 2.7.2 | Maduro, documentacao vasta, comunidade grande |
| Front-end | Thymeleaf | Templates HTML no servidor, sem JS complexo, rapido de aprender |
| Banco de dados | H2 in-memory | Zero instalacao; ideal para projeto academico e demonstracao |
| ORM | Spring Data JPA / Hibernate | Padrao do ecossistema Spring |
| Autenticacao | Spring Security | Integracao nativa com Spring, BCrypt para senhas |
| Build | Maven | Padrao no mundo Java |
| Testes | JUnit 5 + Mockito | Padrao no ecossistema Spring Boot |

## 2.2 Por que Thymeleaf e nao React?

O projeto precisa ser simples, academico e funcional. Thymeleaf:

- Permite renderizar paginas no servidor (server-side rendering).
- Nao exige build separado de front-end (sem npm, sem webpack).
- Java continua sendo a unica linguagem do projeto.
- Mais facil de hospedar (1 unico JAR roda tudo).
- Atende a todos os requisitos de UI sem complexidade desnecessaria.

React/Vue seria escolhido apenas se houvesse interatividade pesada no cliente (drag-and-drop, SPA offline, etc), o que nao e o caso.

## 2.3 Cronograma (previsão academica)

| Fase | Duracao estimada | Entrega |
|---|---|---|
| Concepcao e requisitos | 2 semanas | docs/01 |
| Modelagem e design | 2 semanas | docs/03 |
| Implementacao do MVP | 8 semanas | codigo funcional |
| Testes | 2 semanas | testes automatizados + manuais |
| Implantacao e treinamento | 2 semanas | deploy + manual |
| Total | 16 semanas (4 meses) | sistema em producao |

## 2.4 Equipe (previsão academica)

- 1 desenvolvedor full-stack
- 1 revisor / orientador
- Usuarios de teste (gerentes e atendentes das filiais reais, se houver)

## 2.5 Entregas incrementais (PRs deste repositorio)

| # | PR | Descricao |
|---|---|---|
| 1 | PR1 | Documentacao do projeto (este PR) |
| 2 | PR2 | Bootstrap Spring Boot + configuracoes |
| 3 | PR3 | Modelo de dados + carga inicial |
| 4 | PR4 | Login + perfis + seguranca |
| 5 | PR5 | Cadastro e consulta de produtos |
| 6 | PR6 | Estoque por filial |
| 7 | PR7 | PDV (registro de vendas) |
| 8 | PR8 | Dashboard e relatorios |
| 9 | PR9 | Empacotamento final + instalador local |