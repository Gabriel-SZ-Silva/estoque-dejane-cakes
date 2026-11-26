package command;

import dao.MovimentacaoDAO;
import dao.ReceitaDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Movimentacao;
import model.Receita;
import util.Aviso;
import util.Parametro;

public class DeletaReceitaAction implements ICommand {

    @Override
    public String executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int id = Parametro.inteiro(request, "txtId", "ID");
        Receita receitaBusca = new Receita();
        receitaBusca.setId(id);

        ReceitaDAO dao = new ReceitaDAO();
        Receita receita = dao.consultarById(receitaBusca);
        if (receita == null) {
            request.setAttribute("mensagemErro", "A receita #" + id + " não existe mais. Talvez já tenha sido excluída.");
            return "feedback.jsp";
        }
        dao.moverParaLixeira(receita);

        MovimentacaoDAO movDAO = new MovimentacaoDAO();
        movDAO.cadastrar(new Movimentacao("Exclusão", "Receita " + receita.getNome() + " (#" + id + ") enviada para a lixeira"));

        Aviso.guardar(request, "Receita " + receita.getNome() + " (#" + id + ") enviada para a lixeira. Para desfazer, use Restaurar na Lixeira (até "
                + ConsultaTodosLixeiraAction.DIAS_NA_LIXEIRA + " dias).");
        return "redirect:ControleConfeitaria?acao=ConsultaTodos&entidade=Receita";
    }
}
