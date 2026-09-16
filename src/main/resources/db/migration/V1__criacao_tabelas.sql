CREATE DATABASE IF NOT EXISTS cineshare
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE cineshare;


-- =====================================================
-- TABELA: USUARIOS
-- =====================================================

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(255) NOT NULL,
    data_cadastro DATETIME(6) NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT uk_usuario_email UNIQUE (email)
);


-- =====================================================
-- TABELA: LISTAS
-- =====================================================

CREATE TABLE IF NOT EXISTS listas (
    id BIGINT NOT NULL AUTO_INCREMENT,
    titulo VARCHAR(150) NOT NULL,
    descricao TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'RASCUNHO',
    data_criacao DATETIME(6) NOT NULL,
    data_publicacao DATETIME(6),

    usuario_id BIGINT NOT NULL,

    PRIMARY KEY (id),

    CONSTRAINT fk_lista_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id),

    CONSTRAINT chk_lista_status
        CHECK (status IN ('RASCUNHO', 'PUBLICADA', 'ARQUIVADA'))
);


-- =====================================================
-- TABELA: FILMES
-- =====================================================

CREATE TABLE IF NOT EXISTS filmes (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome_original VARCHAR(255) NOT NULL,
    diretor VARCHAR(255),
    ano_lancamento INT,
    comentario_usuario TEXT NOT NULL,

    lista_id BIGINT NOT NULL,

    PRIMARY KEY (id),

    CONSTRAINT fk_filme_lista
        FOREIGN KEY (lista_id)
        REFERENCES listas(id),

    CONSTRAINT chk_comentario_minimo
        CHECK (CHAR_LENGTH(comentario_usuario) >= 10)
);


-- =====================================================
-- ÍNDICES
-- =====================================================

CREATE INDEX idx_lista_usuario
    ON listas(usuario_id);

CREATE INDEX idx_lista_status
    ON listas(status);

CREATE INDEX idx_filme_lista
    ON filmes(lista_id);