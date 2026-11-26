package command;

import dao.InsumoDAO;
import dao.MovimentacaoDAO;
import dao.ReceitaDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Insumo;
import model.ItemReceita;
import model.Movimentacao;
import model.Receita;
import util.Aviso;
import util.Parametro;

// Tira uma receita da lixeira, com os ingredientes que ela tinha.
public class RestauraReceitaAction implements ICommand {

    @Override
    public String executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int id = Parametro.inteiro(request, "txtId", "ID");
        Receita receitaBusca = new Receita();
        receitaBusca.setId(id);

        ReceitaDAO dao = new ReceitaDAO();
        Receita receita = dao.consultarExcluidaById(receitaBusca);
        if (receita == null) {
            request.setAttribute("mensagemErro", "A receita #" + id + " não está na lixeira. Talvez já tenha sido restaurada ou apagada de vez.");
            return "feedback.jsp";
        }

        if (dao.existeNome(receita.getNome(), id)) {
            request.setAttribute("mensagemErro", "Não dá para restaurar: já existe outra receita chamada " + receita.getNome()
                    + ". Renomeie a que está ativa antes de restaurar.");
            return "feedback.jsp";
        }

        // Todos os ingredientes precisam estar ativos: uma receita não pode usar um insumo da lixeira.
        InsumoDAO insumoDAO = new InsumoDAO();
        StringBuilder naLixeira = new StringBuilder();
        for (ItemReceita item : receita.getItens()) {
            Insumo insumoBusca = new Insumo();
            insumoBusca.setId(item.getIdInsumo());
            if (insumoDAO.consultarById(insumoBusca) == null) {
                if (naLixeira.length() > 0) {
                    naLixeira.append(", ");
                }
                naLixeira.append(item.getNomeInsumo());
            }
        }
        if (naLixeira.length() > 0) {
            request.setAttribute("mensagemErro", "Não dá para restaurar a receita " + receita.getNome()
                    + ": estes ingredientes estão na lixeira: " + naLixeira + ". Restaure-os primeiro.");
            return "feedback.jsp";
        }

        dao.restaurar(receita);

        MovimentacaoDAO movDAO = new MovimentacaoDAO();
        movDAO.cadastrar(new Movimentacao("Restauração", "Receita " + receita.getNome() + " (#" + id + ") restaurada da lixeira com "
                + receita.getItens().size() + " ingrediente(s)"));

        Aviso.guardar(request, "Receita " + receita.getNome() + " (#" + id + ") restaurada!");
        return "redirect:ControleConfeitaria?acao=ConsultaTodos&entidade=Lixeira";
    }
}
