# Histórico de versões

Formato baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/). Datas no formato AAAA-MM-DD.

## [Não lançado]

### Adicionado
- `CONTRIBUTING.md` com o fluxo de branches e pull requests, as convenções de commit e de código, como rodar localmente e os cuidados com o banco em produção.
- Seção "Fluxo de branches e pull requests" no `AGENTS.md`.
- Modelo de pull request (`.github/pull_request_template.md`) e modelos de issue para relato de erro e sugestão de melhoria (`.github/ISSUE_TEMPLATE/`).
- `docs/README.md` como índice da documentação.
- `Makefile` com os atalhos `build`, `up`, `down` (sem `-v`, mantém o banco), `logs`, `ps` e `backup`.
- `NOTICE` com os itens que não estão cobertos pela licença MIT: o MySQL Connector/J (licença própria da Oracle) e a marca e a logomarca "Dejane Cakes".

### Alterado
- README com sumário, seções Topologia de infraestrutura e Testes, selo dinâmico do workflow Build e estrutura do projeto atualizada.
- Licença trocada de "todos os direitos reservados" para MIT, em nome de Gabriel Souza Silva. README e `AGENTS.md` atualizados.
- Autoria: Gabriel Souza Silva passa a constar como autor e mantenedor. O README ganhou as seções Autor e Contribuidores, e o `AGENTS.md` descreve a autoria de mantenedor e contribuidores.

## [1.1.0] - 2026-11-23

### Adicionado
- Perfis de acesso: `DONA` (criada pela tela de primeiro acesso), `ADMIN` e `VISITANTE` (somente leitura).
- `deploy/usuarios.sh` para criar, listar, trocar a senha e remover contas pelo terminal do servidor.
- `docker-compose.override.yml` para rodar em servidores com 1 GB de memória.
- Backup diário agendado (`deploy/backup.sh`) e restauração (`deploy/restaurar.sh`).

### Alterado
- A tela de primeiro acesso fica aberta até a conta da dona existir, mesmo com outras contas cadastradas.

### Migração
- `deploy/atualiza-usuarios.sql`: acrescenta a coluna `papel` à tabela `usuarios`.

## [1.0.0] - 2026-11-23

Primeira versão em produção (MVP).

### Adicionado
- Cadastro, busca, edição e exclusão de insumos, com marca, lote, tipo, quantidade decimal, unidade, validade e data da última compra.
- Receitas com ingredientes, situação de estoque e produção com baixa automática em transação.
- Dashboard com insumos vencidos, vencendo em até 7 dias e histórico de movimentações.
- Lixeira de 30 dias e Desfazer da última alteração.
- Login com hash PBKDF2, sessão de 12 horas e proteção das telas por filtro.
- App instalável (PWA) e telas adaptadas ao celular.
- Publicação com Docker Compose (MySQL, Tomcat e Caddy com HTTPS automático).
