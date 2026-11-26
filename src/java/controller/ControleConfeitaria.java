package controller;

import command.ICommand;
import factory.CommandFactory;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ControleConfeitaria", urlPatterns = {"/ControleConfeitaria"})
public class ControleConfeitaria extends HttpServlet {

    // Ações que gravam, alteram, excluem ou fazem login só valem por POST (os formulários do sistema).
    // Junto com o cookie SameSite (META-INF/context.xml), isso impede que um link de outro site altere dados.
    private static final Set<String> ACOES_SO_POR_POST = Set.of("Cadastra", "Atualiza", "Deleta", "Produz", "Restaura", "Desfaz", "Entra");

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        try {
            String acao = request.getParameter("acao");
            String entidade = request.getParameter("entidade");

            // Os dois montam o nome de uma classe (CommandFactory): só letras, para não aceitar nada estranho.
            if (acao == null || entidade == null || !acao.matches("[A-Za-z]+") || !entidade.matches("[A-Za-z]+")) {
                throw new IllegalArgumentException("Ação ou Entidade não foram enviadas pelo formulário.");
            }
            if (!"POST".equals(request.getMethod()) && ACOES_SO_POR_POST.contains(acao)) {
                throw new IllegalArgumentException("Esta operação só pode ser feita pelos botões do sistema.");
            }
            ICommand comando = CommandFactory.criarComando(acao, entidade);

            if (comando != null) {
                String paginaDestino = comando.executar(request, response);
                // "redirect:pagina" manda o navegador abrir outra página.
                // É usado depois de gravar: assim o F5 na página seguinte não repete a gravação.
                if (paginaDestino.startsWith("redirect:")) {
                    response.sendRedirect(paginaDestino.substring("redirect:".length()));
                } else {
                    request.getRequestDispatcher(paginaDestino).forward(request, response);
                }
            } else {
                throw new IllegalArgumentException("Operação não encontrada: " + acao + " " + entidade + ".");
            }

        } catch (IllegalArgumentException e) {
            // Mensagens escritas pelo próprio sistema (campo em branco, número inválido...): vão direto para a tela.
            request.setAttribute("mensagemErro", e.getMessage());
            request.getRequestDispatcher("feedback.jsp").forward(request, response);

        } catch (Exception e) {
            // Erro técnico: o detalhe vai para o log do Tomcat; a tela mostra só um aviso simples.
            System.err.println("Erro no ControleConfeitaria: " + e.getMessage());
            e.printStackTrace();

            String aviso = (e instanceof SQLException)
                    ? "Não foi possível acessar o banco de dados. Tente de novo em instantes; se continuar, avise quem cuida do sistema."
                    : "Ocorreu um erro inesperado no servidor. Tente de novo; se continuar, avise quem cuida do sistema.";
            request.setAttribute("mensagemErro", aviso);
            request.getRequestDispatcher("feedback.jsp").forward(request, response);
        }
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Front Controller principal do sistema da Confeitaria";
    }
}
