package util;

public class Html {

    // Troca os caracteres especiais do HTML para que um texto digitado
    // (ex.: uma marca com aspas) apareça como texto e não quebre a página.
    public static String escapar(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

}
