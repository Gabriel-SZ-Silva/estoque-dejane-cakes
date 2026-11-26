package command;

import dao.UsuarioDAO;
import filter.AutenticacaoFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Usuario;
import util.Senha;

public class EntraUsuarioAction implements ICommand {

    // Quanto tempo a sessão dura sem uso (a Dejane não precisa entrar de novo a cada pedido).
    private static final int HORAS_SESSAO = 12;

    // Hash de uma senha qualquer, conferido quando o usuário não existe. Assim a resposta demora
    // o mesmo tanto nos dois casos, e o tempo não revela se o nome de usuário está certo.
    private static final String HASH_FALSO = Senha.gerarHash("senha-que-nao-vale");

    @Override
    public String executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String login = request.getParameter("txtLogin");
        String senha = request.getParameter("txtSenha");

        UsuarioDAO dao = new UsuarioDAO();
        Usuario usuario = (login == null) ? null : dao.consultarPorLogin(login.trim());

        String hashSalvo = (usuario == null) ? HASH_FALSO : usuario.getSenhaHash();
        boolean senhaCerta = Senha.conferir(senha, hashSalvo);
        if (usuario == null || !senhaCerta) {
            request.setAttribute("mensagemErro", "Usuário ou senha incorretos.");
            return "login.jsp";
        }

        // Troca a sessão ao entrar: um identificador de sessão antigo não serve para outra pessoa.
        HttpSession antiga = request.getSession(false);
        if (antiga != null) {
            antiga.invalidate();
        }
        HttpSession sessao = request.getSession(true);
        sessao.setAttribute(AutenticacaoFilter.USUARIO_LOGADO, usuario.getLogin());
        sessao.setAttribute(AutenticacaoFilter.PAPEL_USUARIO, usuario.getPapel());
        sessao.setMaxInactiveInterval(HORAS_SESSAO * 60 * 60);

        return "redirect:index.html";
    }
}
