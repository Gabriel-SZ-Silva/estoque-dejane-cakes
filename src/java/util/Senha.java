package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

// Guarda senhas com segurança: o banco nunca recebe a senha, só um hash com sal aleatório.
// Usa PBKDF2, que já vem no próprio Java, para não depender de bibliotecas externas.
public class Senha {

    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final int ITERACOES = 600000;
    private static final int TAMANHO_SAL = 16;
    private static final int TAMANHO_HASH = 256;

    // Formato salvo: pbkdf2$iteracoes$sal$hash (tudo o que é preciso para conferir depois).
    public static String gerarHash(String senha) {
        byte[] sal = new byte[TAMANHO_SAL];
        new SecureRandom().nextBytes(sal);
        byte[] hash = calcular(senha, sal, ITERACOES);
        Base64.Encoder base64 = Base64.getEncoder();
        return "pbkdf2$" + ITERACOES + "$" + base64.encodeToString(sal) + "$" + base64.encodeToString(hash);
    }

    public static boolean conferir(String senha, String hashSalvo) {
        if (senha == null || hashSalvo == null) {
            return false;
        }
        String[] partes = hashSalvo.split("\\$");
        if (partes.length != 4 || !partes[0].equals("pbkdf2")) {
            return false;
        }
        try {
            int iteracoes = Integer.parseInt(partes[1]);
            byte[] sal = Base64.getDecoder().decode(partes[2]);
            byte[] esperado = Base64.getDecoder().decode(partes[3]);
            // Compara em tempo constante: o tempo de resposta não revela quantos bytes acertaram.
            return MessageDigest.isEqual(esperado, calcular(senha, sal, iteracoes));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] calcular(String senha, byte[] sal, int iteracoes) {
        PBEKeySpec spec = new PBEKeySpec(senha.toCharArray(), sal, iteracoes, TAMANHO_HASH);
        try {
            return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("PBKDF2 não está disponível neste Java.", e);
        } finally {
            spec.clearPassword();
        }
    }
}
