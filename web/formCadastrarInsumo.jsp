<%@page import="java.util.Map"%>
<%@page import="util.Unidade"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Cadastrar Insumo</title>
    <link rel="stylesheet" href="css/estilo.css">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="theme-color" content="#aa0000">
    <link rel="manifest" href="manifest.json">
    <link rel="icon" href="img/icone-192.png">
    <link rel="apple-touch-icon" href="img/icone-192.png">
</head>
<body>
    <div class="container estreito">
        <h2><img src="img/logo.jpg" class="logo-pequena" alt="">📝 Cadastrar Novo Insumo</h2>

        <form action="ControleConfeitaria" method="POST">
            <input type="hidden" name="acao" value="Cadastra">
            <input type="hidden" name="entidade" value="Insumo">

            <div class="linha">
                <div class="col">
                    <label>Nome do Insumo</label>
                    <input type="text" name="txtNome" maxlength="100" required placeholder="Ex: Farinha de trigo" autofocus>
                </div>
                <div class="col">
                    <label>Marca</label>
                    <input type="text" name="txtMarca" maxlength="100" required placeholder="Ex: Marca Exemplo">
                </div>
            </div>

            <div class="sessao-tipo">
                <label>📦 Tipo do Insumo (Padrão Decorator)</label>
                <div class="radio-group">
                    <label class="radio-item"><input type="radio" name="radTipo" value="Ingrediente" checked> Ingrediente</label>
                    <label class="radio-item"><input type="radio" name="radTipo" value="Embalagem"> Embalagem</label>
                </div>
            </div>

            <% String unidadeAtual = Unidade.PADRAO; %>
            <div class="linha">
                <div class="col">
                    <label>Quantidade em Estoque</label>
                    <input type="number" name="txtQuantidade" required min="0" step="0.001" placeholder="Ex: 10 ou 1,5">
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
                    <input type="text" name="txtLote" maxlength="50" required placeholder="Ex: L001">
                </div>
            </div>
            <p class="nota">A quantidade aceita até 3 casas decimais: 1,5 kg; 0,25 L; 2,5 un.</p>

            <div class="linha">
                <div class="col">
                    <label>Validade</label>
                    <input type="date" name="txtValidade" required>
                </div>
                <div class="col">
                    <label>Data da Última Compra</label>
                    <input type="date" name="txtDataUltimaCompra" max="<%= java.time.LocalDate.now() %>" required>
                </div>
            </div>

            <button type="submit" class="btn-salvar">Cadastrar Insumo</button>
        </form>
        <a href="index.html" class="btn-voltar">⬅ Voltar ao Menu</a>
    </div>
</body>
</html>
