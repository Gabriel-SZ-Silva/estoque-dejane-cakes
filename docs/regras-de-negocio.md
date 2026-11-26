# Regras de negócio

Este documento descreve o que o sistema permite, o que ele recusa e por quê. Toda regra abaixo é conferida no servidor; as que dependem de unicidade também são garantidas pelo próprio banco.

## Insumos

Um insumo é um ingrediente ou uma embalagem, identificado pela combinação **nome + marca + lote**. Todos os campos são obrigatórios.

| Campo | Regra |
| --- | --- |
| Nome, marca | Até 100 caracteres |
| Lote | Até 50 caracteres |
| Tipo | Ingrediente ou Embalagem |
| Quantidade | Zero ou mais, até 3 casas decimais (`DECIMAL(10,3)`); aceita vírgula ou ponto |
| Unidade | `un`, `g`, `kg`, `ml`, `L`, `pct` (pacote), `cx` (caixa) |
| Validade | Data válida; pode estar no passado, para registrar um item já vencido |
| Data da última compra | Nunca depois de hoje (o calendário bloqueia e o servidor confere de novo) |

- **Sem duplicados:** dois insumos ativos não podem ter o mesmo nome, marca e lote. A comparação ignora maiúsculas e acentos. O mesmo produto em outro lote é aceito, porque cada lote tem a sua validade. Para mudar a quantidade, edita-se o insumo existente.
- **Busca:** um número busca pelo ID; qualquer outro texto busca por parte do nome, ignorando maiúsculas e acentos ("acucar" encontra "Açúcar").
- **Edição:** aplica as mesmas regras do cadastro, registra no histórico os valores antigos e novos e pode ser desfeita.
- **Exclusão:** envia para a lixeira. Um insumo usado em alguma receita ativa não pode ser excluído.
- **Unidade:** mudar a unidade não converte a quantidade; os dois são ajustados juntos na edição.
- **Exibição:** quantidades aparecem com vírgula e sem zeros sobrando (1,5 kg, 500 g, 3 un).

## Receitas e produção

- **Receita:** nome único entre as ativas (até 100 caracteres) e pelo menos um ingrediente com quantidade maior que zero. A quantidade usa a unidade do insumo ("500 g de farinha").
- **Situação:** a lista mostra **Dá para fazer** ou **N em falta** (ingredientes sem estoque suficiente para uma produção). O detalhe mostra, por ingrediente, o necessário, o estoque e quanto falta.
- **Edição:** troca o nome e a lista inteira de ingredientes numa única transação; pode ser desfeita.
- **Produção** ("Produzi esta receita"):
  1. A pessoa informa quantas vezes produziu: inteiro de 1 a 1.000.
  2. O sistema confere o estoque de cada ingrediente (quantidade × vezes). Se faltar qualquer um, nada é descontado e a tela lista o que falta.
  3. Havendo estoque, tudo é descontado numa transação. Se o estoque mudar no meio, a produção inteira é cancelada.
  4. O histórico registra a produção com a quantidade antiga e a nova de cada ingrediente.
- **Sem estorno:** uma produção lançada por engano é corrigida editando a quantidade dos insumos.

## Validade e dashboard

Datas sempre no fuso America/Sao_Paulo.

| Situação | Critério | Onde aparece |
| --- | --- | --- |
| Vencido | Validade anterior a hoje | Dashboard (com botão Editar) e selo na lista de insumos |
| Vencendo | Validade de hoje até 7 dias à frente | Dashboard e selos "Vence hoje", "Vence amanhã", "Vence em N dias" |

O prazo de 7 dias está em `ConsultaTodosMovimentacaoAction.DIAS_PARA_VENCER`.

## Histórico de movimentações

Toda operação que altera dados grava uma linha com data, hora, tipo e descrição. O dashboard mostra as 200 mais recentes (`MovimentacaoDAO.LIMITE_HISTORICO`); as anteriores continuam no banco.

| Tipo | Quando |
| --- | --- |
| Cadastro | Novo insumo ou receita |
| Alteração | Edição, com valores antigos e novos |
| Exclusão | Envio para a lixeira |
| Restauração | Volta da lixeira |
| Produção | Receita produzida |
| Desfazer | Alteração desfeita |

## Lixeira

- Excluir preenche a coluna `excluido_em`; o item some das listas, buscas, formulários e do dashboard e aparece na tela **Lixeira**, com o botão **Restaurar**.
- Depois de 30 dias, é apagado de vez. A limpeza roda quando a Lixeira é aberta (`ConsultaTodosLixeiraAction`): primeiro as receitas, depois os insumos que não estão mais em nenhuma receita.
- Um item na lixeira não impede cadastrar outro igual: a coluna calculada `ativo` (1 quando ativo, `NULL` na lixeira) entra nas regras únicas, e o MySQL aceita vários `NULL` num índice único.
- Restaurar é recusado se já existir um igual ativo, ou, no caso de receita, se algum ingrediente dela estiver na lixeira.

## Desfazer

Cada edição de insumo ou receita guarda o antes e o depois na tabela `versoes` (`util/Foto`). O botão **Desfazer**, no histórico do dashboard, volta o registro para o "antes" quando todas as condições valem:

1. É a alteração mais recente daquele registro.
2. Ainda não foi desfeita.
3. O registro está ativo, fora da lixeira.
4. O registro continua igual ao "depois": nada o mudou desde então, nem uma produção.
5. Voltar ao "antes" não cria um duplicado nem coloca na receita um insumo que está na lixeira.

Salvar sem mudar nada não cria versão. Desfazer é registrado no histórico e não pode ser refeito. Cadastros, exclusões e produções não têm Desfazer: a exclusão se resolve pela Lixeira.

## Acesso e perfis

| Ação | Dona | Administrador | Visitante |
| --- | --- | --- | --- |
| Ver insumos, receitas, dashboard e lixeira | Sim | Sim | Sim |
| Cadastrar, editar e excluir | Sim | Sim | Não |
| Produzir, restaurar e desfazer | Sim | Sim | Não |
| Como a conta é criada | Tela de primeiro acesso | Terminal do servidor | Terminal do servidor |

- A tela de primeiro acesso fica aberta enquanto a conta `DONA` não existe, mesmo com administradores e visitantes cadastrados, e exige o `CODIGO_INSTALACAO`. Rodando localmente (NetBeans), o código pode ficar em branco: só é aceito no próprio computador.
- Login único com até 50 caracteres; senha com pelo menos 8.
- Ao tentar uma ação bloqueada, o visitante vê o aviso "Conta de visitante" e nada é gravado.
- Senha esquecida não se recupera: um administrador define outra pelo terminal ([operação](operacao.md)).

## Proteções contra erros

- **Campos conferidos antes de gravar** (`util/Parametro`): campo em branco, número ou data inválidos viram uma mensagem clara, com o botão "Voltar e corrigir".
- **Gravar e redirecionar** (`util/Aviso`): depois de gravar, a resposta é um redirecionamento; apertar F5 não grava de novo.
- **Transações:** receitas e produções gravam tudo ou nada.
- **Erros técnicos** vão para o log do Tomcat; a tela mostra um aviso simples. Endereço inexistente mostra `erro.jsp`.
