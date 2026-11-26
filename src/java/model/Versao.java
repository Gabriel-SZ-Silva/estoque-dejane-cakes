package model;

import java.sql.Timestamp;

// Uma alteração guardada para poder ser desfeita: como o registro estava antes e como ficou depois.
public class Versao {
    public static final String INSUMO = "Insumo";
    public static final String RECEITA = "Receita";

    private Integer id;
    private String entidade;      // INSUMO ou RECEITA
    private Integer idRegistro;
    private String dadosAntes;    // formato de util/Foto
    private String dadosDepois;
    private boolean desfeita;
    private Timestamp dataHora;

    public Versao() {}

    public Versao(String entidade, Integer idRegistro, String dadosAntes, String dadosDepois) {
        this.entidade = entidade;
        this.idRegistro = idRegistro;
        this.dadosAntes = dadosAntes;
        this.dadosDepois = dadosDepois;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getEntidade() {
        return entidade;
    }
    public void setEntidade(String entidade) {
        this.entidade = entidade;
    }

    public Integer getIdRegistro() {
        return idRegistro;
    }
    public void setIdRegistro(Integer idRegistro) {
        this.idRegistro = idRegistro;
    }

    public String getDadosAntes() {
        return dadosAntes;
    }
    public void setDadosAntes(String dadosAntes) {
        this.dadosAntes = dadosAntes;
    }

    public String getDadosDepois() {
        return dadosDepois;
    }
    public void setDadosDepois(String dadosDepois) {
        this.dadosDepois = dadosDepois;
    }

    public boolean isDesfeita() {
        return desfeita;
    }
    public void setDesfeita(boolean desfeita) {
        this.desfeita = desfeita;
    }

    public Timestamp getDataHora() {
        return dataHora;
    }
    public void setDataHora(Timestamp dataHora) {
        this.dataHora = dataHora;
    }
}
