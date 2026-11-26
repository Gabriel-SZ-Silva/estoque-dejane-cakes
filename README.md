# SistemaConfeitaria — Dejane Cakes

Sistema de controle de estoque de insumos da Dejane Cakes, feito no mesmo molde do `SistemaPizzaria`: Servlet + JSP + JDBC, projeto web do NetBeans, Tomcat 11 e MySQL.

## O que o sistema faz (conforme o relatório)

- **CRUD de insumos**: cadastrar, consultar, alterar e deletar.
- **Informações de cada insumo**: quantidade em estoque, validade, lote, marca, tipo e data da última compra.
- **Dashboard**: histórico de movimentações na aplicação (cada cadastro, alteração e exclusão fica registrado) e, no topo, os produtos já vencidos e os que vencem em até 7 dias. O dia da validade ainda conta como válido ("vence hoje"); o prazo de 7 dias fica na constante `DIAS_PARA_VENCER` da `ConsultaTodosMovimentacaoAction`.
- **Visual**: letras e ícones grandes, com as cores tiradas da logomarca (`web/img/logo.jpg`). As cores e a fonte ficam nas variáveis do início de `web/css/estilo.css`.

## Acesso com usuário e senha

- Todas as telas exigem login (`filter/AutenticacaoFilter`). A senha é guardada só como hash PBKDF2 (`util/Senha`).
- **Três papéis** (coluna `papel` da tabela `usuarios`):
  - `DONA`: a conta da Dejane, criada por ela na tela de primeiro acesso. Acesso total.
  - `ADMIN`: acesso total. Criada no servidor.
  - `VISITANTE`: vê todas as telas, mas não cadastra, altera, exclui, produz, restaura nem desfaz (ex.: banca avaliadora). Criada no servidor. O bloqueio fica no `AutenticacaoFilter`, antes de chegar ao controlador.
- **Primeiro acesso:** na tela de login, clique em "Primeiro acesso? Criar usuário". Fica aberto enquanto não existe a conta `DONA`, mesmo que já existam administradores e visitantes.
  - Rodando pelo NetBeans: deixe o código de instalação em branco; só é aceito no próprio computador.
  - No servidor: informe o `CODIGO_INSTALACAO` do arquivo `.env`.
- **Administradores e visitantes** são gerenciados só pelo terminal do servidor, de propósito (não há tela no site para isso). Dentro da pasta `SistemaConfeitaria`:
  - `bash deploy/usuarios.sh listar`
  - `bash deploy/usuarios.sh criar <login> admin` ou `... criar <login> visitante` (pede a senha duas vezes, sem mostrar na tela)
  - `bash deploy/usuarios.sh senha <login>`: troca a senha de qualquer conta, inclusive a da Dejane (senha esquecida)
  - `bash deploy/usuarios.sh remover <login>`: depois, `docker compose restart app` desconecta quem estava logado com essa conta. Remover a conta `DONA` reabre o primeiro acesso.
- Banco publicado antes desta versão: rode uma vez `deploy/atualiza-usuarios.sql` (instruções no próprio arquivo). As contas existentes viram `ADMIN`.
- Ações que gravam ou excluem só são aceitas por POST, e o cookie de sessão usa SameSite=Lax (`web/META-INF/context.xml`).

## Publicação na internet e app no celular

- `Dockerfile`, `docker-compose.yml`, `.env.exemplo` e a pasta `deploy/` sobem o sistema num servidor com Tomcat 11, MySQL 8.4 e HTTPS automático (Caddy). O passo a passo está no guia "Guia de publicação gratuita: Estoque Dejane Cakes".
- `web/manifest.json`, `web/sw.js` e os ícones em `web/img/` permitem instalar o sistema no celular como app (PWA).
- A conexão com o banco lê `DB_URL`, `DB_USUARIO` e `DB_SENHA` do ambiente; sem elas, usa os dados locais de antes.

## Regras dos insumos (pedido depois dos primeiros testes)

- **Busca por nome ou ID**: na lista de insumos, um número busca pelo ID; qualquer outro texto busca pelo nome, aceitando só um pedaço dele e sem diferenciar maiúsculas.
- **Data da última compra**: não pode ser depois de hoje. O formulário já bloqueia no calendário, e o servidor confere de novo (`util/ValidacaoInsumo`).
- **Insumo repetido**: não é possível cadastrar dois insumos com o mesmo nome, marca e lote. O mesmo produto em outro lote é aceito, porque cada lote tem a sua validade. Para mudar a quantidade, edite o insumo que já existe. O banco também garante isso (`uk_insumo` no `banco.sql`).
- **Receita repetida**: duas receitas não podem ter o mesmo nome.

## Como o sistema se protege de erros (revisão do MVP)

- **Campos conferidos antes de gravar** (`util/Parametro`): campo em branco, número ou data inválidos viram uma mensagem clara na tela de aviso, com o botão "Voltar e corrigir". Os tamanhos máximos são os das colunas do banco.
- **Gravar e redirecionar** (`util/Aviso`): depois de cadastrar, alterar ou excluir, o comando devolve `redirect:` e guarda a mensagem de sucesso na sessão; o `AutenticacaoFilter` passa a mensagem para a tela seguinte. Assim, apertar F5 não grava nem exclui de novo.
- **Receita gravada em transação** (`ReceitaDAO.cadastrar`): a receita e os ingredientes entram juntos; se algo falhar, nada fica pela metade.
- **Erros técnicos não aparecem na tela**: o detalhe vai para o log do Tomcat e a tela mostra um aviso simples (`ControleConfeitaria`). Endereço errado ou erro dentro de uma tela mostram `erro.jsp` (configurado no `WEB-INF/web.xml`).
- **Telas com dados não ficam no cache do navegador** (`Cache-Control: no-store`): depois de sair, o botão "voltar" não mostra o estoque.
- **Histórico**: o dashboard mostra as 200 movimentações mais recentes (`MovimentacaoDAO.LIMITE_HISTORICO`) e cada registro traz o nome do insumo e, nas alterações, a quantidade antiga e a nova.
- **Celular**: nas tabelas, cada linha vira um cartão com os rótulos (classe `empilhavel` no `estilo.css`).

## Lixeira e Desfazer (pedido depois da revisão)

**Lixeira (desfazer exclusões).** Excluir um insumo ou uma receita não apaga: só preenche a coluna `excluido_em`. O registro some das telas e aparece em **Lixeira** (menu principal), com o botão **Restaurar**. Depois de 30 dias na lixeira, é apagado de vez (a limpeza roda quando a Lixeira é aberta, em `ConsultaTodosLixeiraAction`).

- O que está na lixeira não impede cadastrar de novo o mesmo nome/marca/lote. No banco, a coluna calculada `ativo` (1 quando ativo, NULL na lixeira) entra nas regras únicas; como o MySQL aceita vários NULL num índice único, a regra só vale entre os ativos.
- Restaurar é barrado quando já existe um igual ativo (insumo) ou uma receita ativa com o mesmo nome.
- Restaurar uma receita exige que todos os ingredientes estejam ativos; a mensagem diz quais restaurar primeiro.
- Um insumo usado por uma receita ativa continua sem poder ser excluído.

**Desfazer (desfazer edições).** Cada alteração de insumo ou receita guarda na tabela `versoes` como o registro estava e como ficou (`util/Foto`). No Dashboard, a linha da alteração ganha o botão **Desfazer** (`DesfazVersaoAction`), que regrava a versão anterior e registra "Desfazer" no histórico.

- Só a **última** alteração de cada registro pode ser desfeita: desfazer uma antiga apagaria, sem aviso, as mais novas.
- Se o registro mudou depois da alteração (por exemplo, a produção de uma receita descontou o estoque), o botão some: voltar a versão antiga apagaria essa mudança.
- Salvar sem mudar nada não cria versão.
- É o padrão Command usado como foi pensado: o comando de alteração guarda o necessário para ser desfeito.

**O que não se desfaz por aqui:** a produção de uma receita (o certo é um estorno, ainda não implementado) e a perda do servidor (para isso existe o backup diário, em `deploy/backup.sh`).

### Mudanças no banco depois de publicado

O `banco.sql` cria o banco completo e só roda num banco novo. Depois que o sistema estiver no ar com dados reais, não dá para apagar e recriar: cada mudança de estrutura deve vir num script separado (`banco-atualiza-1.sql`, `banco-atualiza-2.sql`...) com os `ALTER TABLE`, rodado uma vez no servidor.

## Unidade de medida (pedido depois da revisão)

- Cada insumo tem uma **unidade**: `un`, `g`, `kg`, `ml`, `L`, `pct` (pacote) ou `cx` (caixa). A lista fica em `util/Unidade` e na coluna `unidade` da tabela `insumos`.
- A quantidade aceita até 3 casas decimais (`DECIMAL(10,3)` no banco, `BigDecimal` no Java): 1,5 kg; 0,25 L. Pode digitar com vírgula ou ponto; as telas mostram com vírgula.
- A receita usa a unidade do insumo: "500 g de farinha", "4 un de ovos". As telas mostram sempre quantidade e unidade juntas.
- Mudar a unidade de um insumo não converte a quantidade: ajuste os dois juntos na edição.

## Receitas (pedido depois do relatório)

- **Cadastrar receita**: informa-se o nome e a quantidade de cada insumo que a receita usa.
- **Ver ingredientes**: a tela da receita mostra, para cada ingrediente, a quantidade na receita, a quantidade em estoque e se está **em falta** (estoque menor que a quantidade da receita), com quanto falta.
- **Editar receita**: a mesma tela do cadastro abre preenchida (`Prepara` com `txtId`); ao salvar, o nome e todos os ingredientes são trocados de uma vez (`AtualizaReceitaAction`, em transação).
- **Produzi esta receita**: na tela da receita, informe quantas vezes fez e clique no botão. O sistema confere o estoque de todos os ingredientes; se faltar qualquer um, não desconta nada e diz o que falta. Se tiver tudo, desconta de uma vez (`ProduzReceitaAction`, em transação) e registra no histórico a quantidade antiga e a nova de cada ingrediente (tipo "Produção").
- Um insumo que é ingrediente de alguma receita não pode ser excluído.
- A lista de receitas mostra, para cada uma, quantos ingredientes tem e quantos estão em falta ("Dá para fazer" quando não falta nada).

## Como rodar

1. Execute o script `banco.sql` no MySQL (cria o banco `dejane_cakes_db` e as tabelas). Se você já tinha rodado a versão anterior, execute de novo: ele só acrescenta as tabelas de receitas.
2. Abra a pasta `SistemaConfeitaria` no NetBeans.
   - O `nbproject/build-impl.xml` é gerado pelo NetBeans ao abrir o projeto. Se não for, copie o do SistemaPizzaria e troque `SistemaPizzaria` por `SistemaConfeitaria` dentro dele.
   - O driver do MySQL já vem no projeto, em `web/WEB-INF/lib/mysql-connector-j.jar`; não é preciso baixar nem configurar nada. O `servlet-api.jar` vem do Tomcat: se o NetBeans avisar que não o encontrou, use "Resolve Problems" e aponte para `lib\servlet-api.jar` dentro da pasta do Tomcat 11 (só para esse item; não aponte o driver do MySQL para ele).
3. Confira usuário e senha do MySQL em `src/java/util/FabricaConexao.java` (padrão `root` / `admin`).
4. Execute o projeto (F6): `http://localhost:8080/SistemaConfeitaria/`.

## Estrutura (igual à da pizzaria)

| Pacote | SistemaPizzaria | SistemaConfeitaria |
|---|---|---|
| `controller` | `ControlePizzaria` | `ControleConfeitaria` (Front Controller) |
| `command` | `ICommand`, `RealizaPedidoAction`, `ConsultaTodosPedidoAction`, `ConsultaByIdPedidoAction`, `BuscarPedidoAction`, `AtualizaPedidoAction`, `DeletaPedidoAction` | `ICommand`, `CadastraInsumoAction`, `ConsultaTodosInsumoAction`, `ConsultaByIdInsumoAction`, `BuscarInsumoAction`, `AtualizaInsumoAction`, `DeletaInsumoAction`, `ConsultaTodosMovimentacaoAction`, `PreparaReceitaAction`, `CadastraReceitaAction`, `AtualizaReceitaAction`, `ProduzReceitaAction`, `ConsultaTodosReceitaAction`, `ConsultaByIdReceitaAction`, `DeletaReceitaAction`, `RestauraInsumoAction`, `RestauraReceitaAction`, `ConsultaTodosLixeiraAction`, `DesfazVersaoAction`, `EntraUsuarioAction`, `SaiUsuarioAction`, `CadastraUsuarioAction` (e `ferramentas.Usuarios`, usada pelo terminal do servidor) |
| `factory` | `CommandFactory` | `CommandFactory`, `MaterialFactory` |
| `dao` | `PedidoDAO`, `PagamentoDAO`, `AtendenteDAO` | `InsumoDAO`, `MovimentacaoDAO`, `ReceitaDAO`, `UsuarioDAO`, `VersaoDAO` |
| `decorator` | `Pizza`, `MassaTradicional`, `PizzaDecorator`, `SaborCalabresa`, `BordaCatupiry`… | `Material`, `MaterialBasico`, `MaterialDecorator`, `TipoIngrediente`, `TipoEmbalagem` |
| `model` | `Pedido`, `Cliente`, `Pagamento`, `Atendente` | `Insumo`, `Movimentacao`, `Receita`, `ItemReceita`, `Usuario`, `Versao` |
| `util` | `FabricaConexao` | `FabricaConexao`, `Html`, `Senha`, `Parametro`, `Aviso`, `ValidacaoInsumo`, `Unidade`, `Foto` |
| telas | `index.html`, `formMontarPizza.jsp`, `consultaPedidos.jsp`, `formEditarPedido.jsp`, `feedback.jsp` | `index.html`, `login.jsp`, `primeiroAcesso.jsp`, `formCadastrarInsumo.jsp`, `consultaInsumos.jsp`, `formEditarInsumo.jsp`, `feedback.jsp`, `erro.jsp`, `dashboard.jsp`, `consultaReceitas.jsp`, `formCadastrarReceita.jsp`, `detalheReceita.jsp`, `lixeira.jsp` |

### Padrões de projeto

- **Front Controller**: toda requisição passa pelo `ControleConfeitaria`, com os parâmetros `acao` e `entidade`.
- **Command**: cada ação é uma classe `<acao><entidade>Action` que implementa `ICommand`.
- **Factory**: a `CommandFactory` cria o comando pelo nome (`acao + entidade + "Action"`); a `MaterialFactory` monta o material com o decorator escolhido.
- **DAO**: `InsumoDAO`, `MovimentacaoDAO` e `ReceitaDAO` concentram o SQL; a `FabricaConexao` abre a conexão.
- **Decorator**: o tipo do insumo é montado como na pizza. `MaterialBasico` é a base ("Material") e `TipoIngrediente` / `TipoEmbalagem` acrescentam o tipo à descrição ("Material do tipo Ingrediente"). Os dois tipos são apenas exemplos genéricos, como os sabores da pizzaria; para criar outro, basta uma nova classe que estenda `MaterialDecorator` e uma linha na `MaterialFactory`.

### SOLID

- **Responsabilidade única**: o controlador só encaminha, cada comando trata uma ação, cada DAO cuida de uma tabela e os JSPs só exibem.
- **Aberto/fechado**: uma ação nova é uma classe nova em `command`; um tipo novo é uma classe nova em `decorator`. O controlador e a `CommandFactory` não mudam.
- **Substituição de Liskov**: qualquer `ICommand` serve para o controlador e qualquer `Material` (decorado ou não) serve para os comandos.
- **Segregação de interfaces**: `ICommand` e `Material` têm um único método cada.
- **Inversão de dependência**: o controlador depende da interface `ICommand`, e os comandos dependem da interface `Material`, não das classes concretas.

## O que foi além da lista do relatório

- **Nome do insumo**: o relatório não cita o campo, mas sem ele não há como saber de qual material é cada linha.
- **`util/Html.escapar`**: evita que um texto digitado com aspas ou `<` quebre a página.
