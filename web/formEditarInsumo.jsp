<%@page import="java.util.Map"%>
<%@page import="util.Unidade"%>
<%@page import="model.Insumo"%>
<%@page import="util.Html"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Editar Insumo</title>
    <link rel="stylesheet" href="css/estilo.css">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="theme-color" content="#aa0000">
    <link rel="manifest" href="manifest.json">
    <link rel="icon" href="img/icone-192.png">
    <link rel="apple-touch-icon" href="img/icone-192.png">
</head>
<body>
    <div class="container estreito">
        <h2><img src="img/logo.jpg" class="logo-pequena" alt="">✏️ Atualizar Insumo</h2>

        <%
            Insumo i = (Insumo) request.getAttribute("insumo");
            if (i != null) {
                String tipo = i.getTipo();
        %>

        <form action="ControleConfeitaria" method="POST">
            <input type="hidden" name="acao" value="Atualiza">
            <input type="hidden" name="entidade" value="Insumo">
            <input type="hidden" name="txtId" value="<%= i.getId() %>">

            <div class="linha">
                <div class="col">
                    <label>Nome do Insumo</label>
                    <input type="text" name="txtNome" maxlength="100" value="<%= Html.escapar(i.getNome()) %>" required>
                </div>
                <div class="col">
                    <label>Marca</label>
                    <input type="text" name="txtMarca" maxlength="100" value="<%= Html.escapar(i.getMarca()) %>" required>
                </div>
            </div>

            <div class="sessao-tipo">
                <label>📦 Alterar Tipo (Padrão Decorator)</label>
                <div class="radio-group">
                    <label class="radio-item"><input type="radio" name="radTipo" value="Ingrediente" <%= !tipo.contains("Embalagem") ? "checked" : "" %>> Ingrediente</label>
                    <label class="radio-item"><input type="radio" name="radTipo" value="Embalagem" <%= tipo.contains("Embalagem") ? "checked" : "" %>> Embalagem</label>
                </div>
            </div>

            <% String unidadeAtual = (i.getUnidade() == null) ? Unidade.PADRAO : i.getUnidade(); %>
            <div class="linha">
                <div class="col">
                    <label>Quantidade em Estoque</label>
                    <input type="number" name="txtQuantidade" value="<%= Unidade.numeroParaCampo(i.getQuantidade()) %>" required min="0" step="0.001">
                </div>
                <div class="col">
                    <label>Unidade</label>
                    <select name="selUnidade">
                        <%
                        for (Map.Entry<String, String> u : Unidade.TODAS.entrySet()) {
                        %>
                        <option value="<%= u.getKey() %>" <%= u.getKey().equals(unidadeAtual) ? "selected" : "" %>><%= u.getValue() %></option>
                        <%
                        }
                        %>
                    </select>
                </div>
                <div class="col">
                    <label>Lote</label>
                    <input type="text" name="txtLote" maxlength="50" value="<%= Html.escapar(i.getLote()) %>" required>
                </div>
            </div>
            <p class="nota">Até 3 casas decimais (1,5 kg). Mudar a unidade não converte a quantidade: 2 kg trocado para g continua "2 g"; ajuste os dois juntos.</p>

            <div class="linha">
                <div class="col">
                    <label>Validade</label>
                    <input type="date" name="txtValidade" value="<%= i.getValidade() %>" required>
                </div>
                <div class="col">
                    <label>Data da Última Compra</label>
                    <input type="date" name="txtDataUltimaCompra" max="<%= java.time.LocalDate.now() %>" value="<%= i.getDataUltimaCompra() %>" required>
                </div>
            </div>

            <button type="submit" class="btn-salvar">Salvar Alterações</button>
        </form>

        <% } else { %>
            <div class="mensagem erro">Erro: Insumo não carregado.</div>
        <% } %>

        <a href="ControleConfeitaria?acao=ConsultaTodos&entidade=Insumo" class="btn-voltar">⬅ Cancelar e Voltar</a>
    </div>
</body>
</html>
