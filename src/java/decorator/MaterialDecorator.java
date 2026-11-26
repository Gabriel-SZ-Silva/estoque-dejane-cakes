package decorator;

public abstract class MaterialDecorator implements Material {
    protected Material materialDecorado;

    public MaterialDecorator(Material material) {
        this.materialDecorado = material;
    }

    @Override
    public String getDescricao() {
        return materialDecorado.getDescricao();
    }
}
