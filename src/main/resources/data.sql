-- =========================
-- Filiais
-- =========================
INSERT INTO filial (id, nome, endereco, cidade, telefone, ativo) VALUES (1, 'Livraria Regional - Centro', 'Rua das Flores, 123', 'Maringa', '(44) 3000-0001', TRUE);
INSERT INTO filial (id, nome, endereco, cidade, telefone, ativo) VALUES (2, 'Livraria Regional - Zona Norte', 'Av. Brasil, 4500', 'Maringa', '(44) 3000-0002', TRUE);
INSERT INTO filial (id, nome, endereco, cidade, telefone, ativo) VALUES (3, 'Livraria Regional - Londrina', 'Av. Higienopolis, 88', 'Londrina', '(43) 3000-0003', TRUE);

-- =========================
-- Produtos (10 livros)
-- Senhas em BCrypt abaixo estao apenas para ambiente academico/demonstracao.
-- =========================
INSERT INTO produto (id, titulo, autor, isbn, categoria, preco, estoque_minimo, ativo) VALUES (1,  'O Continente',                  'Erico Verissimo',           '978-85-01-00001-1', 'Romance Nacional',   59.90, 3, TRUE);
INSERT INTO produto (id, titulo, autor, isbn, categoria, preco, estoque_minimo, ativo) VALUES (2,  'Grande Sertao: Veredas',       'Guimaraes Rosa',            '978-85-01-00002-8', 'Romance Nacional',   79.90, 3, TRUE);
INSERT INTO produto (id, titulo, autor, isbn, categoria, preco, estoque_minimo, ativo) VALUES (3,  'Capitaes da Areia',            'Jorge Amado',               '978-85-01-00003-5', 'Romance Nacional',   49.90, 3, TRUE);
INSERT INTO produto (id, titulo, autor, isbn, categoria, preco, estoque_minimo, ativo) VALUES (4,  'Memorial do Convento',         'Jose Saramago',             '978-85-01-00004-2', 'Romance Estrangeiro',69.90, 3, TRUE);
INSERT INTO produto (id, titulo, autor, isbn, categoria, preco, estoque_minimo, ativo) VALUES (5,  'Cem Anos de Solidao',          'Gabriel Garcia Marquez',    '978-85-01-00005-9', 'Romance Estrangeiro',89.90, 3, TRUE);
INSERT INTO produto (id, titulo, autor, isbn, categoria, preco, estoque_minimo, ativo) VALUES (6,  '1984',                         'George Orwell',             '978-85-01-00006-6', 'Distopia',           39.90, 5, TRUE);
INSERT INTO produto (id, titulo, autor, isbn, categoria, preco, estoque_minimo, ativo) VALUES (7,  'A Revolucao dos Bichos',       'George Orwell',             '978-85-01-00007-3', 'Distopia',           34.90, 5, TRUE);
INSERT INTO produto (id, titulo, autor, isbn, categoria, preco, estoque_minimo, ativo) VALUES (8,  'O Pequeno Principe',           'Antoine de Saint-Exupery',  '978-85-01-00008-0', 'Infantil',           29.90, 5, TRUE);
INSERT INTO produto (id, titulo, autor, isbn, categoria, preco, estoque_minimo, ativo) VALUES (9,  'Clean Code',                   'Robert C. Martin',          '978-85-01-00009-7', 'Tecnologia',         119.90, 2, TRUE);
INSERT INTO produto (id, titulo, autor, isbn, categoria, preco, estoque_minimo, ativo) VALUES (10, 'Domain-Driven Design',         'Eric Evans',                '978-85-01-00010-3', 'Tecnologia',         139.90, 2, TRUE);

-- =========================
-- Estoque por filial x produto
-- Filial 1 (Centro): quantidades cheias
-- Filial 2 (Zona Norte): alguns abaixo do minimo propositalmente
-- Filial 3 (Londrina): distribuicao variada
-- =========================
INSERT INTO estoque (filial_id, produto_id, quantidade) VALUES (1, 1, 15);
INSERT INTO estoque (filial_id, produto_id, quantidade) VALUES (1, 2, 12);
INSERT INTO estoque (filial_id, produto_id, quantidade) VALUES (1, 3, 18);
INSERT INTO estoque (filial_id, produto_id, quantidade) VALUES (1, 4, 10);
INSERT INTO estoque (filial_id, produto_id, quantidade) VALUES (1, 5, 8);
INSERT INTO estoque (filial_id, produto_id, quantidade) VALUES (1, 6, 20);
INSERT INTO estoque (filial_id, produto_id, quantidade) VALUES (1, 7, 22);
INSERT INTO estoque (filial_id, produto_id, quantidade) VALUES (1, 8, 25);
INSERT INTO estoque (filial_id, produto_id, quantidade) VALUES (1, 9, 7);
INSERT INTO estoque (filial_id, produto_id, quantidade) VALUES (1, 10, 6);

INSERT INTO estoque (filial_id, produto_id, quantidade) VALUES (2, 1, 2);
INSERT INTO estoque (filial_id, produto_id, quantidade) VALUES (2, 2, 4);
INSERT INTO estoque (filial_id, produto_id, quantidade) VALUES (2, 3, 1);
INSERT INTO estoque (filial_id, produto_id, quantidade) VALUES (2, 4, 6);
INSERT INTO estoque (filial_id, produto_id, quantidade) VALUES (2, 5, 3);
INSERT INTO estoque (filial_id, produto_id, quantidade) VALUES (2, 6, 8);
INSERT INTO estoque (filial_id, produto_id, quantidade) VALUES (2, 7, 9);
INSERT INTO estoque (filial_id, produto_id, quantidade) VALUES (2, 8, 10);
INSERT INTO estoque (filial_id, produto_id, quantidade) VALUES (2, 9, 2);
INSERT INTO estoque (filial_id, produto_id, quantidade) VALUES (2, 10, 1);

INSERT INTO estoque (filial_id, produto_id, quantidade) VALUES (3, 1, 9);
INSERT INTO estoque (filial_id, produto_id, quantidade) VALUES (3, 2, 7);
INSERT INTO estoque (filial_id, produto_id, quantidade) VALUES (3, 3, 6);
INSERT INTO estoque (filial_id, produto_id, quantidade) VALUES (3, 4, 4);
INSERT INTO estoque (filial_id, produto_id, quantidade) VALUES (3, 5, 5);
INSERT INTO estoque (filial_id, produto_id, quantidade) VALUES (3, 6, 11);
INSERT INTO estoque (filial_id, produto_id, quantidade) VALUES (3, 7, 13);
INSERT INTO estoque (filial_id, produto_id, quantidade) VALUES (3, 8, 14);
INSERT INTO estoque (filial_id, produto_id, quantidade) VALUES (3, 9, 3);
INSERT INTO estoque (filial_id, produto_id, quantidade) VALUES (3, 10, 4);

-- =========================
-- Usuarios de teste
-- gerente/admin123 (BCrypt de "admin123")  -> perfil GERENTE, sem filial fixa (gerente ve tudo)
-- atendente/atendente123 (BCrypt de "atendente123") -> perfil ATENDENTE, filial 1 (Centro)
-- =========================
-- Senhas serao definidas em runtime pelo DataBootstrap (BCrypt)
INSERT INTO usuario (id, login, nome, senha, perfil, filial_id, ativo) VALUES (1, 'gerente', 'Gerente Geral', '__BOOTSTRAP__', 'GERENTE', NULL, TRUE);
INSERT INTO usuario (id, login, nome, senha, perfil, filial_id, ativo) VALUES (2, 'atendente', 'Atendente Centro', '__BOOTSTRAP__', 'ATENDENTE', 1, TRUE);