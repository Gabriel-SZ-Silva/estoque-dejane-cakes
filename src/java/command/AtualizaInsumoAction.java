package command;

import dao.InsumoDAO;
import dao.MovimentacaoDAO;
import dao.VersaoDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Insumo;
import model.Movimentacao;
import model.Versao;
import util.Aviso;
import util.Foto;
import util.Parametro;
import util.ValidacaoInsumo;

public class AtualizaInsumoAction implements ICommand {

    @Override
    public String executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int id = Parametro.inteiro(request, "txtId", "ID");
        Insumo insumoAtualizado = ValidacaoInsumo.lerFormulario(request);
        insumoAtualizado.setId(id);

        InsumoDAO dao = new InsumoDAO();
        Insumo insumoAntes = dao.consultarById(insumoAtualizado);
        if (insumoAntes == null) {
            request.setAttribute("mensagemErro", "O insumo #" + id + " não existe mais. Talvez tenha sido excluído em outra tela.");
            return "feedback.jsp";
        }
        String erro = ValidacaoInsumo.validar(insumoAtualizado, dao);
        if (erro != null) {
            request.setAttribute("mensagemErro", erro);
            return "feedback.jsp";
        }
        dao.atualizar(insumoAtualizado);

        // O histórico registra a mudança de quantidade, que é o que mais importa para o estoque.
        String descricao = "Insumo " + insumoAtualizado.getNome() + " (#" + id + ") alterado";
        if (!insumoAntes.getQuantidadeFormatada().equals(insumoAtualizado.getQuantidadeFormatada())) {
            descricao += ": quantidade de " + insumoAntes.getQuantidadeFormatada() + " para " + insumoAtualizado.getQuantidadeFormatada();
        }
        // Guarda como estava e como ficou, para o botão Desfazer do Dashboard.
        // Se nada mudou (salvou sem alterar), não há o que desfazer.
        String fotoAntes = Foto.deInsumo(insumoAntes);
        String fotoDepois = Foto.deInsumo(insumoAtualizado);
        Integer idVersao = null;
        if (!Foto.iguais(fotoAntes, fotoDepois)) {
            idVersao = new VersaoDAO().cadastrar(new Versao(Versao.INSUMO, id, fotoAntes, fotoDepois));
        }
        MovimentacaoDAO movDAO = new MovimentacaoDAO();
        movDAO.cadastrar(new Movimentacao("Alteração", descricao, idVersao));

        Aviso.guardar(request, "Insumo " + insumoAtualizado.getNome() + " (#" + id + ") modificado com sucesso! Se foi engano, use Desfazer no Dashboard.");
        return "redirect:ControleConfeitaria?acao=ConsultaTodos&entidade=Insumo";
    }
}
