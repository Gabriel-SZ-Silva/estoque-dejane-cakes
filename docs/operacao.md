# Operação

Tarefas do dia a dia no servidor. Todos os comandos rodam dentro da pasta do projeto (por exemplo, `~/SistemaConfeitaria`).

## Situação do sistema

| Tarefa | Comando |
| --- | --- |
| Ver se os containers estão rodando | `docker compose ps` |
| Ver erros do app | `docker compose logs app --tail 50` |
| Ver erros do HTTPS | `docker compose logs caddy --tail 30` |
| Uso de memória | `free -h` e `docker stats --no-stream` |
| Reiniciar o app (desconecta todos) | `docker compose restart app` |

## Contas de usuário

Administradores e visitantes são gerenciados só pelo terminal, de propósito: o site não tem tela de gerenciamento de contas.

```bash
bash deploy/usuarios.sh listar
bash deploy/usuarios.sh criar <login> admin
bash deploy/usuarios.sh criar <login> visitante
bash deploy/usuarios.sh senha <login>      # troca a senha de qualquer conta, inclusive a da dona
bash deploy/usuarios.sh remover <login>
```

- `criar` e `senha` pedem a senha duas vezes, sem mostrar na tela (mínimo de 8 caracteres). A resposta leva alguns segundos.
- Depois de `remover`, rode `docker compose restart app` para desconectar quem estava logado com a conta.
- Remover a conta `DONA` reabre a tela de primeiro acesso.

## Backup

`deploy/backup.sh` gera uma cópia compactada do banco em `backups/` e apaga as com mais de 14 dias.

Agendar todo dia às 03:00 (uma vez, no servidor):

```bash
(crontab -l 2>/dev/null; echo '0 3 * * * bash /home/<usuario>/SistemaConfeitaria/deploy/backup.sh >> /home/<usuario>/backup.log 2>&1') | crontab -
crontab -l
```

Conferir: `cat ~/backup.log` e `ls -lh backups`.

Guarde também uma cópia **fora do servidor**, ao menos uma vez por semana:

```bash
scp <servidor>:~/SistemaConfeitaria/backups/<arquivo>.sql.gz .
```

## Restaurar um backup

```bash
bash deploy/restaurar.sh backups/<arquivo>.sql.gz
```

O script pede que você digite `SIM`, porque substitui todos os dados atuais pelos do backup.

## Esqueceram a senha

- **Dona, administrador ou visitante:** `bash deploy/usuarios.sh senha <login>`.
- **Senhas do banco ou código de instalação:** estão no `.env` do servidor (`cat .env`).
- **Chave SSH do servidor:** no portal da Azure, VM → Ajuda → Redefinir senha → Redefinir chave pública SSH.
