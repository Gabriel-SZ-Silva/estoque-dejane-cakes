#!/usr/bin/env bash
# Cópia de segurança do banco. O cron chama este script todo dia (ver o guia).
# Uso manual:  ./deploy/backup.sh
set -euo pipefail
cd "$(dirname "$0")/.."

PASTA=backups
DIAS_GUARDADOS=14
mkdir -p "$PASTA"
ARQUIVO="$PASTA/dejane-$(date +%Y-%m-%d_%H%M).sql.gz"

docker compose exec -T banco sh -c 'mysqldump -uroot -p"$MYSQL_ROOT_PASSWORD" --single-transaction --databases dejane_cakes_db' \
    | gzip > "$ARQUIVO"

# Apaga as cópias com mais de DIAS_GUARDADOS dias.
find "$PASTA" -name 'dejane-*.sql.gz' -mtime +"$DIAS_GUARDADOS" -delete
echo "Backup salvo em $ARQUIVO"
