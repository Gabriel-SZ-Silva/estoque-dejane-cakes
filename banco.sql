-- Dejane Cakes - criação do banco de dados (MySQL 8).
-- Execute este script uma vez antes de rodar o sistema.

CREATE DATABASE IF NOT EXISTS dejane_cakes_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;   -- acentos e maiúsculas não diferenciam: "açúcar" = "ACUCAR"
USE dejane_cakes_db;

-- Lixeira: excluir só preenche excluido_em. O registro some das telas, aparece na Lixeira
-- e pode ser restaurado; depois de 30 dias o sistema o apaga de vez.
-- A coluna "ativo" é calculada pelo banco: 1 quando ativo, NULL quando na lixeira.
-- As regras de "não repetir" usam essa coluna, e o MySQL aceita vários NULL num índice único:
-- assim, o que está na lixeira não impede cadastrar de novo, e entre os ativos a regra continua valendo.

CREATE TABLE IF NOT EXISTS insumos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    tipo VARCHAR(100) NOT NULL,
    marca VARCHAR(100) NOT NULL,
    lote VARCHAR(50) NOT NULL,
    quantidade DECIMAL(10,3) NOT NULL,           -- até 3 casas decimais: 1.500 kg
    unidade VARCHAR(10) NOT NULL DEFAULT 'un',   -- un, g, kg, ml, L, pct, cx (ver util/Unidade.java)
    validade DATE NOT NULL,
    data_ultima_compra DATE NOT NULL,
    excluido_em DATETIME NULL,
    ativo TINYINT GENERATED ALWAYS AS (IF(excluido_em IS NULL, 1, NULL)) STORED,
    -- O mesmo produto, da mesma marca e do mesmo lote, só pode existir uma vez entre os ativos.
    CONSTRAINT uk_insumo UNIQUE (nome, marca, lote, ativo),
    CONSTRAINT ck_insumo_quantidade CHECK (quantidade >= 0),
    -- O dashboard procura por validade todo dia.
    INDEX idx_insumo_validade (validade)
);

CREATE TABLE IF NOT EXISTS receitas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    excluido_em DATETIME NULL,
    ativo TINYINT GENERATED ALWAYS AS (IF(excluido_em IS NULL, 1, NULL)) STORED,
    CONSTRAINT uk_receita_nome UNIQUE (nome, ativo)
);

-- Ingredientes de cada receita: qual insumo e quanto dele a receita usa.
CREATE TABLE IF NOT EXISTS itens_receita (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_receita INT NOT NULL,
    id_insumo INT NOT NULL,
    quantidade DECIMAL(10,3) NOT NULL,           -- na mesma unidade do insumo
    CONSTRAINT ck_item_quantidade CHECK (quantidade > 0),
    -- Apagar a receita apaga os ingredientes dela; apagar um insumo usado em receita é barrado.
    FOREIGN KEY (id_receita) REFERENCES receitas(id) ON DELETE CASCADE,
    FOREIGN KEY (id_insumo) REFERENCES insumos(id)
);

-- Desfazer: antes de cada alteração, o sistema guarda como o registro estava (dados_antes)
-- e como ficou (dados_depois), no formato chave=valor (ver util/Foto.java).
-- Só é possível desfazer a última alteração de cada registro, e só se ele não mudou depois dela.
CREATE TABLE IF NOT EXISTS versoes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    entidade VARCHAR(20) NOT NULL,      -- Insumo ou Receita
    id_registro INT NOT NULL,
    dados_antes TEXT NOT NULL,
    dados_depois TEXT NOT NULL,
    desfeita BOOLEAN NOT NULL DEFAULT FALSE,
    data_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_versao_registro (entidade, id_registro)
);

CREATE TABLE IF NOT EXISTS movimentacoes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tipo VARCHAR(20) NOT NULL,
    descricao VARCHAR(1000) NOT NULL,   -- uma produção de receita lista todos os ingredientes descontados
    data_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_versao INT NULL,                 -- preenchido nas alterações: liga a linha do histórico ao botão Desfazer
    FOREIGN KEY (id_versao) REFERENCES versoes(id)
);

-- Acesso ao sistema. A senha nunca é guardada, só o hash (ver util/Senha.java).
-- Papel: DONA (criada pela tela de primeiro acesso), ADMIN (acesso total) e VISITANTE (só visualiza).
-- ADMIN e VISITANTE são criados no servidor com deploy/usuarios.sh.
CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    login VARCHAR(50) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    papel VARCHAR(10) NOT NULL DEFAULT 'ADMIN',
    CONSTRAINT chk_usuarios_papel CHECK (papel IN ('DONA', 'ADMIN', 'VISITANTE'))
);
