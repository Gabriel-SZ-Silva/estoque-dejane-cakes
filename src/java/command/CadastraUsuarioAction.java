package command;

import dao.UsuarioDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import model.Usuario;
import util.Aviso;
import util.Senha;

// Cria o acesso da dona do negócio (papel DONA). Só funciona enquanto essa conta não existe:
// os administradores e visitantes criados no servidor (deploy/usuarios.sh) não fecham esta tela.
public class CadastraUsuarioAction implements ICommand {

    private static final int TAMANHO_MINIMO_SENHA = 8;
    private static final int TAMANHO_MAXIMO_LOGIN = 50; // igual à coluna login do banco

    @Override
    public String executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String login = request.getParameter("txtLogin");
        String senha = request.getParameter("txtSenha");
        String confirmacao = request.getParameter("txtConfirmacao");
        String codigo = request.getParameter("txtCodigo");

        UsuarioDAO dao = new UsuarioDAO();
        if (dao.contarPorPapel(Usuario.DONA) > 0) {
            request.setAttribute("mensagemErro", "O acesso já foi criado. Entre com o usuário e a senha cadastrados.");
            return "login.jsp";
        }
        if (!codigoInstalacaoValido(request, codigo)) {
            request.setAttribute("mensagemErro", "Código de instalação incorreto.");
            return "primeiroAcesso.jsp";
        }
        if (login == null || login.trim().isEmpty()) {
            request.setAttribute("mensagemErro", "Informe o nome de usuário.");
            return "primeiroAcesso.jsp";
        }
        if (login.trim().length() > TAMANHO_MAXIMO_LOGIN) {
            request.setAttribute("mensagemErro", "O nome de usuário pode ter no máximo " + TAMANHO_MAXIMO_LOGIN + " caracteres.");
            return "primeiroAcesso.jsp";
        }
        if (senha == null || senha.length() < TAMANHO_MINIMO_SENHA) {
            request.setAttribute("mensagemErro", "A senha precisa ter pelo menos " + TAMANHO_MINIMO_SENHA + " caracteres.");
            return "primeiroAcesso.jsp";
        }
        if (!senha.equals(confirmacao)) {
            request.setAttribute("mensagemErro", "A confirmação não é igual à senha.");
            return "primeiroAcesso.jsp";
        }

        // Um administrador ou visitante pode já usar esse nome.
        if (dao.consultarPorLogin(login.trim()) != null) {
            request.setAttribute("mensagemErro", "Este nome de usuário já existe. Escolha outro.");
            return "primeiroAcesso.jsp";
        }

        Usuario usuario = new Usuario();
        usuario.setLogin(login.trim());
        usuario.setSenhaHash(Senha.gerarHash(senha));
        usuario.setPapel(Usuario.DONA);
        dao.cadastrar(usuario);

        Aviso.guardar(request, "Acesso criado! Agora entre com o usuário e a senha.");
        return "redirect:login.jsp";
    }

    // No servidor, exige o código definido na variável CODIGO_INSTALACAO (arquivo .env).
    // Sem essa variável (rodando pelo NetBeans), só aceita quem está no próprio computador.
    private boolean codigoInstalacaoValido(HttpServletRequest request, String codigo) {
        String esperado = System.getenv("CODIGO_INSTALACAO");
        if (esperado != null && !esperado.isEmpty()) {
            return codigo != null && MessageDigest.isEqual(
                    esperado.getBytes(StandardCharsets.UTF_8), codigo.trim().getBytes(StandardCharsets.UTF_8));
        }
        String origem = request.getRemoteAddr();
        return "127.0.0.1".equals(origem) || "0:0:0:0:0:0:0:1".equals(origem) || "::1".equals(origem);
    }
}
