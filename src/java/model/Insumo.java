package model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Insumo {
    private Integer id;
    private String nome;
    private String tipo;
    private String marca;
    private String lote;
    private BigDecimal quantidade;   // até 3 casas decimais (1,5 kg)
    private String unidade;   // un, g, kg, ml, L, pct, cx (ver util/Unidade)
    private Date validade;
    private Date dataUltimaCompra;
    private Timestamp excluidoEm;

    public Insumo() {}

    // Quantos dias faltam para a validade: 0 = vence hoje, negativo = já venceu.
    public long getDiasParaVencer() {
        return ChronoUnit.DAYS.between(LocalDate.now(), validade.toLocalDate());
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

    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMarca() {
        return marca;
    }
    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getLote() {
        return lote;
    }
    public void setLote(String lote) {
        this.lote = lote;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }
    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public String getUnidade() {
        return unidade;
    }
    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    // Quantidade com a unidade, pronta para a tela: "500 g".
    public String getQuantidadeFormatada() {
        return util.Unidade.formatar(quantidade, unidade);
    }

    public Date getValidade() {
        return validade;
    }
    public void setValidade(Date validade) {
        this.validade = validade;
    }

    public Date getDataUltimaCompra() {
        return dataUltimaCompra;
    }
    public void setDataUltimaCompra(Date dataUltimaCompra) {
        this.dataUltimaCompra = dataUltimaCompra;
    }

    // Preenchido quando o registro está na lixeira (ver banco.sql).
    public Timestamp getExcluidoEm() {
        return excluidoEm;
    }
    public void setExcluidoEm(Timestamp excluidoEm) {
        this.excluidoEm = excluidoEm;
    }
}
