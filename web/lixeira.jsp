<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.List"%>
<%@page import="model.Insumo"%>
<%@page import="model.Receita"%>
<%@page import="util.Html"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Lixeira</title>
    <link rel="stylesheet" href="css/estilo.css">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="theme-color" content="#aa0000">
    <link rel="manifest" href="manifest.json">
    <link rel="icon" href="img/icone-192.png">
    <link rel="apple-touch-icon" href="img/icone-192.png">
</head>
<body>
    <div class="container">
        <h2><img src="img/logo.jpg" class="logo-pequena" alt="">🗑️ Lixeira</h2>

        <%
        SimpleDateFormat formatoDataHora = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Integer diasNaLixeira = (Integer) request.getAttribute("diasNaLixeira");
        if (diasNaLixeira == null) {
            diasNaLixeira = 30;
        }
        String mensagem = (String) request.getAttribute("mensagem");
        if (mensagem != null) {
        %>
            <div class="mensagem sucesso"><%= Html.escapar(mensagem) %></div>
        <%
        }
        %>

        <p class="nota">O que é excluído fica aqui por <%= diasNaLixeira %> dias e pode ser restaurado como estava. Depois disso, é apagado de vez.</p>

        <h3>📦 Insumos Excluídos</h3>
        <table class="empilhavel">
            <tr>
                <th>Insumo</th>
                <th>Lote</th>
                <th>Quantidade</th>
                <th>Excluído em</th>
                <th>Ações</th>
            </tr>
            <%
            List<Insumo> insumos = (List<Insumo>) request.getAttribute("insumosExcluidos");
            if (insumos != null && !insumos.isEmpty()) {
                for (Insumo i : insumos) {
            %>
            <tr>
                <td data-rotulo="Insumo"><div class="valor"><%= Html.escapar(i.getNome()) %><br><small><%= Html.escapar(i.getMarca()) %></small></div></td>
                <td data-rotulo="Lote"><div class="valor"><%= Html.escapar(i.getLote()) %></div></td>
                <td data-rotulo="Quantidade"><div class="valor"><strong><%= Html.escapar(i.getQuantidadeFormatada()) %></strong></div></td>
                <td data-rotulo="Excluído em"><div class="valor"><%= formatoDataHora.format(i.getExcluidoEm()) %></div></td>
                <td data-rotulo=""><div class="valor">
                    <form action="ControleConfeitaria" method="POST" style="display:inline;">
                        <input type="hidden" name="acao" value="Restaura">
                        <input type="hidden" name="entidade" value="Insumo">
                        <input type="hidden" name="txtId" value="<%= i.getId() %>">
                        <button type="submit" class="btn-acao btn-editar">↩ Restaurar</button>
                    </form>
                </div></td>
            </tr>
            <%
                }
            } else {
            %>
            <tr><td colspan="5" style="text-align: center;">Nenhum insumo na lixeira.</td></tr>
            <%
            }
            %>
        </table>

        <h3>📖 Receitas Excluídas</h3>
        <table class="empilhavel">
            <tr>
                <th>Receita</th>
                <th>Excluída em</th>
                <th>Ações</th>
            </tr>
            <%
            List<Receita> receitas = (List<Receita>) request.getAttribute("receitasExcluidas");
            if (receitas != null && !receitas.isEmpty()) {
                for (Receita r : receitas) {
            %>
            <tr>
                <td data-rotulo="Receita"><div class="valor"><%= Html.escapar(r.getNome()) %></div></td>
                <td data-rotulo="Excluída em"><div class="valor"><%= formatoDataHora.format(r.getExcluidoEm()) %></div></td>
                <td data-rotulo=""><div class="valor">
                    <form action="ControleConfeitaria" method="POST" style="display:inline;">
                        <input type="hidden" name="acao" value="Restaura">
                        <input type="hidden" name="entidade" value="Receita">
                        <input type="hidden" name="txtId" value="<%= r.getId() %>">
                        <button type="submit" class="btn-acao btn-editar">↩ Restaurar</button>
                    </form>
                </div></td>
            </tr>
            <%
                }
            } else {
            %>
            <tr><td colspan="3" style="text-align: center;">Nenhuma receita na lixeira.</td></tr>
            <%
            }
            %>
        </table>

        <a href="index.html" class="btn-voltar">⬅ Voltar ao Menu</a>
    </div>
</body>
</html>
