# 03 - Design e Prototipacao

## 3.1 Telas do Sistema (minimo 6)

| # | Tela | Quem acessa | Descricao |
|---|---|---|---|
| 1 | Login | Todos | Formulario de autenticacao |
| 2 | Dashboard | Gerente e Atendente | Visao geral: faturamento do dia, top 5 livros, estoque critico |
| 3 | Produtos | Gerente (CRUD) e Atendente (somente leitura) | Lista, busca, cadastro e edicao de livros |
| 4 | Estoque | Gerente (rede) e Atendente (propria filial) | Consulta de quantidade por filial |
| 5 | PDV (Ponto de Venda) | Gerente e Atendente | Carrinho + finalizacao da venda |
| 6 | Relatorios | Apenas Gerente | Vendas por periodo, top produtos, estoque critico |

## 3.2 Wireframes (descricao textual)

### 3.2.1 Login
- Campo "Usuario"
- Campo "Senha"
- Botao "Entrar"
- Mensagem de erro caso credenciais invalidas

### 3.2.2 Dashboard
- Cabecalho com nome do usuario, filial e botao "Sair"
- Cards: "Faturamento Hoje", "Vendas Hoje", "Itens Vendidos", "Estoque Critico"
- Tabela "Top 5 Produtos Mais Vendidos (hoje)"
- Lista "Produtos com Estoque Baixo"

### 3.2.3 Produtos
- Cabecalho
- Barra de busca (codigo ou nome)
- Botao "Novo Produto" (apenas gerente)
- Tabela: Codigo, Nome, Categoria, Autor, Preco, Acoes

### 3.2.4 Estoque
- Cabecalho
- Filtro de filial (apenas gerente)
- Tabela: Produto, Filial, Quantidade, Estoque Minimo, Status (critico / normal)

### 3.2.5 PDV
- Cabecalho
- Lista "Produtos Disponiveis" (com campo de busca)
- Carrinho: produto, quantidade, valor unitario, subtotal
- Botao "Finalizar Venda" (bloqueado se carrinho vazio)

### 3.2.6 Relatorios
- Cabecalho
- Filtro de periodo (data inicial e final)
- Filtro de filial
- Tabela: data, filial, valor total, itens vendidos

## 3.3 Modelo de Dados (resumo)

Entidades principais:

- **Filial** (id, nome, endereco, cidade, telefone, ativo)
- **Usuario** (id, nome, login, senha_hash, perfil, filial_id, ativo)
- **Cliente** (id, nome, cpf, telefone, email, filial_id, criado_em, ativo)
- **Produto** (id, codigo, nome, categoria, autor, preco, ativo)
- **Estoque** (id, produto_id, filial_id, quantidade, estoque_minimo) — unico por (produto, filial)
- **Venda** (id, data_venda, valor_total, cliente_id, usuario_id, filial_id, status)
- **ItemVenda** (id, venda_id, produto_id, quantidade, valor_unitario, subtotal)

O diagrama ER detalhado sera incluido no PR3 junto com o schema SQL.