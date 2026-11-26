<%@page import="util.Html"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Confeitaria - Status da Operação</title>
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
        <h2>Aviso do Sistema</h2>

        <%
        String mensagem = (String) request.getAttribute("mensagem");
        String mensagemErro = (String) request.getAttribute("mensagemErro");

        if (mensagem != null) {
        %>
            <div class="mensagem sucesso">
                <strong>Sucesso!</strong><br><br><%= Html.escapar(mensagem) %>
            </div>
        <%
        } else if (mensagemErro != null) {
        %>
            <div class="mensagem erro">
                <strong>Ocorreu um problema:</strong><br><br><%= Html.escapar(mensagemErro) %>
            </div>
            <a href="javascript:history.back()" class="btn-menu btn-corrigir">⬅ Voltar e corrigir</a>
        <%
        } else {
        %>
            <div class="mensagem neutra">
                Nenhuma operação foi processada no momento.
            </div>
        <%
        }
        %>

        <a href="index.html" class="btn-menu">Voltar ao Menu Principal</a>
    </div>
</body>
</html>
