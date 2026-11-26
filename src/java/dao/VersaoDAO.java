package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import model.Versao;
import util.FabricaConexao;

public class VersaoDAO {

    // Guarda a versão e devolve o id gerado, para a movimentação apontar para ela.
    public int cadastrar(Versao versao) throws SQLException, ClassNotFoundException {
        String sql = "insert into versoes (entidade, id_registro, dados_antes, dados_depois) values (?, ?, ?, ?)";
        try (Connection conn = FabricaConexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, versao.getEntidade());
            ps.setInt(2, versao.getIdRegistro());
            ps.setString(3, versao.getDadosAntes());
            ps.setString(4, versao.getDadosDepois());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                versao.setId(rs.getInt(1));
            }
        }
        return versao.getId();
    }

    public Versao consultarById(int id) throws SQLException, ClassNotFoundException {
        String sql = "select * from versoes where id=?";
        try (Connection conn = FabricaConexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Versao v = new Versao();
                v.setId(rs.getInt("id"));
                v.setEntidade(rs.getString("entidade"));
                v.setIdRegistro(rs.getInt("id_registro"));
                v.setDadosAntes(rs.getString("dados_antes"));
                v.setDadosDepois(rs.getString("dados_depois"));
                v.setDesfeita(rs.getBoolean("desfeita"));
                v.setDataHora(rs.getTimestamp("data_hora"));
                return v;
            }
        }
    }

    // Id da versão mais recente do registro: só ela pode ser desfeita.
    public int consultarUltimoId(String entidade, int idRegistro) throws SQLException, ClassNotFoundException {
        String sql = "select max(id) from versoes where entidade=? and id_registro=?";
        try (Connection conn = FabricaConexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, entidade);
            ps.setInt(2, idRegistro);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public void marcarDesfeita(Versao versao) throws SQLException, ClassNotFoundException {
        String sql = "update versoes set desfeita=? where id=?";
        try (Connection conn = FabricaConexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, true);
            ps.setInt(2, versao.getId());
            ps.execute();
        }
    }
}
