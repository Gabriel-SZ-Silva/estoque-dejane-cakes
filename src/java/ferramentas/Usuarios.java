package ferramentas;

import dao.UsuarioDAO;
import java.io.Console;
import java.util.Arrays;
import java.util.List;
import model.Usuario;
import util.Senha;

// Gerencia as contas de administrador e de visitante pelo terminal do servidor.
// Não tem tela no site de propósito: só quem acessa o servidor cria ou remove contas.
//
// Uso no servidor (dentro da pasta SistemaConfeitaria):
//   bash deploy/usuarios.sh listar
//   bash deploy/usuarios.sh criar <login> admin|visitante
//   bash deploy/usuarios.sh senha <login>
//   bash deploy/usuarios.sh remover <login>
public class Usuarios {

    static final int TAMANHO_MINIMO_SENHA = 8;
    static final int TAMANHO_MAXIMO_LOGIN = 50; // igual à coluna login do banco

    public static void main(String[] args) {
        try {
            String comando = args.length > 0 ? args[0] : "";
            switch (comando) {
                case "listar" -> System.out.print(listar());
                case "criar" -> {
                    exigir(args, 3, "criar <login> admin|visitante");
                    System.out.println(criar(args[1], args[2], lerSenhaDuasVezes()));
                }
                case "senha" -> {
                    exigir(args, 2, "senha <login>");
                    System.out.println(trocarSenha(args[1], lerSenhaDuasVezes()));
                }
                case "remover" -> {
                    exigir(args, 2, "remover <login>");
                    System.out.println(remover(args[1]));
                }
                default -> {
                    System.out.println("Comandos: listar | criar <login> admin|visitante | senha <login> | remover <login>");
                    System.exit(1);
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.out.println("Erro ao acessar o banco: " + e.getMessage());
            System.exit(2);
        }
    }

    public static String listar() throws Exception {
        List<Usuario> usuarios = new UsuarioDAO().consultarTodos();
        if (usuarios.isEmpty()) {
            return "Nenhum usuário cadastrado.\n";
        }
        StringBuilder texto = new StringBuilder();
        for (Usuario u : usuarios) {
            texto.append(String.format("%-50s %s%n", u.getLogin(), u.getPapel()));
        }
        return texto.toString();
    }

    public static String criar(String login, String papelInformado, String senha) throws Exception {
        String papel = papelInformado.trim().toUpperCase();
        // DONA fica de fora: essa conta é criada pela própria dona, na tela de primeiro acesso.
        if (!papel.equals(Usuario.ADMIN) && !papel.equals(Usuario.VISITANTE)) {
            throw new IllegalArgumentException("o papel deve ser admin ou visitante.");
        }
        String nome = login.trim();
        if (nome.isEmpty() || nome.length() > TAMANHO_MAXIMO_LOGIN) {
            throw new IllegalArgumentException("o login deve ter de 1 a " + TAMANHO_MAXIMO_LOGIN + " caracteres.");
        }
        validarSenha(senha);
        UsuarioDAO dao = new UsuarioDAO();
        if (dao.consultarPorLogin(nome) != null) {
            throw new IllegalArgumentException("já existe um usuário chamado " + nome + ".");
        }
        Usuario usuario = new Usuario();
        usuario.setLogin(nome);
        usuario.setSenhaHash(Senha.gerarHash(senha));
        usuario.setPapel(papel);
        dao.cadastrar(usuario);
        return "Usuário " + nome + " criado como " + papel + ".";
    }

    public static String trocarSenha(String login, String senha) throws Exception {
        validarSenha(senha);
        UsuarioDAO dao = new UsuarioDAO();
        Usuario usuario = buscar(dao, login);
        usuario.setSenhaHash(Senha.gerarHash(senha));
        dao.atualizarSenha(usuario);
        return "Senha de " + usuario.getLogin() + " alterada.";
    }

    public static String remover(String login) throws Exception {
        UsuarioDAO dao = new UsuarioDAO();
        Usuario usuario = buscar(dao, login);
        dao.excluir(usuario);
        String texto = "Usuário " + usuario.getLogin() + " removido.";
        if (Usuario.DONA.equals(usuario.getPapel())) {
            texto += " A tela de primeiro acesso foi reaberta para criar a conta da dona de novo.";
        }
        return texto + " Quem estava conectado com ele sai ao reiniciar o app (docker compose restart app).";
    }

    private static Usuario buscar(UsuarioDAO dao, String login) throws Exception {
        Usuario usuario = dao.consultarPorLogin(login.trim());
        if (usuario == null) {
            throw new IllegalArgumentException("o usuário " + login.trim() + " não existe. Veja a lista com: listar");
        }
        return usuario;
    }

    private static void validarSenha(String senha) {
        if (senha == null || senha.length() < TAMANHO_MINIMO_SENHA) {
            throw new IllegalArgumentException("a senha precisa ter pelo menos " + TAMANHO_MINIMO_SENHA + " caracteres.");
        }
    }

    // A senha é digitada sem aparecer na tela e não fica no histórico do terminal.
    private static String lerSenhaDuasVezes() {
        Console console = System.console();
        if (console == null) {
            throw new IllegalArgumentException("rode pelo deploy/usuarios.sh, num terminal, para digitar a senha.");
        }
        char[] primeira = console.readPassword("Senha (mínimo %d caracteres): ", TAMANHO_MINIMO_SENHA);
        char[] segunda = console.readPassword("Repita a senha: ");
        try {
            if (primeira == null || !Arrays.equals(primeira, segunda)) {
                throw new IllegalArgumentException("as duas senhas não são iguais.");
            }
            return new String(primeira);
        } finally {
            if (primeira != null) Arrays.fill(primeira, ' ');
            if (segunda != null) Arrays.fill(segunda, ' ');
        }
    }

    private static void exigir(String[] args, int quantidade, String uso) {
        if (args.length < quantidade) {
            throw new IllegalArgumentException("uso correto: " + uso);
        }
    }
}
