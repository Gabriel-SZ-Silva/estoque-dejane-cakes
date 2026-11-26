package util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

// Mensagem de sucesso que sobrevive a um redirecionamento.
// Depois de gravar algo, o comando guarda a mensagem aqui e manda o navegador
// abrir outra página (redirect). Assim, apertar F5 nessa página não grava de novo.
// O AutenticacaoFilter chama transferir() em todo pedido, e a tela lê "mensagem" como sempre.
public class Aviso {

    private static final String CHAVE = "avisoPendente";

    public static void guardar(HttpServletRequest request, String mensagem) {
        request.getSession(true).setAttribute(CHAVE, mensagem);
    }

    public static void transferir(HttpServletRequest request) {
        HttpSession sessao = request.getSession(false);
        if (sessao == null) {
            return;
        }
        Object mensagem = sessao.getAttribute(CHAVE);
        if (mensagem != null) {
            request.setAttribute("mensagem", mensagem);
            sessao.removeAttribute(CHAVE);
        }
    }
}
