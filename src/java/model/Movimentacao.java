package model;

import java.sql.Timestamp;

public class Movimentacao {
    private Integer id;
    private String tipo;
    private String descricao;
    private Timestamp dataHora;
    private Integer idVersao;      // preenchido nas alterações (ver versoes no banco.sql)
    private boolean desfazivel;    // calculado no dashboard: mostra o botão Desfazer

    public Movimentacao() {}

    // Tamanho da coluna descricao no banco.sql.
    public static final int TAMANHO_DESCRICAO = 1000;

    // Movimentação de uma alteração: guarda também a versão, para o botão Desfazer.
    public Movimentacao(String tipo, String descricao, Integer idVersao) {
        this(tipo, descricao);
        this.idVersao = idVersao;
    }

    public Movimentacao(String tipo, String descricao) {
        this.tipo = tipo;
        // Uma descrição maior que a coluna seria recusada pelo banco; corta e avisa com reticências.
        this.descricao = (descricao != null && descricao.length() > TAMANHO_DESCRICAO)
                ? descricao.substring(0, TAMANHO_DESCRICAO - 1) + "…" : descricao;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Timestamp getDataHora() {
        return dataHora;
    }
    public void setDataHora(Timestamp dataHora) {
        this.dataHora = dataHora;
    }

    public Integer getIdVersao() {
        return idVersao;
    }
    public void setIdVersao(Integer idVersao) {
        this.idVersao = idVersao;
    }

    public boolean isDesfazivel() {
        return desfazivel;
    }
    public void setDesfazivel(boolean desfazivel) {
        this.desfazivel = desfazivel;
    }
}
