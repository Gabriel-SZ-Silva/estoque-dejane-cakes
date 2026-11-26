package util;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

// Lê os campos que chegam do formulário e confere cada um antes de usar.
// Um campo em branco, um número ou uma data inválidos viram uma mensagem clara
// para a tela de aviso, em vez de um erro técnico do Java.
public class Parametro {

    public static String texto(HttpServletRequest request, String campo, String rotulo, int tamanhoMaximo) {
        String valor = request.getParameter(campo);
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("Preencha o campo " + rotulo + ".");
        }
        valor = valor.trim();
        if (valor.length() > tamanhoMaximo) {
            throw new IllegalArgumentException("O campo " + rotulo + " pode ter no máximo " + tamanhoMaximo + " caracteres.");
        }
        return valor;
    }

    public static int inteiro(HttpServletRequest request, String campo, String rotulo) {
        String valor = request.getParameter(campo);
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("Preencha o campo " + rotulo + ".");
        }
        try {
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("O campo " + rotulo + " precisa ser um número inteiro.");
        }
    }

    // Quantidade de 3 casas decimais no máximo, não negativa. Aceita vírgula ou ponto (1,5 ou 1.5).
    public static final int CASAS_DECIMAIS = 3;

    public static BigDecimal decimalNaoNegativo(HttpServletRequest request, String campo, String rotulo) {
        String valor = request.getParameter(campo);
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("Preencha o campo " + rotulo + ".");
        }
        BigDecimal numero;
        try {
            numero = new BigDecimal(valor.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("O campo " + rotulo + " precisa ser um número (use vírgula ou ponto para decimais).");
        }
        if (numero.signum() < 0) {
            throw new IllegalArgumentException("O campo " + rotulo + " não pode ser negativo.");
        }
        if (numero.stripTrailingZeros().scale() > CASAS_DECIMAIS) {
            throw new IllegalArgumentException("O campo " + rotulo + " aceita no máximo " + CASAS_DECIMAIS + " casas decimais.");
        }
        return numero;
    }

    // O formulário envia a data como aaaa-mm-dd (campo type="date").
    public static Date data(HttpServletRequest request, String campo, String rotulo) {
        String valor = request.getParameter(campo);
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("Preencha o campo " + rotulo + ".");
        }
        try {
            return Date.valueOf(LocalDate.parse(valor.trim()));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("A data do campo " + rotulo + " é inválida.");
        }
    }
}
