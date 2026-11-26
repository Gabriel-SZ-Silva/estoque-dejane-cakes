package decorator;

public class TipoEmbalagem extends MaterialDecorator {

    public TipoEmbalagem(Material material) {
        super(material);
    }

    @Override
    public String getDescricao() {
        return super.getDescricao() + " do tipo Embalagem";
    }
}
