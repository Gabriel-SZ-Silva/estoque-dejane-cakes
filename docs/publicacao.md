# Publicação em servidor

O sistema roda em produção num servidor Linux com Docker Compose: MySQL, Tomcat e Caddy (HTTPS automático com Let's Encrypt). Esta é a configuração em uso desde 23/11/2026.

## Ambiente de produção

| Item | Configuração |
| --- | --- |
| Servidor | VM Microsoft Azure (assinatura Azure for Students), Standard B2ats_v2: 2 vCPU, 1 GB de RAM + 2 GB de swap |
| Sistema | Ubuntu Server 24.04 LTS |
| Containers | `caddy` (portas 80/443), `app` (Tomcat 11 + Java 25), `banco` (MySQL 8.4 com volume persistente) |
| Memória | `docker-compose.override.yml` limita banco (350 MB), app (450 MB) e Caddy (64 MB) |
| Endereço | Subdomínio gratuito do DuckDNS, com HTTPS |
| Backup | Diário às 03:00 (horário de Brasília), 14 dias guardados |

Qualquer servidor Linux com 1 GB de RAM ou mais e Docker serve. Com 4 GB ou mais, o `docker-compose.override.yml` pode ser removido.

## Primeira publicação

1. **Servidor:** Ubuntu 24.04 com as portas 22, 80 e 443 liberadas no firewall da nuvem.
2. **Memória extra** (essencial com 1 GB):
   ```bash
   sudo fallocate -l 2G /swapfile && sudo chmod 600 /swapfile
   sudo mkswap /swapfile && sudo swapon /swapfile
   echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
   sudo timedatectl set-timezone America/Sao_Paulo
   ```
3. **Docker:**
   ```bash
   curl -fsSL https://get.docker.com | sudo sh
   sudo usermod -aG docker $USER   # depois, saia e entre de novo
   ```
4. **Endereço:** crie um subdomínio em [duckdns.org](https://www.duckdns.org) (ou use um domínio próprio) apontando para o IP público do servidor.
5. **Código:** envie o projeto para o servidor (`git clone` ou zip + `scp`) numa pasta, por exemplo `~/SistemaConfeitaria`.
6. **Configuração:**
   ```bash
   cd ~/SistemaConfeitaria
   cp .env.exemplo .env
   openssl rand -base64 18   # rode duas vezes: uma senha para cada variável de banco
   nano .env
   ```
   Preencha `DOMINIO`, `DB_SENHA`, `DB_SENHA_ROOT` e `CODIGO_INSTALACAO`. Guarde uma cópia do `.env` em lugar seguro: as senhas do banco não podem mudar depois da primeira subida.
7. **Subir:**
   ```bash
   docker compose up -d --build
   docker compose ps
   ```
   Na primeira vez, o banco é criado a partir do `banco.sql`. Em um ou dois minutos, o Caddy obtém o certificado e o sistema abre em `https://<DOMINIO>`.
8. **Contas:** a dona cria a dela em "Primeiro acesso", com o `CODIGO_INSTALACAO`. Administradores e visitantes são criados pelo terminal ([operação](operacao.md)).
9. **Backup agendado:** veja [operação](operacao.md#backup).

## Variáveis do `.env`

| Variável | Uso |
| --- | --- |
| `DOMINIO` | Endereço do sistema, sem `https://`. O Caddy pede o certificado para ele |
| `DB_SENHA` | Senha do usuário `dejane` do MySQL, usada pelo app |
| `DB_SENHA_ROOT` | Senha do `root` do MySQL, usada nos backups e na gestão de contas |
| `CODIGO_INSTALACAO` | Código pedido na tela de primeiro acesso |

O `.env` nunca vai para o repositório (está no `.gitignore`).

## Atualizar para uma versão nova

```bash
cd ~/SistemaConfeitaria
# traga os arquivos novos (git pull, ou unzip -o do pacote enviado)
docker compose up -d --build
```

Os dados ficam no volume `dados-mysql` e não são afetados. **Nunca** use `docker compose down -v`: o `-v` apaga o volume do banco.

## Mudanças no banco

O `banco.sql` só roda num banco vazio. Com o sistema no ar, cada mudança de estrutura vem num script de migração em `deploy/`, executado uma única vez:

```bash
docker compose exec -T banco sh -c 'mysql -uroot -p"$MYSQL_ROOT_PASSWORD" dejane_cakes_db' < deploy/<script>.sql
```

| Script | Versão | Conteúdo | Situação em produção |
| --- | --- | --- | --- |
| `deploy/atualiza-usuarios.sql` | 1.1.0 | Coluna `papel` em `usuarios` | Aplicado em 23/11/2026 |
