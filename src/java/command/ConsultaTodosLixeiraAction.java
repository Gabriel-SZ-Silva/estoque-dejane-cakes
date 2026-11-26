package command;

import dao.InsumoDAO;
import dao.ReceitaDAO;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Tela da Lixeira: insumos e receitas excluídos, com o botão Restaurar.
// Ao abrir, apaga de vez o que está na lixeira há mais de DIAS_NA_LIXEIRA dias.
public class ConsultaTodosLixeiraAction implements ICommand {

    public static final int DIAS_NA_LIXEIRA = 30;

    @Override
    public String executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        InsumoDAO insumoDAO = new InsumoDAO();
        ReceitaDAO receitaDAO = new ReceitaDAO();

        // Primeiro as receitas: um insumo só pode ser apagado de vez quando nenhuma receita o usa mais.
        Timestamp limite = Timestamp.valueOf(LocalDateTime.now().minusDays(DIAS_NA_LIXEIRA));
        receitaDAO.apagarExcluidasAntesDe(limite);
        insumoDAO.apagarExcluidosAntesDe(limite);

        request.setAttribute("insumosExcluidos", insumoDAO.consultarExcluidos());
        request.setAttribute("receitasExcluidas", receitaDAO.consultarExcluidas());
        request.setAttribute("diasNaLixeira", DIAS_NA_LIXEIRA);
        return "lixeira.jsp";
    }
}
