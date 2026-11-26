package command;

import dao.InsumoDAO;
import dao.ReceitaDAO;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Insumo;
import model.Receita;
import util.Parametro;

// Abre o formulário de receita já com a lista de insumos para escolher os ingredientes.
// Sem txtId: receita nova. Com txtId: edição, com o nome e as quantidades já preenchidos.
public class PreparaReceitaAction implements ICommand {

    @Override
    public String executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        InsumoDAO dao = new InsumoDAO();
        List<Insumo> listaInsumos = dao.consultarTodos();
        request.setAttribute("resultadoConsulta", listaInsumos);

        String txtId = request.getParameter("txtId");
        if (txtId != null && !txtId.trim().isEmpty()) {
            int id = Parametro.inteiro(request, "txtId", "ID");
            Receita receitaBusca = new Receita();
            receitaBusca.setId(id);
            Receita receita = new ReceitaDAO().consultarById(receitaBusca);
            if (receita == null) {
                request.setAttribute("mensagemErro", "A receita #" + id + " não foi encontrada.");
                return "feedback.jsp";
            }
            request.setAttribute("receita", receita);
        }
        return "formCadastrarReceita.jsp";
    }
}
