package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.Insumo;
import model.ItemReceita;
import model.Receita;
import util.FabricaConexao;

public class ReceitaDAO {

    // Grava a receita e os ingredientes de uma vez só (transação):
    // ou entra tudo, ou nada. Assim não fica uma receita sem ingredientes se algo falhar no meio.
    public int cadastrar(Receita receita) throws SQLException, ClassNotFoundException {
        String sqlReceita = "insert into receitas (nome) values (?)";
        String sqlItem = "insert into itens_receita (id_receita, id_insumo, quantidade) values (?, ?, ?)";
        int idGerado = -1;

        try (Connection conn = FabricaConexao.getConexao()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sqlReceita, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement psItem = conn.prepareStatement(sqlItem)) {

                ps.setString(1, receita.getNome());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        idGerado = rs.getInt(1);
                        receita.setId(idGerado);
                    }
                }

                for (ItemReceita item : receita.getItens()) {
                    psItem.setInt(1, idGerado);
                    psItem.setInt(2, item.getIdInsumo());
                    psItem.setBigDecimal(3, item.getQuantidade());
                    psItem.execute();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
        return idGerado;
    }

    // Lista as receitas com um resumo: quantos ingredientes tem e quantos estão em falta no estoque.
    public List<Receita> consultarTodos() throws SQLException, ClassNotFoundException {
        List<Receita> lista = new ArrayList<>();
        String sql = "select r.id, r.nome, count(ir.id) as total_itens, "
                   + "coalesce(sum(case when i.quantidade < ir.quantidade then 1 else 0 end), 0) as em_falta "
                   + "from receitas r "
                   + "left join itens_receita ir on ir.id_receita = r.id "
                   + "left join insumos i on i.id = ir.id_insumo "
                   + "where r.excluido_em is null "
                   + "group by r.id, r.nome order by r.nome";

        try (Connection conn = FabricaConexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Receita r = new Receita();
                r.setId(rs.getInt("id"));
                r.setNome(rs.getString("nome"));
                r.setTotalItens(rs.getInt("total_itens"));
                r.setTotalEmFalta(rs.getInt("em_falta"));
                lista.add(r);
            }
        }
        return lista;
    }

    // Traz a receita ATIVA com os ingredientes e a quantidade que cada um tem em estoque.
    public Receita consultarById(Receita receitaBusca) throws SQLException, ClassNotFoundException {
        return consultarUma("select * from receitas where id=? and excluido_em is null", receitaBusca.getId());
    }

    // A mesma consulta, para uma receita que está na lixeira.
    public Receita consultarExcluidaById(Receita receitaBusca) throws SQLException, ClassNotFoundException {
        return consultarUma("select * from receitas where id=? and excluido_em is not null", receitaBusca.getId());
    }

    private Receita consultarUma(String sql, int id) throws SQLException, ClassNotFoundException {
        Receita r = null;
        try (Connection conn = FabricaConexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    r = new Receita();
                    r.setId(rs.getInt("id"));
                    r.setNome(rs.getString("nome"));
                    r.setExcluidoEm(rs.getTimestamp("excluido_em"));
                }
            }
        }

        if (r != null) {
            r.setItens(consultarItens(r));
        }
        return r;
    }

    // Receitas na lixeira, da excluída mais recente para a mais antiga (sem os ingredientes).
    public List<Receita> consultarExcluidas() throws SQLException, ClassNotFoundException {
        List<Receita> lista = new ArrayList<>();
        String sql = "select * from receitas where excluido_em is not null order by excluido_em desc";
        try (Connection conn = FabricaConexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Receita r = new Receita();
                r.setId(rs.getInt("id"));
                r.setNome(rs.getString("nome"));
                r.setExcluidoEm(rs.getTimestamp("excluido_em"));
                lista.add(r);
            }
        }
        return lista;
    }

    private List<ItemReceita> consultarItens(Receita receita) throws SQLException, ClassNotFoundException {
        List<ItemReceita> lista = new ArrayList<>();
        String sql = "select ir.id, ir.id_receita, ir.id_insumo, ir.quantidade, i.nome, i.unidade, i.quantidade as estoque "
                   + "from itens_receita ir inner join insumos i on i.id = ir.id_insumo where ir.id_receita=? order by i.nome";

        try (Connection conn = FabricaConexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, receita.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ItemReceita item = new ItemReceita();
                    item.setId(rs.getInt("id"));
                    item.setIdReceita(rs.getInt("id_receita"));
                    item.setIdInsumo(rs.getInt("id_insumo"));
                    item.setQuantidade(rs.getBigDecimal("quantidade"));
                    item.setNomeInsumo(rs.getString("nome"));
                    item.setUnidade(rs.getString("unidade"));
                    item.setQuantidadeEstoque(rs.getBigDecimal("estoque"));
                    lista.add(item);
                }
            }
        }
        return lista;
    }

    // Troca o nome e todos os ingredientes de uma vez (transação): apaga os itens antigos e grava os novos.
    public void atualizar(Receita receita) throws SQLException, ClassNotFoundException {
        String sqlNome = "update receitas set nome=? where id=?";
        String sqlApagaItens = "delete from itens_receita where id_receita=?";
        String sqlItem = "insert into itens_receita (id_receita, id_insumo, quantidade) values (?, ?, ?)";

        try (Connection conn = FabricaConexao.getConexao()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psNome = conn.prepareStatement(sqlNome);
                 PreparedStatement psApaga = conn.prepareStatement(sqlApagaItens);
                 PreparedStatement psItem = conn.prepareStatement(sqlItem)) {

                psNome.setString(1, receita.getNome());
                psNome.setInt(2, receita.getId());
                psNome.executeUpdate();

                psApaga.setInt(1, receita.getId());
                psApaga.executeUpdate();

                for (ItemReceita item : receita.getItens()) {
                    psItem.setInt(1, receita.getId());
                    psItem.setInt(2, item.getIdInsumo());
                    psItem.setBigDecimal(3, item.getQuantidade());
                    psItem.execute();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    // Dá baixa no estoque de todos os ingredientes, "vezes" vezes a receita, numa transação.
    // O "and quantidade >= ?" garante que o estoque não fica negativo, mesmo que alguém tenha
    // alterado o insumo entre a tela e o clique: nesse caso nada é descontado e o método devolve false.
    public boolean produzir(Receita receita, int vezes) throws SQLException, ClassNotFoundException {
        String sql = "update insumos set quantidade = quantidade - ? where id=? and quantidade >= ?";

        try (Connection conn = FabricaConexao.getConexao()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (ItemReceita item : receita.getItens()) {
                    BigDecimal usado = item.getQuantidade().multiply(BigDecimal.valueOf(vezes));
                    ps.setBigDecimal(1, usado);
                    ps.setInt(2, item.getIdInsumo());
                    ps.setBigDecimal(3, usado);
                    if (ps.executeUpdate() != 1) {
                        conn.rollback();
                        return false;
                    }
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    // Excluir manda para a lixeira; os ingredientes ficam guardados junto, para restaurar.
    public void moverParaLixeira(Receita receita) throws SQLException, ClassNotFoundException {
        String sql = "update receitas set excluido_em=? where id=?";
        try (Connection conn = FabricaConexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, receita.getId());
            ps.execute();
        }
    }

    public void restaurar(Receita receita) throws SQLException, ClassNotFoundException {
        String sql = "update receitas set excluido_em=null where id=?";
        try (Connection conn = FabricaConexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, receita.getId());
            ps.execute();
        }
    }

    // Apaga de vez as receitas na lixeira desde antes do limite.
    // Os ingredientes (itens_receita) vão junto, pelo ON DELETE CASCADE do banco.sql.
    public int apagarExcluidasAntesDe(Timestamp limite) throws SQLException, ClassNotFoundException {
        String sql = "delete from receitas where excluido_em < ?";
        try (Connection conn = FabricaConexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, limite);
            return ps.executeUpdate();
        }
    }

    // Já existe outra receita ATIVA com esse nome? (sem diferenciar maiúsculas, como o banco compara)
    // Na alteração, o id da própria receita é ignorado, para ela não acusar a si mesma.
    public boolean existeNome(String nome, Integer idIgnorado) throws SQLException, ClassNotFoundException {
        String sql = "select count(*) from receitas where nome=? and id<>? and excluido_em is null";
        try (Connection conn = FabricaConexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nome);
            ps.setInt(2, idIgnorado == null ? 0 : idIgnorado);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    // Em quantas receitas ATIVAS o insumo é usado (um insumo usado em receita não pode ser excluído).
    public int contarReceitasComInsumo(Insumo insumo) throws SQLException, ClassNotFoundException {
        int total = 0;
        String sql = "select count(*) from itens_receita ir inner join receitas r on r.id = ir.id_receita where ir.id_insumo=? and r.excluido_em is null";
        try (Connection conn = FabricaConexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, insumo.getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    total = rs.getInt(1);
                }
            }
        }
        return total;
    }
}
