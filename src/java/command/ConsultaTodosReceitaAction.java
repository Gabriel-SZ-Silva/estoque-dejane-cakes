package command;

import dao.ReceitaDAO;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Receita;

public class ConsultaTodosReceitaAction implements ICommand {

    @Override
    public String executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ReceitaDAO dao = new ReceitaDAO();
        List<Receita> listaReceitas = dao.consultarTodos();

        request.setAttribute("resultadoConsulta", listaReceitas);
        return "consultaReceitas.jsp";
    }
}
