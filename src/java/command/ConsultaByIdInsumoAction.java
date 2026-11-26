package command;

import dao.InsumoDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Insumo;
import util.Parametro;

public class ConsultaByIdInsumoAction implements ICommand {

    @Override
    public String executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int id = Parametro.inteiro(request, "txtId", "ID");
        Insumo insumoBusca = new Insumo();
        insumoBusca.setId(id);
        InsumoDAO dao = new InsumoDAO();
        Insumo insumoEncontrado = dao.consultarById(insumoBusca);
        if (insumoEncontrado == null) {
            request.setAttribute("mensagemErro", "O insumo #" + id + " não foi encontrado.");
            return "feedback.jsp";
        }
        request.setAttribute("insumo", insumoEncontrado);
        return "formEditarInsumo.jsp";
    }
}
