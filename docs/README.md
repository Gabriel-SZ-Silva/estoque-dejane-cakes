# Documentação

Índice da documentação do Estoque Dejane Cakes. A visão geral e o início rápido estão no [README](../README.md) da raiz.

## Guias

| Documento | Para quem | Conteúdo |
| --- | --- | --- |
| [Regras de negócio](regras-de-negocio.md) | Quem desenvolve ou testa | Insumos, receitas, produção, validade, histórico, lixeira, desfazer e perfis de acesso |
| [Arquitetura](arquitetura.md) | Quem desenvolve | Caminho de uma requisição, pacotes, padrões de projeto, SOLID, modelo de dados e segurança |
| [Instalação local](instalacao-local.md) | Quem desenvolve | Rodar com Docker ou pelo NetBeans, com Tomcat e MySQL |
| [Publicação](publicacao.md) | Mantenedor | Colocar no ar num servidor, atualizar a versão e aplicar migrações do banco |
| [Operação](operacao.md) | Mantenedor | Situação dos containers, contas de usuário, backup, restauração e senhas esquecidas |

## Na raiz do repositório

| Arquivo | Conteúdo |
| --- | --- |
| [CONTRIBUTING.md](../CONTRIBUTING.md) | Fluxo de branches e pull requests, convenções e como rodar localmente |
| [CHANGELOG.md](../CHANGELOG.md) | O que mudou em cada versão |
| [AGENTS.md](../AGENTS.md) | Contexto técnico, acoplamentos por nome ou caminho e cuidados com a produção |
| [LICENSE](../LICENSE) e [NOTICE](../NOTICE) | Licença MIT e itens que não estão cobertos por ela |

## Mantendo a documentação em dia

Mudou uma regra no código? Atualize o documento correspondente e a seção `[Não lançado]` do `CHANGELOG.md` no mesmo pull request.
