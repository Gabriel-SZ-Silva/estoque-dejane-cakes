package factory;

import command.ICommand;

public class CommandFactory {

    public static ICommand criarComando(String acao, String entidade) {
        ICommand comando = null;

        String nomeClasse = "command." + acao + entidade + "Action";

        try {
            Class<?> classe = Class.forName(nomeClasse);
            comando = (ICommand) classe.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            System.err.println("Comando não encontrado. A classe " + nomeClasse + " não existe no pacote command.");
        } catch (Exception e) {
            System.err.println("Erro crítico ao tentar instanciar a classe " + nomeClasse + ".");
            e.printStackTrace();
        }

        return comando;
    }
}
