# AGENTS.md: contexto do projeto para assistentes de IA

Leia este arquivo inteiro antes de mexer no repositório. Ele descreve o que o projeto é, como está organizado, o que **não pode quebrar** e como trabalhar aqui. As instruções do dono do repositório (Gabriel) vêm sempre antes das sugestões deste arquivo.

## 1. Identificação

| Item | Valor |
| --- | --- |
| Projeto | Sistema de Estoque Dejane Cakes (nome técnico: `SistemaConfeitaria`) |
| Repositório | `github.com/Gabriel-SZ-Silva/estoque-dejane-cakes`, branch `main` |
| Cliente | Dejane Cakes, confeitaria MEI em Mogi das Cruzes (SP) |
| Equipe | SaaSquad (projeto acadêmico, UMC) |
| Situação | MVP **em produção** desde 23/11/2026, com dados reais da cliente |
| Objetivo | Controle de estoque de insumos, receitas e produção (baixa automática), com alertas de validade |
| Idioma | Português do Brasil em tudo: código, comentários, telas, commits e documentação |

O projeto segue o mesmo estilo e os mesmos padrões do projeto de referência da disciplina, o `SistemaPizzaria` (Front Controller, Command, Factory, DAO, Decorator, SOLID), **sem frameworks**.

## 2. Tecnologia

| Camada | Tecnologia |
| --- | --- |
| Linguagem | Java 25 |
| Web | Jakarta Servlet 6.1 e JSP 4.0, no Apache Tomcat 11 |
| Banco | MySQL 8.4 (utf8mb4, `utf8mb4_0900_ai_ci`), acessado por JDBC puro |
| Driver | MySQL Connector/J, já incluído em `web/WEB-INF/lib/mysql-connector-j.jar` |
| Build local | Projeto Ant do NetBeans (`build.xml` + `nbproject/`) |
| Build de produção | `Dockerfile` (compila com `javac`, sem Ant nem Maven) |
| Produção | Docker Compose com 3 containers: `banco` (MySQL), `app` (Tomcat) e `caddy` (HTTPS automático) |
| Front-end | JSP + CSS próprio + PWA (`manifest.json`, `sw.js`); sem JavaScript de framework |

Não introduza Spring, Maven, Gradle, Hibernate, Lombok ou frameworks de front-end, a menos que o dono peça explicitamente.

## 3. Estrutura atual

```
.
├── AGENTS.md                  este arquivo
├── README.md                  apresentação do projeto (visão geral e início rápido)
├── CHANGELOG.md               histórico de versões
├── LICENSE                    todos os direitos reservados (não trocar sem o dono)
├── .gitattributes             força LF nos scripts de servidor
├── .github/workflows/build.yml compila com `docker build .` a cada push
├── docs/                      regras de negócio, arquitetura, instalação, publicação e operação
├── banco.sql                  cria o banco completo (usado pelo Docker na 1ª subida)
├── Dockerfile                 compila e empacota o app no Tomcat
├── docker-compose.yml         banco + app + caddy (produção)
├── docker-compose.override.yml ajustes para servidor de 1 GB (lido automaticamente)
├── .env.exemplo               modelo das variáveis secretas (o .env real nunca vai ao Git)
├── build.xml, nbproject/      projeto NetBeans (Ant)
├── deploy/
│   ├── Caddyfile              proxy HTTPS
│   ├── backup.sh              backup diário do banco (cron no servidor)
│   ├── restaurar.sh           restaura um backup
│   ├── usuarios.sh            cria/lista/troca senha/remove contas ADMIN e VISITANTE
│   └── atualiza-usuarios.sql  migração única já aplicada em produção (coluna papel)
├── src/java/
│   ├── controller/ControleConfeitaria.java   Front Controller (único servlet)
│   ├── factory/CommandFactory.java           cria a Action pelo nome, por reflexão
│   ├── factory/MaterialFactory.java          monta o tipo do insumo (Decorator)
│   ├── command/*Action.java                  uma classe por operação (ICommand)
│   ├── dao/*DAO.java                         SQL (um DAO por tabela principal)
│   ├── model/                                classes de dados
│   ├── decorator/                            Material, MaterialBasico, TipoIngrediente, TipoEmbalagem
│   ├── filter/AutenticacaoFilter.java        login, permissões do visitante, cabeçalhos
│   ├── util/                                 validação, senhas, unidades, avisos, cópias p/ Desfazer
│   └── ferramentas/Usuarios.java             gestão de contas pelo terminal (usada por deploy/usuarios.sh)
└── web/
    ├── *.jsp, index.html, offline.html       telas
    ├── css/estilo.css, img/                  visual (cores da logomarca)
    ├── manifest.json, sw.js                  app instalável (PWA)
    ├── META-INF/context.xml
    └── WEB-INF/web.xml, WEB-INF/lib/         configuração e driver
```

## 4. Como uma requisição funciona

1. Toda requisição passa pelo `AutenticacaoFilter`: sem login, só a tela de login, o primeiro acesso, a página de erro e os arquivos estáticos ficam abertos. Contas `VISITANTE` são barradas em qualquer ação que altera dados.
2. O `ControleConfeitaria` lê os parâmetros `acao` e `entidade` (só letras) e pede ao `CommandFactory` a classe `command.<acao><entidade>Action`. Exemplo: `acao=Cadastra&entidade=Insumo` executa `command.CadastraInsumoAction`.
3. As ações que gravam (`Cadastra`, `Atualiza`, `Deleta`, `Produz`, `Restaura`, `Desfaz`, `Entra`) só aceitam POST.
4. A Action devolve o nome de uma JSP (forward) ou `redirect:<destino>` (padrão gravar-e-redirecionar, com mensagem de aviso na sessão via `util/Aviso`).
5. Os DAOs usam `PreparedStatement` e transações onde há várias gravações (receitas e produção).

## 5. O que NÃO pode quebrar (leia antes de reorganizar)

Estas ligações são feitas por **nome ou caminho**. Mover ou renomear sem ajustar o outro lado quebra o sistema, muitas vezes sem erro de compilação.

| Se você mexer em… | Também precisa ajustar… |
| --- | --- |
| Nome ou pacote de qualquer `*Action` | Nada aponta para ela por import: o `CommandFactory` monta `"command." + acao + entidade + "Action"`. As telas chamam pela URL. Manter pacote `command` e nomes exatos |
| Pastas `src/java` ou `web` | `Dockerfile` (`COPY src/java`, `COPY web`), `nbproject/project.properties` (`src.dir`, `web.docbase.dir`, `webinf.dir`, `file.reference…`) e `.dockerignore` |
| Nome de uma JSP | Os `return "x.jsp"` e `redirect:` nas Actions, os links das outras telas, `index.html`, `AutenticacaoFilter` (caminhos livres e formulários bloqueados), `web.xml` (páginas de erro) e `sw.js` |
| `banco.sql` (local ou nome) | `docker-compose.yml` (montado em `/docker-entrypoint-initdb.d`) |
| Qualquer arquivo de `deploy/` | `docker-compose.yml` (Caddyfile), o **cron do servidor** (chama `/home/azureuser/SistemaConfeitaria/deploy/backup.sh`) e `docs/publicacao.md` / `docs/operacao.md` |
| Estrutura de tabelas | Produção já tem dados: nunca só editar o `banco.sql`. Criar também um script de migração em `deploy/` (como `atualiza-usuarios.sql`) e avisar que ele precisa ser rodado uma vez no servidor |
| Driver em `web/WEB-INF/lib` | `nbproject/project.properties` (`file.reference.mysql-connector-j-9.2.0.jar`) e o `Dockerfile` |
| `docker-compose.override.yml` | É o que faz o sistema caber no servidor de 1 GB. Não apagar |

Outros cuidados:

- `nbproject/project.properties` tem um caminho absoluto do Windows para o `servlet-api.jar` do Tomcat (`file.reference.servlet-api.jar`). É da máquina do Gabriel; não "corrija" sem combinar.
- `nbproject/private/`, `build/`, `dist/`, `backups/`, `.env` e `*.zip` são ignorados pelo Git e não devem ser commitados.
- Nunca grave senhas, IPs do servidor, tokens ou dados da cliente no repositório. As variáveis secretas estão documentadas em `.env.exemplo`.
- Nunca sugira `docker compose down -v`: o `-v` apaga o volume com o banco de produção.

## 6. Regras de negócio essenciais

- **Insumos:** identificados por nome + marca + lote, sem duplicados entre os ativos (ignora maiúsculas e acentos). Quantidade `DECIMAL(10,3)` ≥ 0, aceita vírgula ou ponto. Unidades: un, g, kg, ml, L, pct, cx. Data da última compra nunca no futuro.
- **Receitas:** nome único entre as ativas; pelo menos um ingrediente com quantidade > 0.
- **Produção:** inteiro de 1 a 1.000; desconta todos os ingredientes numa transação ou nenhum. Sem estorno.
- **Validade:** vencido = antes de hoje; vencendo = até 7 dias. Fuso America/Sao_Paulo.
- **Lixeira:** exclusão lógica (`excluido_em`), 30 dias para restaurar. Coluna gerada `ativo` nas regras únicas.
- **Desfazer:** tabela `versoes` com antes/depois; só a última alteração de cada registro, e só se nada mudou depois.
- **Usuários:** papéis `DONA` (criada pela tela de primeiro acesso com `CODIGO_INSTALACAO`), `ADMIN` e `VISITANTE` (só leitura). ADMIN e VISITANTE só pelo terminal (`deploy/usuarios.sh`). Senhas em PBKDF2-SHA256 com 600 mil iterações.
- **Histórico:** tabela `movimentacoes`, a tela mostra as 200 mais recentes.

O detalhamento está em `docs/regras-de-negocio.md` e `docs/arquitetura.md`. Mudou uma regra no código? Atualize esses documentos e o `CHANGELOG.md` no mesmo commit.

## 7. Como compilar e testar

- **Docker (qualquer sistema):** copie `.env.exemplo` para `.env`, preencha, e rode `docker compose up -d --build`. O app fica atrás do Caddy nas portas 80/443.
- **Só compilar, para conferir:** `docker build .` (falha se algum `.java` não compilar).
- **NetBeans:** abrir a pasta como projeto, Tomcat 11 cadastrado em Servers, MySQL local com o `banco.sql` executado. Sem variáveis de ambiente, `util/FabricaConexao` usa `jdbc:mysql://localhost:3306/dejane_cakes_db` com root/admin (padrão só de desenvolvimento).
- **Testes automatizados:** ainda não estão no repositório (existiu um roteiro de verificação fora dele). Uma pasta `test/` com JUnit é uma melhoria bem-vinda, desde que não exija Maven.

Depois de qualquer reorganização, confirme no mínimo: `docker build .` passa (o workflow do GitHub também roda isso); não sobrou referência ao caminho antigo (`git grep "<caminho antigo>"`); o README e os documentos de `docs/` continuam corretos.

## 8. Produção (para não quebrar quem está usando)

- Servidor: VM Ubuntu 24.04 na Azure for Students, pasta `~/SistemaConfeitaria` (o nome da pasta no servidor **não** é o nome do repositório).
- Atualização hoje: o dono envia um zip, roda `unzip -o` e `docker compose up -d --build`. Se você mudar essa forma (por exemplo, para `git pull` no servidor), escreva o passo a passo e avise que o cron de backup aponta para o caminho atual.
- Mudança de banco = script de migração + aviso explícito.

## 9. Convenções

- Nomes no estilo da pizzaria: `CadastraInsumoAction`, `InsumoDAO`, `consultaInsumos.jsp`, parâmetros de formulário com prefixo (`txtNome`, `selUnidade`).
- Comentários curtos em português, explicando o porquê.
- Mensagens para o usuário em português simples, dizendo o que corrigir.
- Commits em português, no imperativo e com assunto curto (ex.: `Corrige validação de lote`). Um assunto por commit.
- Não acrescente linhas de coautoria, assinatura ou link de sessão de assistentes de IA nos commits, no código ou na documentação. A autoria é da Equipe SaaSquad.
- Não reformatar arquivos inteiros sem pedido: diffs pequenos facilitam a revisão do dono.

## 10. Ideias de organização (só faça o que o dono pedir)

Já feito: `docs/`, `LICENSE` (todos os direitos reservados), `CHANGELOG.md`, `.gitattributes` e o workflow de build. Ainda em aberto:

- Capturas de tela das principais telas em `docs/imagens/`, referenciadas no README.
- Modelos de issue e de pull request em `.github/`.
- Testes JUnit em `test/`.
- Mover `deploy/atualiza-usuarios.sql` para `deploy/migracoes/` (lembrando de atualizar README e instruções).

Antes de qualquer mudança estrutural, apresente o plano (o que move, para onde, e quais arquivos da seção 5 serão ajustados) e espere a confirmação do dono.
