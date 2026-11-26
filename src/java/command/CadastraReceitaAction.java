package command;

import dao.InsumoDAO;
import dao.MovimentacaoDAO;
import dao.ReceitaDAO;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Insumo;
import model.ItemReceita;
import model.Movimentacao;
import model.Receita;
import util.Aviso;
import util.Parametro;

public class CadastraReceitaAction implements ICommand {

    public static final int TAMANHO_NOME = 100; // igual à coluna nome da tabela receitas

    @Override
    public String executar(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String nome = Parametro.texto(request, "txtNome", "Nome da Receita", TAMANHO_NOME);

        ReceitaDAO receitaDAO = new ReceitaDAO();
        if (receitaDAO.existeNome(nome, null)) {
            request.setAttribute("mensagemErro", "Já existe uma receita chamada " + nome + ". Use outro nome ou exclua a receita antiga.");
            return "feedback.jsp";
        }

        List<ItemReceita> itens = lerIngredientes(request);
        if (itens.isEmpty()) {
            request.setAttribute("mensagemErro", "Informe a quantidade de pelo menos um ingrediente da receita.");
            return "feedback.jsp";
        }

        Receita receita = new Receita();
        receita.setNome(nome);
        receita.setItens(itens);
        int idReceitaGerado = receitaDAO.cadastrar(receita);

        MovimentacaoDAO movDAO = new MovimentacaoDAO();
        movDAO.cadastrar(new Movimentacao("Cadastro", "Receita " + nome + " cadastrada com " + itens.size() + " ingrediente(s)"));

        Aviso.guardar(request, "Receita cadastrada com sucesso! " + nome);
        return "redirect:ControleConfeitaria?acao=ConsultaById&entidade=Receita&txtId=" + idReceitaGerado;
    }

    // O formulário tem um campo de quantidade para cada insumo: txtQuantidade + ID do insumo.
    // Só entram na receita os insumos com quantidade maior que zero. Usado também pela alteração.
    public static List<ItemReceita> lerIngredientes(HttpServletRequest request) throws Exception {
        InsumoDAO insumoDAO = new InsumoDAO();
        List<ItemReceita> itens = new ArrayList<>();
        for (Insumo insumo : insumoDAO.consultarTodos()) {
            String campo = "txtQuantidade" + insumo.getId();
            String quantidade = request.getParameter(campo);
            if (quantidade == null || quantidade.trim().isEmpty()) {
                continue;
            }
            BigDecimal valor = Parametro.decimalNaoNegativo(request, campo, "Quantidade de " + insumo.getNome());
            if (valor.signum() > 0) {
                ItemReceita item = new ItemReceita();
                item.setIdInsumo(insumo.getId());
                item.setQuantidade(valor);
                itens.add(item);
            }
        }
        return itens;
    }
}
