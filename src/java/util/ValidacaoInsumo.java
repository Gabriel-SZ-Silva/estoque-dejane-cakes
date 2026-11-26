package util;

import dao.InsumoDAO;
import decorator.Material;
import factory.MaterialFactory;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import model.Insumo;

// Monta o insumo a partir do formulário e confere as regras antes de gravar.
// É usado pelo cadastro e pela alteração, para os dois seguirem as mesmas regras.
public class ValidacaoInsumo {

    // Tamanhos iguais aos das colunas da tabela insumos (banco.sql).
    public static final int TAMANHO_NOME = 100;
    public static final int TAMANHO_MARCA = 100;
    public static final int TAMANHO_LOTE = 50;

    // Lê os campos do formulário. Campo em branco, número ou data inválidos geram uma mensagem clara.
    public static Insumo lerFormulario(HttpServletRequest request) {
        Material material = MaterialFactory.criarMaterial(request.getParameter("radTipo"));

        Insumo insumo = new Insumo();
        insumo.setNome(Parametro.texto(request, "txtNome", "Nome do Insumo", TAMANHO_NOME));
        insumo.setTipo(material.getDescricao());
        insumo.setMarca(Parametro.texto(request, "txtMarca", "Marca", TAMANHO_MARCA));
        insumo.setLote(Parametro.texto(request, "txtLote", "Lote", TAMANHO_LOTE));
        insumo.setQuantidade(Parametro.decimalNaoNegativo(request, "txtQuantidade", "Quantidade em Estoque"));
        String unidade = request.getParameter("selUnidade");
        if (!Unidade.existe(unidade)) {
            throw new IllegalArgumentException("Escolha a unidade de medida da quantidade.");
        }
        insumo.setUnidade(unidade);
        insumo.setValidade(Parametro.data(request, "txtValidade", "Validade"));
        insumo.setDataUltimaCompra(Parametro.data(request, "txtDataUltimaCompra", "Data da Última Compra"));
        return insumo;
    }

    // Devolve a mensagem do problema encontrado, ou null quando o insumo pode ser gravado.
    public static String validar(Insumo insumo, InsumoDAO dao) throws Exception {
        if (insumo.getDataUltimaCompra().toLocalDate().isAfter(LocalDate.now())) {
            return "A data da última compra não pode ser depois de hoje.";
        }
        if (dao.existeIgual(insumo)) {
            return "Já existe um insumo " + insumo.getNome() + " da marca " + insumo.getMarca()
                    + " no lote " + insumo.getLote() + ". Para mudar a quantidade, edite o insumo existente.";
        }
        return null;
    }
}
