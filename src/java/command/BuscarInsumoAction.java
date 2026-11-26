package command;

import dao.InsumoDAO;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Insumo;

public class BuscarInsumoAction implements ICommand {

    @Override
    public String executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String busca = request.getParameter("txtBusca");
        if (busca == null || busca.trim().isEmpty()) {
            return "ControleConfeitaria?acao=ConsultaTodos&entidade=Insumo";
        }
        busca = busca.trim();

        InsumoDAO dao = new InsumoDAO();
        List<Insumo> resultado;

        // Só números: busca pelo ID. Qualquer outro texto: busca pelo nome.
        if (busca.matches("\\d{1,9}")) {
            int id = Integer.parseInt(busca);
            Insumo insumoBusca = new Insumo();
            insumoBusca.setId(id);

            resultado = new ArrayList<>();
            Insumo insumoEncontrado = dao.consultarById(insumoBusca);
            if (insumoEncontrado != null) {
                resultado.add(insumoEncontrado);
                request.setAttribute("mensagem", "Resultado da busca pelo Insumo #" + id);
            } else {
                request.setAttribute("mensagem", "Nenhum insumo encontrado com o ID " + id);
            }
        } else {
            resultado = dao.consultarPorNome(busca);
            if (resultado.isEmpty()) {
                request.setAttribute("mensagem", "Nenhum insumo encontrado com o nome \"" + busca + "\"");
            } else {
                request.setAttribute("mensagem", resultado.size() + " insumo(s) encontrado(s) com o nome \"" + busca + "\"");
            }
        }

        request.setAttribute("resultadoConsulta", resultado);
        request.setAttribute("busca", busca);
        return "consultaInsumos.jsp";
    }
}
