package filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Set;
import model.Usuario;
import util.Aviso;

// Fica na frente de todo o sistema: quem não entrou com usuário e senha vai para a tela de login.
@WebFilter(urlPatterns = {"/*"})
public class AutenticacaoFilter implements Filter {

    public static final String USUARIO_LOGADO = "usuarioLogado";
    public static final String PAPEL_USUARIO = "papelUsuario";

    // Conta de visitante (papel VISITANTE) só visualiza: estas ações e telas ficam fechadas para ela.
    private static final Set<String> ACOES_DE_ALTERACAO = Set.of(
            "Cadastra", "Atualiza", "Deleta", "Produz", "Restaura", "Desfaz", "Prepara");
    private static final Set<String> FORMULARIOS = Set.of(
            "/formCadastrarInsumo.jsp", "/formEditarInsumo.jsp", "/formCadastrarReceita.jsp");
    static final String AVISO_VISITANTE = "Conta de visitante: você pode ver todas as telas, mas não pode cadastrar, alterar nem excluir dados.";

    // O que abre sem login: a tela de entrada e os arquivos que ela e o app do celular usam.
    private static final Set<String> CAMINHOS_LIVRES = Set.of(
            "/login.jsp", "/primeiroAcesso.jsp", "/offline.html", "/manifest.json", "/sw.js", "/erro.jsp");
    private static final Set<String> ACOES_LIVRES = Set.of("Entra", "Sai", "Cadastra");

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        // Antes de ler qualquer parâmetro, para os acentos chegarem certos.
        request.setCharacterEncoding("UTF-8");

        if (!arquivoEstatico(request)) {
            // Telas com dados não ficam guardadas no navegador: depois de sair, o botão "voltar"
            // não mostra o estoque para quem pegar o aparelho.
            response.setHeader("Cache-Control", "no-store");
            // Mensagem de sucesso deixada pelo comando anterior (ver util/Aviso).
            Aviso.transferir(request);
        }

        HttpSession sessao = request.getSession(false);
        boolean logado = sessao != null && sessao.getAttribute(USUARIO_LOGADO) != null;

        if (logado && Usuario.VISITANTE.equals(sessao.getAttribute(PAPEL_USUARIO)) && alteraDados(request)) {
            request.setAttribute("mensagemErro", AVISO_VISITANTE);
            request.getRequestDispatcher("/feedback.jsp").forward(request, response);
        } else if (logado || caminhoLivre(request)) {
            chain.doFilter(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        }
    }

    private boolean arquivoEstatico(HttpServletRequest request) {
        String caminho = request.getServletPath();
        return caminho.startsWith("/css/") || caminho.startsWith("/img/")
                || caminho.equals("/manifest.json") || caminho.equals("/sw.js") || caminho.equals("/offline.html");
    }

    private boolean alteraDados(HttpServletRequest request) {
        String caminho = request.getServletPath();
        if (FORMULARIOS.contains(caminho)) {
            return true;
        }
        if (!caminho.equals("/ControleConfeitaria")) {
            return false;
        }
        String acao = request.getParameter("acao");
        // ConsultaById de Insumo só abre o formulário de edição.
        return ACOES_DE_ALTERACAO.contains(acao)
                || ("ConsultaById".equals(acao) && "Insumo".equals(request.getParameter("entidade")));
    }

    private boolean caminhoLivre(HttpServletRequest request) {
        String caminho = request.getServletPath();
        if (CAMINHOS_LIVRES.contains(caminho) || arquivoEstatico(request)) {
            return true;
        }
        // No controlador, sem login, só entrar, sair e criar o primeiro acesso.
        return caminho.equals("/ControleConfeitaria")
                && "Usuario".equals(request.getParameter("entidade"))
                && ACOES_LIVRES.contains(request.getParameter("acao"));
    }
}
