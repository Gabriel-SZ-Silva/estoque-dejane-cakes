package command;

import dao.InsumoDAO;
import dao.MovimentacaoDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Insumo;
import model.Movimentacao;
import util.Aviso;
import util.ValidacaoInsumo;

public class CadastraInsumoAction implements ICommand {

    @Override
    public String executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Insumo insumo = ValidacaoInsumo.lerFormulario(request);

        InsumoDAO insumoDAO = new InsumoDAO();
        String erro = ValidacaoInsumo.validar(insumo, insumoDAO);
        if (erro != null) {
            request.setAttribute("mensagemErro", erro);
            return "feedback.jsp";
        }
        insumoDAO.cadastrar(insumo);

        MovimentacaoDAO movDAO = new MovimentacaoDAO();
        movDAO.cadastrar(new Movimentacao("Cadastro", "Insumo " + insumo.getNome() + " (" + insumo.getMarca()
                + ", lote " + insumo.getLote() + ") cadastrado com quantidade " + insumo.getQuantidadeFormatada()));

        Aviso.guardar(request, "Insumo cadastrado com sucesso! " + insumo.getNome() + " - " + insumo.getTipo());
        return "redirect:ControleConfeitaria?acao=ConsultaTodos&entidade=Insumo";
    }
}
