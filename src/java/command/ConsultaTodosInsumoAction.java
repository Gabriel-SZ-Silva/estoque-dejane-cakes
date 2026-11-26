package command;

import dao.InsumoDAO;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Insumo;

public class ConsultaTodosInsumoAction implements ICommand {

    @Override
    public String executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        InsumoDAO dao = new InsumoDAO();
        List<Insumo> listaInsumos = dao.consultarTodos();

        request.setAttribute("resultadoConsulta", listaInsumos);
        return "consultaInsumos.jsp";
    }
}
