<%@page import="model.ItemReceita"%>
<%@page import="model.Receita"%>
<%@page import="util.Html"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Ingredientes da Receita</title>
    <link rel="stylesheet" href="css/estilo.css">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="theme-color" content="#aa0000">
    <link rel="manifest" href="manifest.json">
    <link rel="icon" href="img/icone-192.png">
    <link rel="apple-touch-icon" href="img/icone-192.png">
</head>
<body>
    <div class="container">
        <%
            Receita r = (Receita) request.getAttribute("receita");
            if (r != null) {
                int totalEmFalta = 0;
                for (ItemReceita item : r.getItens()) {
                    if (item.isEmFalta()) {
                        totalEmFalta++;
                    }
                }
        %>
        <h2><img src="img/logo.jpg" class="logo-pequena" alt="">📖 <%= Html.escapar(r.getNome()) %></h2>

        <%
        String mensagem = (String) request.getAttribute("mensagem");
        if (mensagem != null) {
        %>
            <div class="mensagem sucesso">
                <%= Html.escapar(mensagem) %>
            </div>
        <%
        }
        if (totalEmFalta > 0) {
        %>
            <div class="mensagem erro">
                ⚠️ <%= totalEmFalta %> ingrediente(s) em falta para fazer esta receita.
            </div>
        <%
        } else {
        %>
            <div class="mensagem sucesso">
                ✅ Há estoque de todos os ingredientes desta receita.
            </div>
        <%
        }
        %>

        <table class="empilhavel">
            <tr>
                <th>Ingrediente</th>
                <th>Quantidade na Receita</th>
                <th>Em Estoque</th>
                <th>Situação</th>
            </tr>

            <%
            for (ItemReceita item : r.getItens()) {
            %>
            <tr>
                <td data-rotulo="Ingrediente"><div class="valor"><%= Html.escapar(item.getNomeInsumo()) %></div></td>
                <td data-rotulo="Na receita"><div class="valor"><strong><%= Html.escapar(item.getQuantidadeFormatada()) %></strong></div></td>
                <td data-rotulo="Em estoque"><div class="valor"><%= Html.escapar(item.getQuantidadeEstoqueFormatada()) %></div></td>
                <td data-rotulo="Situação"><div class="valor">
                    <% if (item.isEmFalta()) { %>
                    <span class="badge em-falta">Em falta (faltam <%= Html.escapar(item.getQuantidadeFaltanteFormatada()) %>)</span>
                    <% } else { %>
                    <span class="badge disponivel">Disponível</span>
                    <% } %>
                </div></td>
            </tr>
            <%
            }
            %>
        </table>

        <div class="acoes-receita">
            <% if (totalEmFalta == 0 && !r.getItens().isEmpty()) { %>
            <form action="ControleConfeitaria" method="POST" class="form-produzir"
                  onsubmit="return confirm('Dar baixa no estoque dos ingredientes desta receita?');">
                <input type="hidden" name="acao" value="Produz">
                <input type="hidden" name="entidade" value="Receita">
                <input type="hidden" name="txtId" value="<%= r.getId() %>">
                <label for="txtVezes">Quantas vezes</label>
                <input type="number" id="txtVezes" name="txtVezes" value="1" min="1" max="1000" required>
                <button type="submit" class="btn-salvar">🧁 Produzi esta receita: dar baixa no estoque</button>
            </form>
            <% } else { %>
            <div class="mensagem neutra">Para dar baixa no estoque ao produzir, primeiro reponha os ingredientes em falta.</div>
            <% } %>

            <a href="ControleConfeitaria?acao=Prepara&entidade=Receita&txtId=<%= r.getId() %>" class="btn-menu btn-outline">✏️ Editar Receita</a>

            <form action="ControleConfeitaria" method="POST">
                <input type="hidden" name="acao" value="Deleta">
                <input type="hidden" name="entidade" value="Receita">
                <input type="hidden" name="txtId" value="<%= r.getId() %>">
                <button type="submit" class="btn-acao btn-deletar" onclick="return confirm('Enviar esta receita para a lixeira? Dá para restaurar depois, na Lixeira.');">Excluir Receita</button>
            </form>
        </div>

        <% } else { %>
            <h2><img src="img/logo.jpg" class="logo-pequena" alt="">📖 Receita</h2>
            <div class="mensagem erro">Erro: Receita não carregada.</div>
        <% } %>

        <a href="ControleConfeitaria?acao=ConsultaTodos&entidade=Receita" class="btn-voltar">⬅ Voltar às Receitas</a>
    </div>
</body>
</html>
