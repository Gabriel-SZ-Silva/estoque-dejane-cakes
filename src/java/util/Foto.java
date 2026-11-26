package util;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Map;
import java.util.TreeMap;
import model.Insumo;
import model.ItemReceita;
import model.Receita;

// "Foto" de um insumo ou receita, em texto, para guardar na tabela versoes.
// Formato: uma linha por campo, "chave=valor", em ordem alfabética. Exemplo de insumo:
//   data_ultima_compra=2026-11-19
//   lote=L001
//   marca=Nestlé
//   nome=Farinha de trigo
//   quantidade=1.5
//   ...
// Receita: "nome=Bolo" e uma linha "item.<id do insumo>=<quantidade>" por ingrediente.
// Duas fotos iguais significam que nada mudou no registro.
public class Foto {

    public static String deInsumo(Insumo i) {
        Map<String, String> campos = new TreeMap<>();
        campos.put("nome", i.getNome());
        campos.put("tipo", i.getTipo());
        campos.put("marca", i.getMarca());
        campos.put("lote", i.getLote());
        campos.put("quantidade", Unidade.numeroParaCampo(i.getQuantidade()));
        campos.put("unidade", i.getUnidade());
        campos.put("validade", i.getValidade().toString());
        campos.put("data_ultima_compra", i.getDataUltimaCompra().toString());
        return escrever(campos);
    }

    public static Insumo paraInsumo(String foto) {
        Map<String, String> campos = ler(foto);
        Insumo i = new Insumo();
        i.setNome(campos.get("nome"));
        i.setTipo(campos.get("tipo"));
        i.setMarca(campos.get("marca"));
        i.setLote(campos.get("lote"));
        i.setQuantidade(new BigDecimal(campos.get("quantidade")));
        i.setUnidade(campos.get("unidade"));
        i.setValidade(Date.valueOf(campos.get("validade")));
        i.setDataUltimaCompra(Date.valueOf(campos.get("data_ultima_compra")));
        return i;
    }

    public static String deReceita(Receita r) {
        Map<String, String> campos = new TreeMap<>();
        campos.put("nome", r.getNome());
        for (ItemReceita item : r.getItens()) {
            campos.put("item." + item.getIdInsumo(), Unidade.numeroParaCampo(item.getQuantidade()));
        }
        return escrever(campos);
    }

    public static Receita paraReceita(String foto) {
        Map<String, String> campos = ler(foto);
        Receita r = new Receita();
        r.setNome(campos.get("nome"));
        for (Map.Entry<String, String> campo : campos.entrySet()) {
            if (campo.getKey().startsWith("item.")) {
                ItemReceita item = new ItemReceita();
                item.setIdInsumo(Integer.valueOf(campo.getKey().substring("item.".length())));
                item.setQuantidade(new BigDecimal(campo.getValue()));
                r.getItens().add(item);
            }
        }
        return r;
    }

    // Compara o conteúdo, não o texto: a mesma foto escrita de dois jeitos continua igual.
    public static boolean iguais(String foto1, String foto2) {
        return ler(foto1).equals(ler(foto2));
    }

    private static String escrever(Map<String, String> campos) {
        StringBuilder texto = new StringBuilder();
        for (Map.Entry<String, String> campo : campos.entrySet()) {
            String valor = campo.getValue() == null ? "" : campo.getValue();
            // Barra e quebra de linha viram \\ e \n, para cada campo caber numa linha.
            valor = valor.replace("\\", "\\\\").replace("\n", "\\n").replace("\r", "");
            texto.append(campo.getKey()).append('=').append(valor).append('\n');
        }
        return texto.toString();
    }

    private static Map<String, String> ler(String foto) {
        Map<String, String> campos = new TreeMap<>();
        for (String linha : foto.split("\n")) {
            int igual = linha.indexOf('=');
            if (igual > 0) {
                String valor = linha.substring(igual + 1);
                StringBuilder original = new StringBuilder();
                for (int k = 0; k < valor.length(); k++) {
                    char c = valor.charAt(k);
                    if (c == '\\' && k + 1 < valor.length()) {
                        char prox = valor.charAt(++k);
                        original.append(prox == 'n' ? '\n' : prox);
                    } else {
                        original.append(c);
                    }
                }
                campos.put(linha.substring(0, igual), original.toString());
            }
        }
        return campos;
    }
}
