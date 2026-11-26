-- Atualização única para um banco criado antes da versão com vários usuários.
-- Acrescenta o papel de cada conta. As contas que já existiam viram ADMIN, então a tela
-- de primeiro acesso volta a funcionar até a dona criar a conta dela.
-- Bancos novos não precisam disto: o banco.sql já cria a tabela completa.
--
-- Uso no servidor (dentro da pasta SistemaConfeitaria), uma única vez:
--   docker compose exec -T banco sh -c 'mysql -uroot -p"$MYSQL_ROOT_PASSWORD" dejane_cakes_db' < deploy/atualiza-usuarios.sql

ALTER TABLE usuarios
    ADD COLUMN papel VARCHAR(10) NOT NULL DEFAULT 'ADMIN',
    ADD CONSTRAINT chk_usuarios_papel CHECK (papel IN ('DONA', 'ADMIN', 'VISITANTE'));
