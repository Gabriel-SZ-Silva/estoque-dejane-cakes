package command;

import dao.MovimentacaoDAO;
import dao.ReceitaDAO;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import jakarta.servlet.http.HttpServletResponse;
import model.ItemReceita;
import model.Movimentacao;
import model.Receita;
import util.Aviso;
import util.Parametro;
import util.Unidade;

// "Produzi esta receita": desconta do estoque a quantidade de cada ingrediente.
// Pode ser mais de uma vez (txtVezes), para quem faz dois ou três bolos de uma vez.
public class ProduzReceitaAction implements ICommand {

    public static final int VEZES_MAXIMO = 1000;

    @Override
    public String executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int id = Parametro.inteiro(request, "txtId", "ID");
        int vezes = Parametro.inteiro(request, "txtVezes", "Quantas vezes");
        if (vezes < 1 || vezes > VEZES_MAXIMO) {
            throw new IllegalArgumentException("O campo Quantas vezes precisa ser de 1 a " + VEZES_MAXIMO + ".");
        }

        ReceitaDAO dao = new ReceitaDAO();
        Receita receitaBusca = new Receita();
        receitaBusca.setId(id);
        Receita receita = dao.consultarById(receitaBusca);
        if (receita == null) {
            request.setAttribute("mensagemErro", "A receita #" + id + " não existe mais.");
            return "feedback.jsp";
        }

        // Primeiro confere tudo: se faltar qualquer ingrediente, nada é descontado.
        StringBuilder faltando = new StringBuilder();
        for (ItemReceita item : receita.getItens()) {
            BigDecimal usado = item.getQuantidade().multiply(BigDecimal.valueOf(vezes));
            if (item.getQuantidadeEstoque().compareTo(usado) < 0) {
                if (faltando.length() > 0) {
                    faltando.append("; ");
                }
                faltando.append(item.getNomeInsumo()).append(": precisa de ")
                        .append(Unidade.formatar(usado, item.getUnidade())).append(", tem ")
                        .append(item.getQuantidadeEstoqueFormatada());
            }
        }
        if (faltando.length() > 0) {
            request.setAttribute("mensagemErro", "Não dá para produzir " + receita.getNome() + " " + vezes
                    + " vez(es). Está em falta: " + faltando + ". Reponha o estoque e tente de novo.");
            return "feedback.jsp";
        }

        if (!dao.produzir(receita, vezes)) {
            request.setAttribute("mensagemErro", "O estoque mudou enquanto a tela estava aberta e um ingrediente ficou em falta. Nada foi descontado; abra a receita de novo e confira.");
            return "feedback.jsp";
        }

        // Histórico: o que foi descontado de cada ingrediente.
        StringBuilder descricao = new StringBuilder("Receita " + receita.getNome() + " produzida " + vezes + " vez(es): ");
        boolean primeiro = true;
        for (ItemReceita item : receita.getItens()) {
            BigDecimal usado = item.getQuantidade().multiply(BigDecimal.valueOf(vezes));
            if (!primeiro) {
                descricao.append("; ");
            }
            descricao.append(item.getNomeInsumo()).append(" de ").append(item.getQuantidadeEstoqueFormatada())
                    .append(" para ").append(Unidade.formatar(item.getQuantidadeEstoque().subtract(usado), item.getUnidade()));
            primeiro = false;
        }
        MovimentacaoDAO movDAO = new MovimentacaoDAO();
        movDAO.cadastrar(new Movimentacao("Produção", descricao.toString()));

        Aviso.guardar(request, "Estoque atualizado: " + receita.getNome() + " produzida " + vezes + " vez(es). Veja no Dashboard o que foi descontado.");
        return "redirect:ControleConfeitaria?acao=ConsultaById&entidade=Receita&txtId=" + id;
    }
}
