package command;

import dao.InsumoDAO;
import dao.MovimentacaoDAO;
import dao.ReceitaDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Insumo;
import model.Movimentacao;
import util.Aviso;
import util.Parametro;

public class DeletaInsumoAction implements ICommand {

    @Override
    public String executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int id = Parametro.inteiro(request, "txtId", "ID");
        Insumo insumoBusca = new Insumo();
        insumoBusca.setId(id);

        InsumoDAO dao = new InsumoDAO();
        Insumo insumo = dao.consultarById(insumoBusca);
        if (insumo == null) {
            request.setAttribute("mensagemErro", "O insumo #" + id + " não existe mais. Talvez já tenha sido excluído.");
            return "feedback.jsp";
        }

        // Um insumo que é ingrediente de alguma receita ativa não pode ser excluído.
        ReceitaDAO receitaDAO = new ReceitaDAO();
        int totalReceitas = receitaDAO.contarReceitasComInsumo(insumo);
        if (totalReceitas > 0) {
            request.setAttribute("mensagemErro", "O insumo " + insumo.getNome() + " (#" + id + ") não pode ser excluído: ele é usado em "
                    + totalReceitas + " receita(s). Exclua ou refaça essas receitas antes.");
            return "feedback.jsp";
        }

        dao.moverParaLixeira(insumo);

        MovimentacaoDAO movDAO = new MovimentacaoDAO();
        movDAO.cadastrar(new Movimentacao("Exclusão", "Insumo " + insumo.getNome() + " (" + insumo.getMarca()
                + ", lote " + insumo.getLote() + ", #" + id + ") enviado para a lixeira; tinha quantidade " + insumo.getQuantidadeFormatada()));

        Aviso.guardar(request, "Insumo " + insumo.getNome() + " (#" + id + ") enviado para a lixeira. Para desfazer, use Restaurar na Lixeira (até "
                + ConsultaTodosLixeiraAction.DIAS_NA_LIXEIRA + " dias).");
        return "redirect:ControleConfeitaria?acao=ConsultaTodos&entidade=Insumo";
    }
}
