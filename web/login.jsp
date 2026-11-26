<%@page import="util.Html"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Entrar - Dejane Cakes</title>
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
        <h1>Controle de Estoque</h1>

        <%
        String mensagem = (String) request.getAttribute("mensagem");
        String mensagemErro = (String) request.getAttribute("mensagemErro");
        if (mensagem == null && request.getParameter("saiu") != null) {
            mensagem = "Você saiu do sistema.";
        }
        if (mensagem != null) {
        %>
            <div class="mensagem sucesso"><%= Html.escapar(mensagem) %></div>
        <%
        }
        if (mensagemErro != null) {
        %>
            <div class="mensagem erro"><%= Html.escapar(mensagemErro) %></div>
        <%
        }
        %>

        <form action="ControleConfeitaria" method="POST" class="form-login">
            <input type="hidden" name="acao" value="Entra">
            <input type="hidden" name="entidade" value="Usuario">

            <div class="col">
                <label for="txtLogin">Usuário</label>
                <input type="text" id="txtLogin" name="txtLogin" required autocomplete="username" autocapitalize="none" autofocus>
            </div>
            <div class="col">
                <label for="txtSenha">Senha</label>
                <input type="password" id="txtSenha" name="txtSenha" required autocomplete="current-password">
            </div>

            <button type="submit" class="btn-salvar">Entrar</button>
        </form>

        <a href="primeiroAcesso.jsp" class="btn-voltar">Primeiro acesso? Criar usuário</a>
    </div>
    <script>
        // Ativa o modo "app" no celular (ver sw.js).
        if ('serviceWorker' in navigator) {
            navigator.serviceWorker.register('sw.js').catch(function () { /* sem app offline neste navegador; o sistema segue normal */ });
        }
    </script>
</body>
</html>
