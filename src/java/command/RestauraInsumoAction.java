package command;

import dao.InsumoDAO;
import dao.MovimentacaoDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Insumo;
import model.Movimentacao;
import util.Aviso;
import util.Parametro;

// Tira um insumo da lixeira e o devolve à lista, exatamente como estava.
public class RestauraInsumoAction implements ICommand {

    @Override
    public String executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int id = Parametro.inteiro(request, "txtId", "ID");
        Insumo insumoBusca = new Insumo();
        insumoBusca.setId(id);

        InsumoDAO dao = new InsumoDAO();
        Insumo insumo = dao.consultarExcluidoById(insumoBusca);
        if (insumo == null) {
            request.setAttribute("mensagemErro", "O insumo #" + id + " não está na lixeira. Talvez já tenha sido restaurado ou apagado de vez.");
            return "feedback.jsp";
        }

        // Enquanto estava na lixeira, pode ter sido cadastrado outro igual: dois ativos iguais não pode.
        if (dao.existeIgual(insumo)) {
            request.setAttribute("mensagemErro", "Não dá para restaurar: já existe outro insumo " + insumo.getNome() + " da marca "
                    + insumo.getMarca() + " no lote " + insumo.getLote() + ". Edite o que está ativo, ou mude o nome, a marca ou o lote dele antes de restaurar.");
            return "feedback.jsp";
        }

        dao.restaurar(insumo);

        MovimentacaoDAO movDAO = new MovimentacaoDAO();
        movDAO.cadastrar(new Movimentacao("Restauração", "Insumo " + insumo.getNome() + " (" + insumo.getMarca()
                + ", lote " + insumo.getLote() + ", #" + id + ") restaurado da lixeira com quantidade " + insumo.getQuantidadeFormatada()));

        Aviso.guardar(request, "Insumo " + insumo.getNome() + " (#" + id + ") restaurado!");
        return "redirect:ControleConfeitaria?acao=ConsultaTodos&entidade=Lixeira";
    }
}
