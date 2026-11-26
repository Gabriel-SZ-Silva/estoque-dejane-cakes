package factory;

import decorator.Material;
import decorator.MaterialBasico;
import decorator.TipoEmbalagem;
import decorator.TipoIngrediente;

public class MaterialFactory {

    // Monta o material com o decorator do tipo escolhido no formulário.
    // Fica em um só lugar para o cadastro e a alteração não repetirem o mesmo código.
    public static Material criarMaterial(String tipo) {
        Material material = new MaterialBasico();

        if (tipo != null) {
            if (tipo.equals("Ingrediente")) {
                material = new TipoIngrediente(material);
            } else if (tipo.equals("Embalagem")) {
                material = new TipoEmbalagem(material);
            }
        }

        return material;
    }
}
