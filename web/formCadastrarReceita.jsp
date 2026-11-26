<%@page import="java.math.BigDecimal"%>
<%@page import="java.util.HashMap"%>
<%@page import="util.Unidade"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="model.ItemReceita"%>
<%@page import="model.Receita"%>
<%@page import="model.Insumo"%>
<%@page import="util.Html"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <%
        // A mesma tela serve para cadastrar e para editar: na edição, o comando Prepara manda a receita.
        Receita receita = (Receita) request.getAttribute("receita");
        boolean editando = (receita != null);
        Map<Integer, BigDecimal> quantidadeNaReceita = new HashMap<>();
        if (editando) {
            for (ItemReceita item : receita.getItens()) {
                quantidadeNaReceita.put(item.getIdInsumo(), item.getQuantidade());
            }
        }
    %>
    <title><%= editando ? "Editar Receita" : "Cadastrar Receita" %></title>
    <link rel="stylesheet" href="css/estilo.css">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="theme-color" content="#aa0000">
    <link rel="manifest" href="manifest.json">
    <link rel="icon" href="img/icone-192.png">
    <link rel="apple-touch-icon" href="img/icone-192.png">
</head>
<body>
    <div class="container estreito">
        <h2><img src="img/logo.jpg" class="logo-pequena" alt=""><%= editando ? "✏️ Editar Receita" : "📝 Cadastrar Nova Receita" %></h2>

        <%
        List<Insumo> lista = (List<Insumo>) request.getAttribute("resultadoConsulta");
        if (lista != null && !lista.isEmpty()) {
        %>

        <form action="ControleConfeitaria" method="POST">
            <input type="hidden" name="acao" value="<%= editando ? "Atualiza" : "Cadastra" %>">
            <input type="hidden" name="entidade" value="Receita">
            <% if (editando) { %>
            <input type="hidden" name="txtId" value="<%= receita.getId() %>">
            <% } %>

            <div class="col" style="margin-bottom: 20px;">
                <label>Nome da Receita</label>
                <input type="text" name="txtNome" maxlength="100" required placeholder="Ex: Bolo de chocolate" autofocus
                       value="<%= editando ? Html.escapar(receita.getNome()) : "" %>">
            </div>

            <div class="sessao-tipo">
                <label>🧺 Ingredientes da Receita</label>
                <p style="margin: 8px 0 0 0;">Preencha a quantidade só dos insumos que a receita usa (aceita decimais: 0,5 kg). Deixe os outros em branco.</p>

                <table class="empilhavel">
                    <tr>
                        <th>Insumo</th>
                        <th>Em Estoque</th>
                        <th>Quantidade na Receita</th>
                    </tr>
                    <%
                    for (Insumo i : lista) {
                    %>
                    <tr>
                        <td data-rotulo="Insumo"><div class="valor">
                            <%= Html.escapar(i.getNome()) %><br>
                            <small><%= Html.escapar(i.getMarca()) %></small>
                        </div></td>
                        <td data-rotulo="Em estoque"><div class="valor"><strong><%= Html.escapar(i.getQuantidadeFormatada()) %></strong></div></td>
                        <td data-rotulo="Na receita"><div class="valor"><input type="number" name="txtQuantidade<%= i.getId() %>" min="0" step="0.001" placeholder="0"
                                   value="<%= Unidade.numeroParaCampo(quantidadeNaReceita.get(i.getId())) %>"> <%= Html.escapar(i.getUnidade()) %></div></td>
                    </tr>
                    <%
                    }
                    %>
                </table>
            </div>

            <button type="submit" class="btn-salvar"><%= editando ? "Salvar Alterações" : "Cadastrar Receita" %></button>
        </form>

        <% } else { %>
            <div class="mensagem neutra">Cadastre os insumos antes de montar uma receita.</div>
            <a href="formCadastrarInsumo.jsp" class="btn-menu">➕ Novo Insumo</a>
        <% } %>

        <% if (editando) { %>
        <a href="ControleConfeitaria?acao=ConsultaById&entidade=Receita&txtId=<%= receita.getId() %>" class="btn-voltar">⬅ Cancelar e Voltar</a>
        <% } else { %>
        <a href="ControleConfeitaria?acao=ConsultaTodos&entidade=Receita" class="btn-voltar">⬅ Cancelar e Voltar</a>
        <% } %>
    </div>
</body>
</html>
