<%@page contentType="text/html" pageEncoding="UTF-8" isErrorPage="true"%>
<%
    // Página mostrada pelo Tomcat quando algo falha fora do ControleConfeitaria (endereço errado,
    // erro dentro de uma tela...). Sem ela, o Tomcat mostraria a sua própria tela, com detalhes técnicos.
    Integer codigo = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
    if (codigo == null) {
        codigo = 500;
    }
    if (exception != null) {
        System.err.println("Erro em uma tela do sistema: " + exception);
        exception.printStackTrace();
    }
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Dejane Cakes - Aviso</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/estilo.css">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="theme-color" content="#aa0000">
</head>
<body class="centro">
    <div class="card">
        <img src="<%= request.getContextPath() %>/img/logo.jpg" class="logo" alt="Logomarca Dejane Bolos e Doces">
        <h2>Aviso do Sistema</h2>
        <% if (codigo == 404) { %>
            <div class="mensagem neutra">Esta página não existe. Confira o endereço ou volte ao menu.</div>
        <% } else { %>
            <div class="mensagem erro">Ocorreu um erro inesperado (código <%= codigo %>). Tente de novo; se continuar, avise quem cuida do sistema.</div>
        <% } %>
        <a href="<%= request.getContextPath() %>/index.html" class="btn-menu">Voltar ao Menu Principal</a>
    </div>
</body>
</html>
