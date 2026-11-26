# Atalhos para os comandos do Docker Compose. Rode na pasta do projeto.
# Cada alvo só chama o comando mostrado; sem make, use os comandos diretamente.

SERVICO ?=

.PHONY: help build up down logs ps backup

help: ## Lista os alvos disponíveis
	@grep -E '^[a-z]+:.*## ' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*## "} {printf "  make %-8s %s\n", $$1, $$2}'

build: ## Compila a aplicação na imagem Docker (o mesmo que o workflow Build)
	docker build -t estoque-dejane-cakes .

up: ## Sobe banco, app e Caddy, recompilando o app (exige o .env preenchido)
	docker compose up -d --build

# Sem -v de propósito: o -v apagaria o volume dados-mysql, com todo o banco.
down: ## Para os containers e mantém os dados do banco
	docker compose down

logs: ## Acompanha os logs (um serviço só: make logs SERVICO=app)
	docker compose logs -f --tail=100 $(SERVICO)

ps: ## Mostra a situação dos containers
	docker compose ps

# A restauração (deploy/restaurar.sh) fica fora do Makefile porque substitui os dados atuais.
backup: ## Gera uma cópia do banco em backups/ (containers rodando)
	bash deploy/backup.sh
