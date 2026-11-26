package command;

import dao.InsumoDAO;
import dao.MovimentacaoDAO;
import dao.VersaoDAO;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Insumo;
import model.Movimentacao;
import model.Versao;

public class ConsultaTodosMovimentacaoAction implements ICommand {

    // Com quantos dias de antecedência o insumo aparece em "vencem em breve".
    private static final int DIAS_PARA_VENCER = 7;

    @Override
    public String executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        MovimentacaoDAO dao = new MovimentacaoDAO();
        List<Movimentacao> listaMovimentacoes = dao.consultarRecentes();

        // Botão Desfazer: só nas alterações que ainda podem ser desfeitas (regras em DesfazVersaoAction).
        VersaoDAO versaoDAO = new VersaoDAO();
        for (Movimentacao m : listaMovimentacoes) {
            if (m.getIdVersao() != null) {
                Versao versao = versaoDAO.consultarById(m.getIdVersao());
                m.setDesfazivel(DesfazVersaoAction.motivoParaNaoDesfazer(versao) == null);
            }
        }

        LocalDate hoje = LocalDate.now();
        Date dataHoje = Date.valueOf(hoje);
        Date dataLimite = Date.valueOf(hoje.plusDays(DIAS_PARA_VENCER));

        InsumoDAO insumoDAO = new InsumoDAO();
        List<Insumo> listaVencidos = insumoDAO.consultarVencidos(dataHoje);
        List<Insumo> listaVencendo = insumoDAO.consultarVencendo(dataHoje, dataLimite);

        request.setAttribute("resultadoConsulta", listaMovimentacoes);
        request.setAttribute("vencidos", listaVencidos);
        request.setAttribute("vencendo", listaVencendo);
        request.setAttribute("diasParaVencer", DIAS_PARA_VENCER);
        request.setAttribute("limiteHistorico", MovimentacaoDAO.LIMITE_HISTORICO);
        return "dashboard.jsp";
    }
}
