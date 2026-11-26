package decorator;

public class TipoIngrediente extends MaterialDecorator {

    public TipoIngrediente(Material material) {
        super(material);
    }

    @Override
    public String getDescricao() {
        return super.getDescricao() + " do tipo Ingrediente";
    }
}
