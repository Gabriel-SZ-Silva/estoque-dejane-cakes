package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Usuario;
import util.FabricaConexao;

public class UsuarioDAO {

    public void cadastrar(Usuario usuario) throws SQLException, ClassNotFoundException {
        String sql = "insert into usuarios (login, senha_hash, papel) values (?, ?, ?)";
        try (Connection conn = FabricaConexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario.getLogin());
            ps.setString(2, usuario.getSenhaHash());
            ps.setString(3, usuario.getPapel());
            ps.execute();
        }
    }

    public Usuario consultarPorLogin(String login) throws SQLException, ClassNotFoundException {
        Usuario u = null;
        String sql = "select * from usuarios where login=?";

        try (Connection conn = FabricaConexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    u = montar(rs);
                }
            }
        }
        return u;
    }

    public List<Usuario> consultarTodos() throws SQLException, ClassNotFoundException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "select * from usuarios order by login";
        try (Connection conn = FabricaConexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(montar(rs));
            }
        }
        return lista;
    }

    public int contarTodos() throws SQLException, ClassNotFoundException {
        int total = 0;
        String sql = "select count(*) from usuarios";
        try (Connection conn = FabricaConexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                total = rs.getInt(1);
            }
        }
        return total;
    }

    // Usado pelo primeiro acesso: a tela fica aberta enquanto não existe a conta DONA.
    public int contarPorPapel(String papel) throws SQLException, ClassNotFoundException {
        int total = 0;
        String sql = "select count(*) from usuarios where papel=?";
        try (Connection conn = FabricaConexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, papel);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    total = rs.getInt(1);
                }
            }
        }
        return total;
    }

    public void atualizarSenha(Usuario usuario) throws SQLException, ClassNotFoundException {
        String sql = "update usuarios set senha_hash=? where id=?";
        try (Connection conn = FabricaConexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario.getSenhaHash());
            ps.setInt(2, usuario.getId());
            ps.executeUpdate();
        }
    }

    public void excluir(Usuario usuario) throws SQLException, ClassNotFoundException {
        String sql = "delete from usuarios where id=?";
        try (Connection conn = FabricaConexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuario.getId());
            ps.executeUpdate();
        }
    }

    private Usuario montar(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setLogin(rs.getString("login"));
        u.setSenhaHash(rs.getString("senha_hash"));
        u.setPapel(rs.getString("papel"));
        return u;
    }
}
