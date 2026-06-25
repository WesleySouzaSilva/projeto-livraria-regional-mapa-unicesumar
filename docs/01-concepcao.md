# 01 - Concepcao do Sistema

## 1.1 Requisitos Funcionais

O sistema deve permitir:

| Codigo | Requisito |
|---|---|
| RF01 | Login com usuario e senha, com 2 perfis: GERENTE e ATENDENTE |
| RF02 | Cada atendente esta vinculado a uma filial; gerente ve dados de todas as filiais |
| RF03 | Cadastro de produtos (codigo, nome, categoria, autor, preco) — apenas gerente |
| RF04 | Listagem e busca de produtos — gerente e atendente |
| RF05 | Controle de estoque por filial (quantidade por livro em cada loja) |
| RF06 | Alerta visual de estoque baixo |
| RF07 | Registro de venda (PDV) — selecionar produtos, quantidades e finalizar |
| RF08 | Baixa automatica no estoque ao registrar a venda |
| RF09 | Bloquear venda quando nao houver estoque suficiente |
| RF10 | Dashboard com faturamento do dia e top 5 produtos |
| RF11 | Relatorio de vendas por periodo — apenas gerente |

## 1.2 Requisitos Nao Funcionais

| Codigo | Requisito |
|---|---|
| RNF01 | Sistema web acessivel via navegador |
| RNF02 | Banco de dados embutido (sem instalacao externa) |
| RNF03 | Senhas armazenadas com hash (BCrypt) |
| RNF04 | Interface simples, funcional, em portugues |
| RNF15 | Tempo de resposta inferior a 2 segundos para operacoes comuns |
| RNF06 | Disponivel 100% local (offline) |

## 1.3 Escopo Negativo (o que NAO sera feito neste projeto)

- NAO havera integracao com meios de pagamento reais (cartao, PIX, etc).
- NAO havera emissao de nota fiscal eletronica.
- NAO havera app mobile nativo.
- NAO havera sincronizacao em nuvem.
- NAO havera integracao com sistemas de terceiros.
- NAO havera sistema de promocoes / cupons.
- NAO havera fluxo de devolucao de produtos.
- NAO havera transferencia automatica de estoque entre filiais (apenas registro manual).

## 1.4 Atores

- **Gerente** — administrador da rede. Acessa todas as filiais. Pode cadastrar produtos, ver relatorios, gerenciar usuarios.
- **Atendente** — operador de caixa. Acessa apenas a filial em que trabalha. Pode consultar produtos e registrar vendas.

## 1.5 Filiais (escopo)

- Filial 1 — Centro
- Filial 2 — Zona Norte
- Filial 3 — Zona Sul

Todas em Sao Paulo. Sistema preparado para escalar para mais filiais no futuro (configuracao por dados, nao hardcoded).