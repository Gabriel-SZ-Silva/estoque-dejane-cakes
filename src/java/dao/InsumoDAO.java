package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.Insumo;
import util.FabricaConexao;

public class InsumoDAO {

    public void cadastrar(Insumo insumo) throws SQLException, ClassNotFoundException {
        String sql = "insert into insumos (nome, tipo, marca, lote, quantidade, unidade, validade, data_ultima_compra) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = FabricaConexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, insumo.getNome());
            ps.setString(2, insumo.getTipo());
            ps.setString(3, insumo.getMarca());
            ps.setString(4, insumo.getLote());
            ps.setBigDecimal(5, insumo.getQuantidade());
            ps.setString(6, insumo.getUnidade());
            ps.setDate(7, insumo.getValidade());
            ps.setDate(8, insumo.getDataUltimaCompra());
            ps.execute();
        }
    }

    public List<Insumo> consultarTodos() throws SQLException, ClassNotFoundException {
        List<Insumo> lista = new ArrayList<>();
        String sql = "select * from insumos where excluido_em is null order by nome";

        try (Connection conn = FabricaConexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(montarInsumo(rs));
            }
        }
        return lista;
    }

    public void atualizar(Insumo insumo) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE insumos SET nome=?, tipo=?, marca=?, lote=?, quantidade=?, unidade=?, validade=?, data_ultima_compra=? WHERE id=?";

        try (Connection conn = FabricaConexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, insumo.getNome());
            ps.setString(2, insumo.getTipo());
            ps.setString(3, insumo.getMarca());
            ps.setString(4, insumo.getLote());
            ps.setBigDecimal(5, insumo.getQuantidade());
            ps.setString(6, insumo.getUnidade());
            ps.setDate(7, insumo.getValidade());
            ps.setDate(8, insumo.getDataUltimaCompra());

            ps.setInt(9, insumo.getId());

            ps.execute();
        }
    }

    // Excluir manda para a lixeira: o registro continua no banco e pode ser restaurado.
    public void moverParaLixeira(Insumo insumo) throws SQLException, ClassNotFoundException {
        String sql = "update insumos set excluido_em=? where id=?";
        try (Connection conn = FabricaConexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, insumo.getId());
            ps.execute();
        }
    }

    public void restaurar(Insumo insumo) throws SQLException, ClassNotFoundException {
        String sql = "update insumos set excluido_em=null where id=?";
        try (Connection conn = FabricaConexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, insumo.getId());
            ps.execute();
        }
    }

    // Insumos na lixeira, do excluído mais recente para o mais antigo.
    public List<Insumo> consultarExcluidos() throws SQLException, ClassNotFoundException {
        List<Insumo> lista = new ArrayList<>();
        String sql = "select * from insumos where excluido_em is not null order by excluido_em desc";
        try (Connection conn = FabricaConexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(montarInsumo(rs));
            }
        }
        return lista;
    }

    public Insumo consultarExcluidoById(Insumo insumoBusca) throws SQLException, ClassNotFoundException {
        Insumo i = null;
        String sql = "select * from insumos where id=? and excluido_em is not null";
        try (Connection conn = FabricaConexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, insumoBusca.getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    i = montarInsumo(rs);
                }
            }
        }
        return i;
    }

    // Apaga de vez o que está na lixeira desde antes do limite.
    // Insumos ainda ligados a uma receita da lixeira ficam, até a receita ser apagada.
    public int apagarExcluidosAntesDe(Timestamp limite) throws SQLException, ClassNotFoundException {
        String sql = "delete from insumos where excluido_em < ? and id not in (select id_insumo from itens_receita)";
        try (Connection conn = FabricaConexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, limite);
            return ps.executeUpdate();
        }
    }

    // Só os insumos ativos; os da lixeira usam consultarExcluidoById.
    public Insumo consultarById(Insumo insumoBusca) throws SQLException, ClassNotFoundException {
        Insumo i = null;
        String sql = "select * from insumos where id=? and excluido_em is null";

        try (Connection conn = FabricaConexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, insumoBusca.getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    i = montarInsumo(rs);
                }
            }
        }
        return i;
    }

    // Busca pelo nome, aceitando só um pedaço dele ("farinha" acha "Farinha de trigo").
    public List<Insumo> consultarPorNome(String nome) throws SQLException, ClassNotFoundException {
        List<Insumo> lista = new ArrayList<>();
        String sql = "select * from insumos where nome like ? and excluido_em is null order by nome";

        try (Connection conn = FabricaConexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // % e _ têm significado especial no LIKE; com a barra eles valem como letras comuns.
            String termo = nome.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
            ps.setString(1, "%" + termo + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(montarInsumo(rs));
                }
            }
        }
        return lista;
    }

    // Verifica se já existe outro insumo ATIVO com o mesmo nome, marca e lote (a lixeira não conta).
    // O id do próprio insumo é ignorado, para a alteração não acusar o registro que está sendo editado.
    public boolean existeIgual(Insumo insumo) throws SQLException, ClassNotFoundException {
        String sql = "select count(*) from insumos where nome=? and marca=? and lote=? and id<>? and excluido_em is null";

        try (Connection conn = FabricaConexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, insumo.getNome());
            ps.setString(2, insumo.getMarca());
            ps.setString(3, insumo.getLote());
            ps.setInt(4, insumo.getId() == null ? 0 : insumo.getId());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    // Insumos com validade anterior a hoje. O próprio dia da validade ainda conta como válido.
    public List<Insumo> consultarVencidos(Date hoje) throws SQLException, ClassNotFoundException {
        List<Insumo> lista = new ArrayList<>();
        String sql = "select * from insumos where validade < ? and excluido_em is null order by validade";

        try (Connection conn = FabricaConexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, hoje);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(montarInsumo(rs));
                }
            }
        }
        return lista;
    }

    // Insumos que vencem de hoje até a data limite, incluindo os dois dias.
    public List<Insumo> consultarVencendo(Date hoje, Date limite) throws SQLException, ClassNotFoundException {
        List<Insumo> lista = new ArrayList<>();
        String sql = "select * from insumos where validade >= ? and validade <= ? and excluido_em is null order by validade";

        try (Connection conn = FabricaConexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, hoje);
            ps.setDate(2, limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(montarInsumo(rs));
                }
            }
        }
        return lista;
    }

    private Insumo montarInsumo(ResultSet rs) throws SQLException {
        Insumo i = new Insumo();
        i.setId(rs.getInt("id"));
        i.setNome(rs.getString("nome"));
        i.setTipo(rs.getString("tipo"));
        i.setMarca(rs.getString("marca"));
        i.setLote(rs.getString("lote"));
        i.setQuantidade(rs.getBigDecimal("quantidade"));
        i.setUnidade(rs.getString("unidade"));
        i.setValidade(rs.getDate("validade"));
        i.setDataUltimaCompra(rs.getDate("data_ultima_compra"));
        i.setExcluidoEm(rs.getTimestamp("excluido_em"));
        return i;
    }
}
