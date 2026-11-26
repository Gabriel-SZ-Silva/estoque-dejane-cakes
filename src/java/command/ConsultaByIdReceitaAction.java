package command;

import dao.ReceitaDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Receita;
import util.Parametro;

// Mostra a receita com a quantidade de cada ingrediente e se ele está em falta no estoque.
public class ConsultaByIdReceitaAction implements ICommand {

    @Override
    public String executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int id = Parametro.inteiro(request, "txtId", "ID");
        Receita receitaBusca = new Receita();
        receitaBusca.setId(id);
        ReceitaDAO dao = new ReceitaDAO();
        Receita receitaEncontrada = dao.consultarById(receitaBusca);
        if (receitaEncontrada == null) {
            request.setAttribute("mensagemErro", "A receita #" + id + " não foi encontrada.");
            return "feedback.jsp";
        }
        request.setAttribute("receita", receitaEncontrada);
        return "detalheReceita.jsp";
    }
}
