package command;

import dao.InsumoDAO;
import dao.MovimentacaoDAO;
import dao.ReceitaDAO;
import dao.VersaoDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Insumo;
import model.ItemReceita;
import model.Movimentacao;
import model.Receita;
import model.Versao;
import util.Aviso;
import util.Foto;
import util.Parametro;

// Botão Desfazer do Dashboard: volta um insumo ou receita para como estava antes de uma alteração.
// É o padrão Command usado como foi pensado: a alteração guardou o necessário (tabela versoes)
// para ser desfeita depois.
public class DesfazVersaoAction implements ICommand {

    // As regras ficam aqui e são usadas também pelo Dashboard, para mostrar o botão só quando dá.
    // Devolve o motivo de não poder desfazer, ou null quando pode.
    public static String motivoParaNaoDesfazer(Versao versao) throws Exception {
        if (versao == null) {
            return "Esta alteração não foi encontrada.";
        }
        if (versao.isDesfeita()) {
            return "Esta alteração já foi desfeita.";
        }
        // Só a última: desfazer uma alteração antiga apagaria, sem aviso, as mais novas.
        if (new VersaoDAO().consultarUltimoId(versao.getEntidade(), versao.getIdRegistro()) != versao.getId()) {
            return "Só é possível desfazer a última alteração de cada registro, e esta já tem alterações mais novas.";
        }
        String fotoAtual = fotoAtual(versao);
        if (fotoAtual == null) {
            return "O registro não está mais ativo (está na lixeira ou foi apagado). Restaure-o antes de desfazer.";
        }
        // O registro mudou depois da alteração (por exemplo, pela produção de uma receita):
        // voltar a foto antiga apagaria essa mudança.
        if (!Foto.iguais(fotoAtual, versao.getDadosDepois())) {
            return "O registro mudou depois desta alteração (em outra tela ou pela produção de uma receita). "
                    + "Para não perder essa mudança, edite-o direto.";
        }
        return null;
    }

    private static String fotoAtual(Versao versao) throws Exception {
        if (Versao.INSUMO.equals(versao.getEntidade())) {
            Insumo busca = new Insumo();
            busca.setId(versao.getIdRegistro());
            Insumo atual = new InsumoDAO().consultarById(busca);
            return atual == null ? null : Foto.deInsumo(atual);
        }
        Receita busca = new Receita();
        busca.setId(versao.getIdRegistro());
        Receita atual = new ReceitaDAO().consultarById(busca);
        return atual == null ? null : Foto.deReceita(atual);
    }

    @Override
    public String executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int id = Parametro.inteiro(request, "txtId", "ID");
        VersaoDAO versaoDAO = new VersaoDAO();
        Versao versao = versaoDAO.consultarById(id);

        String motivo = motivoParaNaoDesfazer(versao);
        if (motivo != null) {
            request.setAttribute("mensagemErro", "Não dá para desfazer: " + motivo);
            return "feedback.jsp";
        }

        String descricao = Versao.INSUMO.equals(versao.getEntidade())
                ? desfazerInsumo(versao, request)
                : desfazerReceita(versao, request);
        if (descricao == null) {
            return "feedback.jsp";   // a mensagem de erro já foi colocada no request
        }

        versaoDAO.marcarDesfeita(versao);
        new MovimentacaoDAO().cadastrar(new Movimentacao("Desfazer", descricao));

        Aviso.guardar(request, descricao + ".");
        return "redirect:ControleConfeitaria?acao=ConsultaTodos&entidade=Movimentacao";
    }

    private String desfazerInsumo(Versao versao, HttpServletRequest request) throws Exception {
        InsumoDAO dao = new InsumoDAO();
        Insumo atual = Foto.paraInsumo(versao.getDadosDepois());
        Insumo antes = Foto.paraInsumo(versao.getDadosAntes());
        antes.setId(versao.getIdRegistro());

        // Voltar o nome, a marca ou o lote não pode repetir outro insumo ativo.
        if (dao.existeIgual(antes)) {
            request.setAttribute("mensagemErro", "Não dá para desfazer: voltar para " + antes.getNome() + " da marca " + antes.getMarca()
                    + " no lote " + antes.getLote() + " repetiria outro insumo que já existe.");
            return null;
        }
        dao.atualizar(antes);

        String descricao = "Alteração do insumo " + antes.getNome() + " (#" + antes.getId() + ") desfeita";
        if (!atual.getQuantidadeFormatada().equals(antes.getQuantidadeFormatada())) {
            descricao += ": quantidade voltou de " + atual.getQuantidadeFormatada() + " para " + antes.getQuantidadeFormatada();
        }
        return descricao;
    }

    private String desfazerReceita(Versao versao, HttpServletRequest request) throws Exception {
        ReceitaDAO dao = new ReceitaDAO();
        Receita antes = Foto.paraReceita(versao.getDadosAntes());
        antes.setId(versao.getIdRegistro());

        if (dao.existeNome(antes.getNome(), antes.getId())) {
            request.setAttribute("mensagemErro", "Não dá para desfazer: voltar ao nome " + antes.getNome()
                    + " repetiria outra receita que já existe.");
            return null;
        }
        // A versão antiga pode usar um insumo que agora está na lixeira.
        InsumoDAO insumoDAO = new InsumoDAO();
        for (ItemReceita item : antes.getItens()) {
            Insumo busca = new Insumo();
            busca.setId(item.getIdInsumo());
            if (insumoDAO.consultarById(busca) == null) {
                request.setAttribute("mensagemErro", "Não dá para desfazer: a versão anterior da receita usa um insumo (#"
                        + item.getIdInsumo() + ") que está na lixeira. Restaure-o primeiro.");
                return null;
            }
        }
        dao.atualizar(antes);
        return "Alteração da receita " + antes.getNome() + " (#" + antes.getId() + ") desfeita: voltou a ter "
                + antes.getItens().size() + " ingrediente(s)";
    }
}
