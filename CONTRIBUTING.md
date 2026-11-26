# Como contribuir

Obrigado por contribuir com o Estoque Dejane Cakes. O sistema está **em produção, com dados reais da cliente**: por isso, toda mudança passa por pull request e pela revisão do mantenedor antes de chegar à `main`.

## Fluxo de trabalho

1. **Acesso.** O mantenedor adiciona os colaboradores em *Settings → Collaborators*, com permissão **Write**. Sem esse acesso, faça um fork do repositório e abra o pull request a partir dele.
2. **Branch.** Crie uma branch própria, sempre a partir da `main` atualizada:

   ```bash
   git switch main
   git pull
   git switch -c fix/validacao-lote
   ```

   | Prefixo | Uso | Exemplo |
   | --- | --- | --- |
   | `feature/` | Funcionalidade nova | `feature/limite-tentativas-login` |
   | `fix/` | Correção de erro | `fix/validacao-lote` |
   | `docs/` | Só documentação | `docs/guia-operacao` |

3. **Commits.** Pequenos e com um assunto cada (veja [Convenções](#convenções)).
4. **Pull request.** Envie a branch (`git push -u origin <branch>`) e abra um pull request para a `main`. Preencha o [modelo](.github/pull_request_template.md) que aparece na descrição: o que mudou, como testar, se há migração de banco e se afeta a produção.
5. **Revisão.** O pull request só é integrado com o workflow **Build** passando e a aprovação do mantenedor. Ninguém envia commits direto para a `main`.
6. **Integração e publicação.** Quem faz o merge é o mantenedor. Só o que está na `main` vai para o servidor, e quem atualiza o servidor é o mantenedor.

Para trazer as novidades da `main` para a sua branch, use `git fetch origin` e `git merge origin/main`.

### Histórico

- Nunca reescreva o histórico da `main` (`commit --amend`, `rebase` ou `push --force`).
- Depois que o pull request estiver aberto, prefira novos commits a reescrever a branch: outras pessoas podem já tê-la baixado.
- Não altere a autoria de commits de outras pessoas.

## Convenções

### Commits

- Em português, no imperativo, com assunto de até cerca de 60 caracteres: `Corrige validação de lote`, `Adiciona guia de publicação`.
- Um assunto por commit. Quando o motivo não for óbvio, explique o porquê no corpo da mensagem.

### Código

- Siga os padrões já usados no projeto: Front Controller, Command, Factory, DAO, Decorator e Filter. A arquitetura está em [docs/arquitetura.md](docs/arquitetura.md).
- Não introduza Spring, Spring Boot, Hibernate/JPA, Maven, Gradle, Lombok nem frameworks JavaScript.
- Nomes no estilo existente: `CadastraInsumoAction`, `InsumoDAO`, `consultaInsumos.jsp`, campos de formulário com prefixo (`txtNome`, `selUnidade`).
- As classes `*Action` ficam no pacote `command`, com o nome exato `<acao><entidade>Action`: a `CommandFactory` as encontra pelo nome, em tempo de execução. Renomear uma delas quebra a tela sem erro de compilação.
- Comentários curtos em português, explicando o porquê. Mensagens para o usuário em português simples, dizendo o que corrigir.
- Não reformate arquivos inteiros: diffs pequenos facilitam a revisão.

### Documentação

Mudou uma regra no código? No mesmo pull request, atualize o documento correspondente em `docs/` (por exemplo, [docs/regras-de-negocio.md](docs/regras-de-negocio.md)) e registre a mudança na seção `[Não lançado]` do [CHANGELOG.md](CHANGELOG.md).

## Como rodar localmente

Com Docker:

```bash
git clone https://github.com/Gabriel-SZ-Silva/estoque-dejane-cakes.git
cd estoque-dejane-cakes
cp .env.exemplo .env
```

Edite o `.env` e preencha todas as variáveis: `DOMINIO=localhost`, `DB_SENHA`, `DB_SENHA_ROOT` e `CODIGO_INSTALACAO` (para uso local, valores quaisquer). Sem o `.env` preenchido, o Docker Compose para com erro. Depois:

```bash
docker compose up -d --build
```

Acesse `https://localhost` (o navegador avisa sobre o certificado local; pode continuar) e crie a primeira conta em **Primeiro acesso**, com o `CODIGO_INSTALACAO` do `.env`. Para parar, use `docker compose down`: os dados ficam guardados no volume e voltam na próxima subida.

Com `make` instalado (Linux e macOS), os atalhos `make up`, `make down`, `make logs` e `make ps` fazem o mesmo; `make` sem alvo lista todos.

Para rodar pelo NetBeans, siga [docs/instalacao-local.md](docs/instalacao-local.md).

## Antes de abrir o pull request

O projeto ainda não tem testes automatizados. Confira pelo menos:

- `docker build .` (ou `make build`) termina sem erro: é o mesmo que o workflow **Build** roda;
- o sistema sobe localmente e as telas afetadas funcionam no navegador;
- se a mudança mexe em permissões, confira também com uma conta `VISITANTE` (crie com `bash deploy/usuarios.sh criar <login> visitante`, com os containers rodando);
- `docs/` e `CHANGELOG.md` estão atualizados.

## Relatar erros e sugerir melhorias

Abra uma issue com o modelo [Relato de erro](.github/ISSUE_TEMPLATE/bug_report.md) ou [Sugestão de melhoria](.github/ISSUE_TEMPLATE/feature_request.md). Para trabalhar numa issue, mencione o número dela no pull request.

## Banco de dados e produção

- **Mudança na estrutura das tabelas:** editar só o `banco.sql` não basta, porque ele roda apenas num banco vazio. Crie também um script de migração em `deploy/` (como `deploy/atualiza-usuarios.sql`), acrescente-o à tabela de migrações de [docs/publicacao.md](docs/publicacao.md), registre-o na seção "Migração" do CHANGELOG e avise no pull request.
- **Arquivos ligados por nome ou caminho:** antes de mover ou renomear pastas, JSPs, scripts de `deploy/` ou o `banco.sql`, confira a tabela de acoplamentos na seção 5 do [AGENTS.md](AGENTS.md).
- **Nunca** use nem sugira `docker compose down -v`: o `-v` apaga o volume com o banco.
- **Segredos e dados da cliente:** o `.env` nunca vai para o Git. Não coloque senhas, tokens, endereço ou IP do servidor nem dados reais da cliente no código, em issues, em pull requests ou em capturas de tela.
