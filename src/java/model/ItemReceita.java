package model;

import java.math.BigDecimal;

// Um ingrediente da receita: qual insumo e quanto dele a receita usa.
public class ItemReceita {
    private Integer id;
    private Integer idReceita;
    private Integer idInsumo;
    private String nomeInsumo;
    private String unidade;   // a mesma unidade do insumo
    private BigDecimal quantidade;
    private BigDecimal quantidadeEstoque;

    public ItemReceita() {}

    // Está em falta quando o estoque é menor que a quantidade que a receita usa.
    public boolean isEmFalta() {
        return quantidadeEstoque.compareTo(quantidade) < 0;
    }

    public BigDecimal getQuantidadeFaltante() {
        return quantidade.subtract(quantidadeEstoque);
    }

    public String getUnidade() {
        return unidade;
    }
    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String getQuantidadeFormatada() {
        return util.Unidade.formatar(quantidade, unidade);
    }
    public String getQuantidadeEstoqueFormatada() {
        return util.Unidade.formatar(quantidadeEstoque, unidade);
    }
    public String getQuantidadeFaltanteFormatada() {
        return util.Unidade.formatar(getQuantidadeFaltante(), unidade);
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdReceita() {
        return idReceita;
    }
    public void setIdReceita(Integer idReceita) {
        this.idReceita = idReceita;
    }

    public Integer getIdInsumo() {
        return idInsumo;
    }
    public void setIdInsumo(Integer idInsumo) {
        this.idInsumo = idInsumo;
    }

    public String getNomeInsumo() {
        return nomeInsumo;
    }
    public void setNomeInsumo(String nomeInsumo) {
        this.nomeInsumo = nomeInsumo;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }
    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getQuantidadeEstoque() {
        return quantidadeEstoque;
    }
    public void setQuantidadeEstoque(BigDecimal quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }
}
