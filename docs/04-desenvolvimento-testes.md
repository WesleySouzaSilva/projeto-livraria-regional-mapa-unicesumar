# 04 - Desenvolvimento e Testes

## 4.1 Metodologia

- Desenvolvimento incremental em **PRs pequenos** (maximo 1-2 modulos por PR).
- Cada PR deve compilar e rodar localmente.
- Cada PR adiciona **pelo menos 1 teste** automatizado.
- Convensoes de commit:
  - `feat:` nova funcionalidade
  - `fix:` correcao de bug
  - `docs:` apenas documentacao
  - `chore:` tarefas de infraestrutura
  - `refactor:` refatoracao sem mudar comportamento

## 4.2 Estrategia de Testes

### 4.2.1 Testes unitarios
- Services com Mockito (sem subir contexto Spring).
- Foco em regras de negocio:
  - Calculo de valor total da venda.
  - Baixa de estoque.
  - Bloqueio de venda sem estoque.
  - Hash de senha.

### 4.2.2 Testes de integracao
- Controllers com `@SpringBootTest` + `@AutoConfigureMockMvc`.
- Banco H2 em memoria separado (perfil `test`).
- Fluxos end-to-end:
  - Login retorna 302 + cookie.
  - PDV: registrar venda devolve 200 e estoque decrementa.

### 4.2.3 Testes manuais (smoke test)
- Script `EXECUTAR.bat` / `EXECUTAR.sh` (entregue no PR9) permite rodar o sistema com 1 clique.
- Checklist manual:
  - Login com `gerente/admin123` e ver dashboard com 3 filiais.
  - Login com `atendente/atendente123` e ver apenas 1 filial.
  - Cadastrar produto, ver na lista.
  - Registrar venda, ver estoque decrementar.
  - Tentar vender produto sem estoque, ver bloqueio.

## 4.3 Cenarios de teste por modulo (planejados)

| Modulo | Cenarios |
|---|---|
| Login | (1) usuario valido entra; (2) usuario invalido recebe erro; (3) acesso sem login redireciona |
| Produtos | (1) gerente cadastra; (2) atendente tenta cadastrar e e bloqueado; (3) busca por nome |
| Estoque | (1) gerente ve 3 filiais; (2) atendente ve so a dele; (3) alerta visual de estoque baixo |
| PDV | (1) venda com estoque suficiente; (2) venda sem estoque bloqueada; (3) estoque decrementa apos venda |
| Relatorios | (1) filtro por periodo; (2) filtro por filial |

## 4.4 Cobertura minima

- 70% de cobertura no service layer.
- 100% dos controllers com ao menos 1 teste de integracao.