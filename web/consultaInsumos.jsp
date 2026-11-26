<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.List"%>
<%@page import="model.Insumo"%>
<%@page import="util.Html"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Lista de Insumos</title>
    <link rel="stylesheet" href="css/estilo.css">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="theme-color" content="#aa0000">
    <link rel="manifest" href="manifest.json">
    <link rel="icon" href="img/icone-192.png">
    <link rel="apple-touch-icon" href="img/icone-192.png">
</head>
<body>
    <div class="container">
        <h2><img src="img/logo.jpg" class="logo-pequena" alt="">📋 Insumos em Estoque</h2>
        <div class="barra-busca">
            <%-- A busca só consulta, por isso vai por GET: o endereço fica no histórico e o F5 repete a busca sem avisos. --%>
            <form action="ControleConfeitaria" method="GET">
                <input type="hidden" name="acao" value="Buscar">
                <input type="hidden" name="entidade" value="Insumo">

                <label for="txtBusca">Buscar por nome ou ID:</label>
                <%
                String buscaAtual = (String) request.getAttribute("busca");
                %>
                <input type="text" id="txtBusca" name="txtBusca" class="campo-busca" placeholder="Ex: farinha ou 1"
                       value="<%= buscaAtual == null ? "" : Html.escapar(buscaAtual) %>" required>
                <button type="submit" class="btn-busca">Pesquisar</button>
            </form>

            <a href="ControleConfeitaria?acao=ConsultaTodos&entidade=Insumo" class="btn-busca btn-limpar">Limpar / Ver Todos</a>

            <a href="formCadastrarInsumo.jsp" class="btn-busca">➕ Novo Insumo</a>
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
                <th>Insumo</th>
                <th>Tipo</th>
                <th>Lote</th>
                <th>Quantidade</th>
                <th>Validade</th>
                <th>Última Compra</th>
                <th>Ações</th>
            </tr>

            <%
            SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy");
            List<Insumo> lista = (List<Insumo>) request.getAttribute("resultadoConsulta");
            if (lista != null && !lista.isEmpty()) {
                for (Insumo i : lista) {
            %>
            <tr>
                <td data-rotulo="ID"><div class="valor"><strong>#<%= i.getId() %></strong></div></td>
                <td data-rotulo="Insumo"><div class="valor">
                    <%= Html.escapar(i.getNome()) %><br>
                    <small><%= Html.escapar(i.getMarca()) %></small>
                </div></td>
                <td data-rotulo="Tipo"><div class="valor">
                    <span class="badge"><%= Html.escapar(i.getTipo()) %></span>
                </div></td>
                <td data-rotulo="Lote"><div class="valor"><%= Html.escapar(i.getLote()) %></div></td>
                <td data-rotulo="Quantidade"><div class="valor"><strong><%= Html.escapar(i.getQuantidadeFormatada()) %></strong></div></td>
                <td data-rotulo="Validade"><div class="valor">
                    <%= formatoData.format(i.getValidade()) %>
                    <%
                    long dias = i.getDiasParaVencer();
                    if (dias < 0) {
                    %>
                    <br><span class="badge em-falta">Vencido</span>
                    <%
                    } else if (dias <= 7) {
                    %>
                    <br><span class="badge atencao"><%= dias == 0 ? "Vence hoje" : (dias == 1 ? "Vence amanhã" : "Vence em " + dias + " dias") %></span>
                    <%
                    }
                    %>
                </div></td>
                <td data-rotulo="Última compra"><div class="valor"><%= formatoData.format(i.getDataUltimaCompra()) %></div></td>

                <td data-rotulo="" style="min-width: 130px;"><div class="valor">
                    <form action="ControleConfeitaria" method="POST" style="display:inline;">
                        <input type="hidden" name="entidade" value="Insumo">
                        <input type="hidden" name="acao" value="ConsultaById">
                        <input type="hidden" name="txtId" value="<%= i.getId() %>">
                        <button type="submit" class="btn-acao btn-editar">Editar</button>
                    </form>

                    <form action="ControleConfeitaria" method="POST" style="display:inline;">
                        <input type="hidden" name="entidade" value="Insumo">
                        <input type="hidden" name="acao" value="Deleta">
                        <input type="hidden" name="txtId" value="<%= i.getId() %>">
                        <button type="submit" class="btn-acao btn-deletar" onclick="return confirm('Enviar este insumo para a lixeira? Dá para restaurar depois, na Lixeira.');">Excluir</button>
                    </form>
                </div></td>
            </tr>
            <%
                }
            } else {
            %>
            <tr><td colspan="8" style="text-align: center;">Nenhum insumo cadastrado ainda.</td></tr>
            <%
            }
            %>
        </table>

        <a href="index.html" class="btn-voltar">⬅ Voltar ao Menu</a>
    </div>
</body>
</html>
