package util;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

// Unidades de medida aceitas para a quantidade de um insumo.
// A quantidade aceita até 3 casas decimais (1,5 kg; 0,25 L; 2,5 un).
public class Unidade {

    // Código guardado no banco -> nome mostrado na tela.
    public static final Map<String, String> TODAS = new LinkedHashMap<>();
    static {
        TODAS.put("un", "unidade(s)");
        TODAS.put("g", "gramas (g)");
        TODAS.put("kg", "quilos (kg)");
        TODAS.put("ml", "mililitros (ml)");
        TODAS.put("L", "litros (L)");
        TODAS.put("pct", "pacote(s)");
        TODAS.put("cx", "caixa(s)");
    }

    public static final String PADRAO = "un";

    public static boolean existe(String codigo) {
        return codigo != null && TODAS.containsKey(codigo);
    }

    // Texto pronto para a tela, com vírgula e sem zeros sobrando: "500 g", "1,5 kg", "3 un".
    public static String formatar(BigDecimal quantidade, String unidade) {
        return numero(quantidade) + " " + (unidade == null ? PADRAO : unidade);
    }

    // Número do jeito brasileiro: 1.500 -> "1,5"; 2.000 -> "2".
    public static String numero(BigDecimal quantidade) {
        if (quantidade == null) {
            return "0";
        }
        return quantidade.stripTrailingZeros().toPlainString().replace('.', ',');
    }

    // Número do jeito que o campo type="number" do formulário espera (ponto, sem zeros sobrando).
    public static String numeroParaCampo(BigDecimal quantidade) {
        if (quantidade == null) {
            return "";
        }
        return quantidade.stripTrailingZeros().toPlainString();
    }
}
