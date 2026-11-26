<%@page import="java.util.List"%>
<%@page import="model.Receita"%>
<%@page import="util.Html"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Lista de Receitas</title>
    <link rel="stylesheet" href="css/estilo.css">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="theme-color" content="#aa0000">
    <link rel="manifest" href="manifest.json">
    <link rel="icon" href="img/icone-192.png">
    <link rel="apple-touch-icon" href="img/icone-192.png">
</head>
<body>
    <div class="container">
        <h2><img src="img/logo.jpg" class="logo-pequena" alt="">📖 Receitas</h2>
        <div class="barra-busca">
            <span>Abra uma receita para ver os ingredientes e o que está em falta.</span>
            <a href="ControleConfeitaria?acao=Prepara&entidade=Receita" class="btn-busca">➕ Nova Receita</a>
        </div>
        <%
        String mensagem = (String) request.getAttribute("mensagem");
        if (mensagem != null) {
        %>
            <div class="mensagem sucesso">
                <%= Html.escapar(mensagem) %>
            </div>
        <%
        }
        %>

        <table class="empilhavel">
            <tr>
                <th>ID</th>
                <th>Receita</th>
                <th>Ingredientes</th>
                <th>Situação</th>
                <th>Ações</th>
            </tr>

            <%
            List<Receita> lista = (List<Receita>) request.getAttribute("resultadoConsulta");
            if (lista != null && !lista.isEmpty()) {
                for (Receita r : lista) {
            %>
            <tr>
                <td data-rotulo="ID"><div class="valor"><strong>#<%= r.getId() %></strong></div></td>
                <td data-rotulo="Receita"><div class="valor"><%= Html.escapar(r.getNome()) %></div></td>
                <td data-rotulo="Ingredientes"><div class="valor"><%= r.getTotalItens() %></div></td>
                <td data-rotulo="Situação"><div class="valor">
                    <% if (r.getTotalEmFalta() != null && r.getTotalEmFalta() > 0) { %>
                    <span class="badge em-falta"><%= r.getTotalEmFalta() %> em falta</span>
                    <% } else { %>
                    <span class="badge disponivel">Dá para fazer</span>
                    <% } %>
                </div></td>

                <td data-rotulo="" style="min-width: 360px;"><div class="valor">
                    <form action="ControleConfeitaria" method="POST" style="display:inline;">
                        <input type="hidden" name="entidade" value="Receita">
                        <input type="hidden" name="acao" value="ConsultaById">
                        <input type="hidden" name="txtId" value="<%= r.getId() %>">
                        <button type="submit" class="btn-acao btn-editar">Ver Ingredientes</button>
                    </form>

                    <a href="ControleConfeitaria?acao=Prepara&entidade=Receita&txtId=<%= r.getId() %>" class="btn-acao btn-editar">Editar</a>

                    <form action="ControleConfeitaria" method="POST" style="display:inline;">
                        <input type="hidden" name="entidade" value="Receita">
                        <input type="hidden" name="acao" value="Deleta">
                        <input type="hidden" name="txtId" value="<%= r.getId() %>">
                        <button type="submit" class="btn-acao btn-deletar" onclick="return confirm('Enviar esta receita para a lixeira? Dá para restaurar depois, na Lixeira.');">Excluir</button>
                    </form>
                </div></td>
            </tr>
            <%
                }
            } else {
            %>
            <tr><td colspan="5" style="text-align: center;">Nenhuma receita cadastrada ainda.</td></tr>
            <%
            }
            %>
        </table>

        <a href="index.html" class="btn-voltar">⬅ Voltar ao Menu</a>
    </div>
</body>
</html>
