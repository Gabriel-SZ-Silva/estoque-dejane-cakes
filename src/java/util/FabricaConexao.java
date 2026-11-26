package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class FabricaConexao {

    // No servidor (Docker), os dados de acesso vêm das variáveis de ambiente DB_URL, DB_USUARIO e DB_SENHA.
    // Rodando pelo NetBeans, sem essas variáveis, valem os mesmos dados de antes.
    public static Connection getConexao() throws ClassNotFoundException, SQLException {
        String DRIVER = "com.mysql.cj.jdbc.Driver";
        String URL = valor("DB_URL", "jdbc:mysql://localhost:3306/dejane_cakes_db");
        String USER = valor("DB_USUARIO", "root");
        String PASSWORD = valor("DB_SENHA", "admin");
        Class.forName(DRIVER);
        Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
        return con;
    }

    private static String valor(String variavel, String padrao) {
        String valor = System.getenv(variavel);
        return (valor == null || valor.isEmpty()) ? padrao : valor;
    }

}
