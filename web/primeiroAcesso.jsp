<%@page import="util.Html"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Primeiro Acesso - Dejane Cakes</title>
    <link rel="stylesheet" href="css/estilo.css">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="theme-color" content="#aa0000">
    <link rel="manifest" href="manifest.json">
    <link rel="icon" href="img/icone-192.png">
    <link rel="apple-touch-icon" href="img/icone-192.png">
</head>
<body class="centro">
    <div class="card">
        <img src="img/logo.jpg" class="logo" alt="Logomarca Dejane Bolos e Doces">
        <h2>Criar o Acesso Principal</h2>
        <p class="texto-ajuda">Conta da dona do negócio, com acesso total ao sistema.</p>

        <%
        String mensagemErro = (String) request.getAttribute("mensagemErro");
        if (mensagemErro != null) {
        %>
            <div class="mensagem erro"><%= Html.escapar(mensagemErro) %></div>
        <%
        }
        %>

        <form action="ControleConfeitaria" method="POST" class="form-login">
            <input type="hidden" name="acao" value="Cadastra">
            <input type="hidden" name="entidade" value="Usuario">

            <div class="col">
                <label for="txtLogin">Usuário</label>
                <input type="text" id="txtLogin" name="txtLogin" required maxlength="50" autocomplete="username" autocapitalize="none" placeholder="Ex: dejane">
            </div>
            <div class="col">
                <label for="txtSenha">Senha (pelo menos 8 caracteres)</label>
                <input type="password" id="txtSenha" name="txtSenha" required minlength="8" autocomplete="new-password">
            </div>
            <div class="col">
                <label for="txtConfirmacao">Repita a senha</label>
                <input type="password" id="txtConfirmacao" name="txtConfirmacao" required minlength="8" autocomplete="new-password">
            </div>
            <div class="col">
                <label for="txtCodigo">Código de instalação</label>
                <input type="text" id="txtCodigo" name="txtCodigo" autocomplete="off" autocapitalize="none">
                <small>Está no arquivo .env do servidor (CODIGO_INSTALACAO). Rodando pelo NetBeans, deixe em branco.</small>
            </div>

            <button type="submit" class="btn-salvar">Criar Acesso</button>
        </form>

        <a href="login.jsp" class="btn-voltar">⬅ Voltar para Entrar</a>
    </div>
</body>
</html>
