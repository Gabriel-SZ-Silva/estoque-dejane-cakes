<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.List"%>
<%@page import="model.Insumo"%>
<%@page import="model.Movimentacao"%>
<%@page import="util.Html"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Dashboard</title>
    <link rel="stylesheet" href="css/estilo.css">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="theme-color" content="#aa0000">
    <link rel="manifest" href="manifest.json">
    <link rel="icon" href="img/icone-192.png">
    <link rel="apple-touch-icon" href="img/icone-192.png">
</head>
<body>
    <div class="container">
        <h2><img src="img/logo.jpg" class="logo-pequena" alt="">📊 Dashboard</h2>

        <%
        SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat formatoDataHora = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        List<Insumo> vencidos = (List<Insumo>) request.getAttribute("vencidos");
        List<Insumo> vencendo = (List<Insumo>) request.getAttribute("vencendo");
        Integer diasParaVencer = (Integer) request.getAttribute("diasParaVencer");
        if (diasParaVencer == null) {
            diasParaVencer = 7;
        }
        Integer limiteHistorico = (Integer) request.getAttribute("limiteHistorico");
        int totalVencidos = (vencidos != null) ? vencidos.size() : 0;
        int totalVencendo = (vencendo != null) ? vencendo.size() : 0;
        %>

        <%
        String mensagem = (String) request.getAttribute("mensagem");
        if (mensagem != null) {
        %>
            <div class="mensagem sucesso"><%= Html.escapar(mensagem) %></div>
        <%
        }
        %>

        <div class="resumo">
            <a href="#vencidos" class="resumo-item perigo">
                <span class="resumo-numero"><%= totalVencidos %></span>
                produto(s) vencido(s)
            </a>
            <a href="#vencendo" class="resumo-item atencao">
                <span class="resumo-numero"><%= totalVencendo %></span>
                vencem em até <%= diasParaVencer %> dias
            </a>
        </div>

        <h3 id="vencidos">⚠️ Produtos Vencidos</h3>
        <table class="empilhavel">
            <tr>
                <th>Insumo</th>
                <th>Lote</th>
                <th>Quantidade</th>
                <th>Validade</th>
                <th>Situação</th>
                <th>Ações</th>
            </tr>
            <%
            if (totalVencidos > 0) {
                for (Insumo i : vencidos) {
                    long diasVencido = -i.getDiasParaVencer();
            %>
            <tr>
                <td data-rotulo="Insumo"><div class="valor">
                    <%= Html.escapar(i.getNome()) %><br>
                    <small><%= Html.escapar(i.getMarca()) %></small>
                </div></td>
                <td data-rotulo="Lote"><div class="valor"><%= Html.escapar(i.getLote()) %></div></td>
                <td data-rotulo="Quantidade"><div class="valor"><strong><%= Html.escapar(i.getQuantidadeFormatada()) %></strong></div></td>
                <td data-rotulo="Validade"><div class="valor"><%= formatoData.format(i.getValidade()) %></div></td>
                <td data-rotulo="Situação"><div class="valor"><span class="badge em-falta">Venceu há <%= diasVencido %> <%= diasVencido == 1 ? "dia" : "dias" %></span></div></td>
                <td data-rotulo=""><div class="valor">
                    <form action="ControleConfeitaria" method="POST" style="display:inline;">
                        <input type="hidden" name="entidade" value="Insumo">
                        <input type="hidden" name="acao" value="ConsultaById">
                        <input type="hidden" name="txtId" value="<%= i.getId() %>">
                        <button type="submit" class="btn-acao btn-editar">Editar</button>
                    </form>
                </div></td>
            </tr>
            <%
                }
            } else {
            %>
            <tr><td colspan="6" style="text-align: center;">Nenhum produto vencido.</td></tr>
            <%
            }
            %>
        </table>

        <h3 id="vencendo">⏳ Vencem em até <%= diasParaVencer %> Dias</h3>
        <table class="empilhavel">
            <tr>
                <th>Insumo</th>
                <th>Lote</th>
                <th>Quantidade</th>
                <th>Validade</th>
                <th>Situação</th>
                <th>Ações</th>
            </tr>
            <%
            if (totalVencendo > 0) {
                for (Insumo i : vencendo) {
                    long dias = i.getDiasParaVencer();
                    String situacao;
                    if (dias == 0) {
                        situacao = "Vence hoje";
                    } else if (dias == 1) {
                        situacao = "Vence amanhã";
                    } else {
                        situacao = "Vence em " + dias + " dias";
                    }
            %>
            <tr>
                <td data-rotulo="Insumo"><div class="valor">
                    <%= Html.escapar(i.getNome()) %><br>
                    <small><%= Html.escapar(i.getMarca()) %></small>
                </div></td>
                <td data-rotulo="Lote"><div class="valor"><%= Html.escapar(i.getLote()) %></div></td>
                <td data-rotulo="Quantidade"><div class="valor"><strong><%= Html.escapar(i.getQuantidadeFormatada()) %></strong></div></td>
                <td data-rotulo="Validade"><div class="valor"><%= formatoData.format(i.getValidade()) %></div></td>
                <td data-rotulo="Situação"><div class="valor"><span class="badge atencao"><%= situacao %></span></div></td>
                <td data-rotulo=""><div class="valor">
                    <form action="ControleConfeitaria" method="POST" style="display:inline;">
                        <input type="hidden" name="entidade" value="Insumo">
                        <input type="hidden" name="acao" value="ConsultaById">
                        <input type="hidden" name="txtId" value="<%= i.getId() %>">
                        <button type="submit" class="btn-acao btn-editar">Editar</button>
                    </form>
                </div></td>
            </tr>
            <%
                }
            } else {
            %>
            <tr><td colspan="6" style="text-align: center;">Nenhum produto vence nos próximos <%= diasParaVencer %> dias.</td></tr>
            <%
            }
            %>
        </table>

        <h3>🕒 Histórico de Movimentações</h3>
        <% if (limiteHistorico != null) { %>
        <p class="nota">Aparecem as <%= limiteHistorico %> movimentações mais recentes. As anteriores continuam guardadas no banco.
        O botão Desfazer aparece na última alteração de cada insumo ou receita, enquanto ele não mudar de novo.
        Exclusões se desfazem na <a href="ControleConfeitaria?acao=ConsultaTodos&entidade=Lixeira">Lixeira</a>.</p>
        <% } %>
        <table class="empilhavel">
            <tr>
                <th>Data e Hora</th>
                <th>Movimentação</th>
                <th>Descrição</th>
                <th></th>
            </tr>

            <%
            List<Movimentacao> lista = (List<Movimentacao>) request.getAttribute("resultadoConsulta");
            if (lista != null && !lista.isEmpty()) {
                for (Movimentacao m : lista) {
            %>
            <tr>
                <td data-rotulo="Data e hora"><div class="valor"><%= formatoDataHora.format(m.getDataHora()) %></div></td>
                <td data-rotulo="Movimentação"><div class="valor"><span class="badge"><%= Html.escapar(m.getTipo()) %></span></div></td>
                <td data-rotulo="Descrição"><div class="valor"><%= Html.escapar(m.getDescricao()) %></div></td>
                <td data-rotulo=""><div class="valor">
                    <% if (m.isDesfazivel()) { %>
                    <form action="ControleConfeitaria" method="POST" style="display:inline;"
                          onsubmit="return confirm('Voltar o registro para como estava antes desta alteração?');">
                        <input type="hidden" name="acao" value="Desfaz">
                        <input type="hidden" name="entidade" value="Versao">
                        <input type="hidden" name="txtId" value="<%= m.getIdVersao() %>">
                        <button type="submit" class="btn-acao btn-editar">↩ Desfazer</button>
                    </form>
                    <% } %>
                </div></td>
            </tr>
            <%
                }
            } else {
            %>
            <tr><td colspan="4" style="text-align: center;">Nenhuma movimentação registrada ainda.</td></tr>
            <%
            }
            %>
        </table>

        <a href="index.html" class="btn-voltar">⬅ Voltar ao Menu</a>
    </div>
</body>
</html>
