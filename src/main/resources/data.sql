-- ============================================================
-- Data inicial - Livraria Regional
-- Update 4.1: alinhado ao MODELO DE DADOS do TRABALHO
--   Filial:    id, nome, endereco
--   Produto:   id, codigo, nome, categoria, preco
--   Cliente:   id, nome, telefone, email, cpf_cnpj (campo enriquecido)
--   Usuario:   id, nome, login, senha, perfil, filial_id (campo enriquecido)
--   Estoque:   id, filial_id, produto_id, quantidade
--   Venda:     id, data_venda, valor_total, cliente_id, usuario_id, filial_id
--   Item_Venda:id, venda_id, produto_id, quantidade, valor_unitario
-- ============================================================

-- =========================
-- Filiais (3 filiais)
-- =========================
INSERT INTO filial (id, nome, endereco, ativo) VALUES (1, 'Livraria Regional - Centro',         'Rua das Flores, 123 - Centro, Maringa-PR', TRUE);
INSERT INTO filial (id, nome, endereco, ativo) VALUES (2, 'Livraria Regional - Zona Norte',     'Av. Brasil, 4500 - Zona Norte, Maringa-PR', TRUE);
INSERT INTO filial (id, nome, endereco, ativo) VALUES (3, 'Livraria Regional - Londrina',        'Av. Higienopolis, 88 - Centro, Londrina-PR', TRUE);

-- =========================
-- Produtos (10 livros)
-- Cada livro tem codigo unico para busca rapida no PDV.
-- =========================
INSERT INTO produto (id, codigo, nome, autor, isbn, categoria, preco, estoque_minimo, ativo) VALUES
  (1,  'LV001', 'O Continente',                'Erico Verissimo',          '978-85-01-00001-1', 'Romance Nacional',     59.90, 3, TRUE),
  (2,  'LV002', 'Grande Sertao: Veredas',     'Guimaraes Rosa',           '978-85-01-00002-8', 'Romance Nacional',     79.90, 3, TRUE),
  (3,  'LV003', 'Capitaes da Areia',          'Jorge Amado',              '978-85-01-00003-5', 'Romance Nacional',     49.90, 3, TRUE),
  (4,  'LV004', 'Memorial do Convento',       'Jose Saramago',            '978-85-01-00004-2', 'Romance Estrangeiro',  69.90, 3, TRUE),
  (5,  'LV005', 'Cem Anos de Solidao',        'Gabriel Garcia Marquez',   '978-85-01-00005-9', 'Romance Estrangeiro',  89.90, 3, TRUE),
  (6,  'LV006', '1984',                       'George Orwell',            '978-85-01-00006-6', 'Distopia',             39.90, 5, TRUE),
  (7,  'LV007', 'A Revolucao dos Bichos',     'George Orwell',            '978-85-01-00007-3', 'Distopia',             34.90, 5, TRUE),
  (8,  'LV008', 'O Pequeno Principe',         'Antoine de Saint-Exupery', '978-85-01-00008-0', 'Infantil',             29.90, 5, TRUE),
  (9,  'LV009', 'Clean Code',                 'Robert C. Martin',         '978-85-01-00009-7', 'Tecnologia',          119.90, 2, TRUE),
  (10, 'LV010', 'Domain-Driven Design',       'Eric Evans',               '978-85-01-00010-3', 'Tecnologia',          139.90, 2, TRUE);

-- =========================
-- Estoque por filial x produto
-- Filial 1 (Centro): quantidades cheias
-- Filial 2 (Zona Norte): alguns abaixo do minimo propositalmente (gera alerta)
-- Filial 3 (Londrina): distribuicao variada
-- =========================
INSERT INTO estoque (filial_id, produto_id, quantidade) VALUES
  -- Filial 1 (Centro) - sadia
  (1, 1, 15), (1, 2, 12), (1, 3, 18), (1, 4, 10), (1, 5, 8),
  (1, 6, 20), (1, 7, 22), (1, 8, 25), (1, 9, 7),  (1, 10, 6),
  -- Filial 2 (Zona Norte) - com itens abaixo do minimo (dispara alerta no dashboard)
  (2, 1, 2),  (2, 2, 4),  (2, 3, 1),  (2, 4, 6),  (2, 5, 3),
  (2, 6, 8),  (2, 7, 9),  (2, 8, 10), (2, 9, 2),  (2, 10, 1),
  -- Filial 3 (Londrina) - distribuicao variada
  (3, 1, 9),  (3, 2, 7),  (3, 3, 6),  (3, 4, 4),  (3, 5, 5),
  (3, 6, 11), (3, 7, 13), (3, 8, 14), (3, 9, 3),  (3, 10, 4);

-- =========================
-- Usuarios de teste
-- gerente/admin123        -> perfil GERENTE, sem filial fixa (gerente ve todas)
-- atendente/atendente123  -> perfil ATENDENTE, filial 1 (Centro)
--
-- Senhas serao definidas em runtime pelo DataBootstrap (BCrypt).
-- Placeholder "__BOOTSTRAP__" e detectado e substituido pelo hash real.
-- =========================
INSERT INTO usuario (id, login, nome, senha, perfil, filial_id, ativo) VALUES
  (1, 'gerente',   'Gerente Geral',       '__BOOTSTRAP__', 'GERENTE',   NULL, TRUE),
  (2, 'atendente', 'Atendente Centro',    '__BOOTSTRAP__', 'ATENDENTE', 1,    TRUE);