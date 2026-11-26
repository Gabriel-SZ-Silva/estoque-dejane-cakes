# Instalação local (desenvolvimento)

Duas formas de rodar o sistema no seu computador: com Docker, sem instalar Java nem MySQL, ou pelo NetBeans, para desenvolver e depurar.

## Opção A: Docker

Requisitos: [Docker Desktop](https://www.docker.com/products/docker-desktop/) (Windows/macOS) ou Docker Engine (Linux).

```bash
git clone https://github.com/Gabriel-SZ-Silva/estoque-dejane-cakes.git
cd estoque-dejane-cakes
cp .env.exemplo .env      # no Windows: copy .env.exemplo .env
```

Edite o `.env`: para uso local, `DOMINIO=localhost` e senhas quaisquer. Depois:

```bash
docker compose up -d --build
```

Acesse `https://localhost` (o navegador vai avisar sobre o certificado local; pode continuar). Na primeira vez, use **Primeiro acesso** com o `CODIGO_INSTALACAO` do `.env`.

Para parar: `docker compose down`. Os dados ficam no volume `dados-mysql` e voltam na próxima subida.

## Opção B: NetBeans + Tomcat + MySQL

### Requisitos

| Programa | Versão |
| --- | --- |
| JDK | 25 |
| Apache NetBeans | 31 ou mais recente |
| Apache Tomcat | 11 |
| MySQL Server e MySQL Workbench | 8.4 |

### Passos

1. **Banco:** no MySQL Workbench, abra e execute o `banco.sql`. Ele cria o banco `dejane_cakes_db` com todas as tabelas.
2. **Credenciais:** sem variáveis de ambiente, `src/java/util/FabricaConexao.java` conecta em `jdbc:mysql://localhost:3306/dejane_cakes_db` com usuário `root` e senha `admin`. Se o seu MySQL usa outra senha, defina as variáveis `DB_URL`, `DB_USUARIO` e `DB_SENHA` ou ajuste os valores padrão nesse arquivo (sem commitar senhas reais).
3. **Tomcat no NetBeans:** em *Tools → Servers → Add Server*, cadastre o Tomcat 11. No Windows, abra o NetBeans como administrador na primeira vez, para ele conseguir ler a pasta do Tomcat.
4. **Abrir o projeto:** *File → Open Project* e escolha a pasta clonada.
   - Se aparecer "Resolve Problems" para o `servlet-api.jar`, aponte para `lib\servlet-api.jar` dentro da pasta do Tomcat 11.
   - O driver do MySQL já vem em `web/WEB-INF/lib/mysql-connector-j.jar`; não aponte o driver para outro arquivo.
5. **Executar:** F6. O sistema abre em `http://localhost:8080/SistemaConfeitaria/`.
6. **Primeiro acesso:** na tela de login, clique em "Primeiro acesso? Criar usuário". Localmente, o código de instalação pode ficar em branco.

### Problemas comuns

| Sintoma | Solução |
| --- | --- |
| `ClassNotFoundException: com.mysql.cj.jdbc.Driver` | Confira se `web/WEB-INF/lib/mysql-connector-j.jar` existe e se o `nbproject/project.properties` aponta para ele |
| "Stopping of Tomcat failed" | Feche o Tomcat preso na porta 8080 (no PowerShell: `Get-NetTCPConnection -LocalPort 8080 \| ForEach-Object { Stop-Process -Id $_.OwningProcess -Force }`) |
| Erro de acesso ao banco | Confira usuário, senha e se o MySQL está rodando |
| Build sem `WEB-INF` | Confira se `web/WEB-INF/web.xml` existe |
