#!/usr/bin/env bash
# Cria, lista, troca a senha ou remove contas de administrador e de visitante.
# Roda dentro do container do app, que já sabe o endereço e a senha do banco (arquivo .env).
#
#   bash deploy/usuarios.sh listar
#   bash deploy/usuarios.sh criar <login> admin|visitante
#   bash deploy/usuarios.sh senha <login>
#   bash deploy/usuarios.sh remover <login>
set -euo pipefail
cd "$(dirname "$0")/.."

ROOT=/usr/local/tomcat/webapps/ROOT/WEB-INF
# Memória pequena: o Tomcat já está rodando no mesmo container.
docker compose exec app java -Dstdout.encoding=UTF-8 -Xmx64m -XX:+UseSerialGC -Xss512k \
    -cp "$ROOT/classes:$ROOT/lib/*" ferramentas.Usuarios "$@"
