package command;

import dao.MovimentacaoDAO;
import dao.ReceitaDAO;
import dao.VersaoDAO;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ItemReceita;
import model.Movimentacao;
import model.Receita;
import model.Versao;
import util.Aviso;
import util.Foto;
import util.Parametro;

// Altera o nome e os ingredientes de uma receita que já existe.
public class AtualizaReceitaAction implements ICommand {

    @Override
    public String executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int id = Parametro.inteiro(request, "txtId", "ID");
        String nome = Parametro.texto(request, "txtNome", "Nome da Receita", CadastraReceitaAction.TAMANHO_NOME);

        ReceitaDAO dao = new ReceitaDAO();
        Receita receitaBusca = new Receita();
        receitaBusca.setId(id);
        Receita receitaAntes = dao.consultarById(receitaBusca);
        if (receitaAntes == null) {
            request.setAttribute("mensagemErro", "A receita #" + id + " não existe mais. Talvez tenha sido excluída em outra tela.");
            return "feedback.jsp";
        }
        if (dao.existeNome(nome, id)) {
            request.setAttribute("mensagemErro", "Já existe outra receita chamada " + nome + ". Use outro nome.");
            return "feedback.jsp";
        }

        List<ItemReceita> itens = CadastraReceitaAction.lerIngredientes(request);
        if (itens.isEmpty()) {
            request.setAttribute("mensagemErro", "Informe a quantidade de pelo menos um ingrediente da receita.");
            return "feedback.jsp";
        }

        Receita receita = new Receita();
        receita.setId(id);
        receita.setNome(nome);
        receita.setItens(itens);
        dao.atualizar(receita);

        String descricao = "Receita " + nome + " (#" + id + ") alterada: " + itens.size() + " ingrediente(s)";
        if (!receitaAntes.getNome().equals(nome)) {
            descricao += "; nome antes: " + receitaAntes.getNome();
        }
        // Guarda como estava e como ficou, para o botão Desfazer do Dashboard.
        String fotoAntes = Foto.deReceita(receitaAntes);
        String fotoDepois = Foto.deReceita(receita);
        Integer idVersao = null;
        if (!Foto.iguais(fotoAntes, fotoDepois)) {
            idVersao = new VersaoDAO().cadastrar(new Versao(Versao.RECEITA, id, fotoAntes, fotoDepois));
        }
        MovimentacaoDAO movDAO = new MovimentacaoDAO();
        movDAO.cadastrar(new Movimentacao("Alteração", descricao, idVersao));

        Aviso.guardar(request, "Receita " + nome + " (#" + id + ") modificada com sucesso! Se foi engano, use Desfazer no Dashboard.");
        return "redirect:ControleConfeitaria?acao=ConsultaById&entidade=Receita&txtId=" + id;
    }
}
