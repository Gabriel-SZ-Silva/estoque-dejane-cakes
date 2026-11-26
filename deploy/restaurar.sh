#!/usr/bin/env bash
# Volta o banco para uma cópia de segurança. ATENÇÃO: substitui os dados atuais.
# Uso:  ./deploy/restaurar.sh backups/dejane-2026-11-19_0300.sql.gz
set -euo pipefail
cd "$(dirname "$0")/.."

if [ $# -ne 1 ] || [ ! -f "$1" ]; then
    echo "Informe o arquivo de backup. Exemplo: ./deploy/restaurar.sh backups/dejane-2026-11-19_0300.sql.gz"
    exit 1
fi

read -r -p "Isto apaga os dados atuais e volta para $1. Digite SIM para continuar: " RESPOSTA
if [ "$RESPOSTA" != "SIM" ]; then
    echo "Cancelado."
    exit 1
fi

gunzip -c "$1" | docker compose exec -T banco sh -c 'mysql -uroot -p"$MYSQL_ROOT_PASSWORD"'
echo "Banco restaurado a partir de $1"
