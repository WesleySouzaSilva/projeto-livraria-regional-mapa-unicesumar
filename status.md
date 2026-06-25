# Status do Projeto - Livraria Regional

> Arquivo de acompanhamento LOCAL. NAO sobe no Git (esta no .gitignore).
> Use este arquivo para saber onde parou e o que vem a seguir quando retomar o trabalho.
>
> Regra: ao finalizar uma PR (fechar/mergear), remova a entrada correspondente deste arquivo.

---

## Em andamento (PRs abertas, aguardando merge ou finalizacao)

- **PR3** - `pom.xml` + classe main + `application.yml` + 7 entidades JPA + 7 repositories + `data.sql` + teste de context loads
  - Decisao tecnica: hash BCrypt gerado em runtime via `DataBootstrap` (placeholder `{noop}VIRAR` no `data.sql` e bean gera o hash real no startup). Motivo: impossivel gerar BCrypt estatico sem rodar o encoder; alternativa dinamica garante que o login (PR #4) funcione sem SQL manual.

---

## Concluidas (ja merged em main)

- **PR1** - Documentacao inicial + `.gitignore` (README + pasta `docs/` completa)
- **PR2** - `docs/PR2-roadmap-execucao.md` (mapa PR x cronograma, 7 semanas)

---

## Proximas (roadmap, ordem de execucao)

- **PR4** - Spring Security + tela de login + 2 usuarios de teste (gerente/admin123, atendente/atendente123)
- **PR5** - Tela de Produtos (CRUD) + Estoque (consulta por filial) + transferencia entre filiais
- **PR6** - Tela de PDV (carrinho + finalizacao + baixa de estoque)
- **PR7** - Dashboard + Relatorios (faturamento por filial, top livros, melhores clientes) + `EXECUTAR.bat` / `EXECUTAR.sh`
- **PR8** - README final + validacao completa + smoke test

---

## Anotacoes / pendencias

- O modelo do TRABALHO menciona MySQL em producao. Decidimos manter **H2 in-memory tambem em producao** para o MVP academico (alinhado ao feedback de "stack simples, zero setup"). Justificativa: o TRABALHO e a documentacao academica; a implementacao prioriza demonstracao portatil.
- AWS foi removida do escopo de implementacao pelo mesmo motivo (sem deploy real, sem custo de infraestrutura).
- Transferencia de estoque entre filiais: sera implementada na PR #5 (campo/operacao no EstoqueService).