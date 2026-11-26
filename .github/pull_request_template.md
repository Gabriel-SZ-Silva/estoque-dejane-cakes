## O que mudou

<!-- Resumo da mudança e o motivo. Se resolve uma issue, escreva "Resolve #número". -->

## Como testar

<!-- Passos para conferir a mudança localmente: telas, dados de teste, perfil usado (DONA, ADMIN ou VISITANTE). -->

1.
2.

## Migração de banco

- [ ] Não altera a estrutura do banco
- [ ] Altera a estrutura do banco: script de migração em `deploy/` (nome: `deploy/...`), `banco.sql` atualizado para instalações novas e tabela de migrações de `docs/publicacao.md` atualizada

## Impacto na produção

- [ ] Nenhum além de publicar a nova versão (`docker compose up -d --build`)
- [ ] Exige passos no servidor (descreva abaixo, incluindo backup antes e como voltar atrás)

<!-- Passos no servidor, se houver. Nunca use docker compose down -v. -->

## Checklist

- [ ] `docker build .` termina sem erro
- [ ] Testei no navegador as telas afetadas
- [ ] Atualizei `docs/` e a seção `[Não lançado]` do `CHANGELOG.md`, quando necessário
- [ ] Commits em português, no imperativo, um assunto por commit
- [ ] Sem senhas, `.env`, endereço ou IP do servidor e sem dados reais da cliente
