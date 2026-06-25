# 05 - Riscos e Implantacao

## 5.1 Riscos Identificados

| # | Risco | Probabilidade | Impacto | Mitigacao |
|---|---|---|---|---|
| R1 | Resistencia dos funcionarios ao novo sistema | Media | Alto | Treinamento presencial + manual simples + periodo de adaptacao com sistema antigo em paralelo |
| R2 | Queda de energia / perda de dados | Media | Alto | Banco H2 com backup diario via script; em producao real, usar MySQL com replicacao |
| R3 | Erro de digitacao no PDV | Alta | Medio | Confirmacao da venda antes de finalizar; possibilidade de cancelar |
| R4 | Acesso indevido a dados de outra filial | Baixa | Alto | Filtro automatico por filial no backend; testes de permissao |
| R5 | Mudanca de requisitos durante o desenvolvimento | Alta | Medio | Documentacao detalhada por PR; revisao semanal |
| R6 | Atraso no cronograma | Media | Medio | PRs pequenos e priorizacao do MVP; deixar features secundarias para versoes futuras |

## 5.2 Plano de Implantacao (previsão)

### 5.2.1 Ambiente de demonstracao (academico)
- Rodar localmente com H2 in-memory.
- 1 unico JAR auto-suficiente.
- Script `EXECUTAR.bat` / `EXECUTAR.sh` para iniciar com 1 clique.

### 5.2.2 Ambiente de producao (previsão futura, fora do escopo do MAPA)
- Backend em container Docker.
- Banco MySQL gerenciado (AWS RDS ou similar).
- Front-end no mesmo JAR (ja renderizado pelo Thymeleaf).
- Deploy via CI/CD (GitHub Actions).

### 5.2.3 Treinamento (previsão)
- 1 sessao de 1h com cada atendente.
- Manual em PDF com screenshots.
- Video de 5 min mostrando o fluxo principal.

## 5.3 Custos estimados (previsão academica)

Como o projeto usa H2 in-memory e roda localmente, o custo de demonstracao e **zero**.

Para um cenario de producao real:

| Item | Custo mensal estimado |
|---|---|
| AWS EC2 t3.small (backend) | USD 15 |
| AWS RDS MySQL db.t3.micro | USD 15 |
| Dominio + SSL | USD 1 |
| Backup S3 | USD 1 |
| **Total** | **~USD 32/mes** |

No primeiro ano com Free Tier da AWS, custo efetivo: **~USD 0**.

## 5.4 Rollback

Como cada PR e isolado e compilado independentemente, rollback e feito via:

```bash
git revert <commit-hash>
```

Ou voltando para a tag da versao anterior.