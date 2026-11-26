package model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Receita {
    private Integer id;
    private String nome;
    private List<ItemReceita> itens;
    // Resumo usado na lista de receitas: quantos ingredientes tem e quantos estão em falta.
    private Integer totalItens;
    private Integer totalEmFalta;
    private Timestamp excluidoEm;

    public Receita() {
        this.itens = new ArrayList<>();
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getTotalItens() {
        return totalItens;
    }
    public void setTotalItens(Integer totalItens) {
        this.totalItens = totalItens;
    }

    public Integer getTotalEmFalta() {
        return totalEmFalta;
    }
    public void setTotalEmFalta(Integer totalEmFalta) {
        this.totalEmFalta = totalEmFalta;
    }

    public List<ItemReceita> getItens() {
        return itens;
    }
    public void setItens(List<ItemReceita> itens) {
        this.itens = itens;
    }

    // Preenchido quando o registro está na lixeira (ver banco.sql).
    public Timestamp getExcluidoEm() {
        return excluidoEm;
    }
    public void setExcluidoEm(Timestamp excluidoEm) {
        this.excluidoEm = excluidoEm;
    }
}
