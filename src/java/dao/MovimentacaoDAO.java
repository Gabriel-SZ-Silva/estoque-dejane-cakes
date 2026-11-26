package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Movimentacao;
import util.FabricaConexao;

public class MovimentacaoDAO {

    // O dashboard mostra só as movimentações mais recentes; as antigas continuam no banco.
    public static final int LIMITE_HISTORICO = 200;

    public void cadastrar(Movimentacao movimentacao) throws SQLException, ClassNotFoundException {
        // Só as alterações apontam para uma versão (botão Desfazer); as demais deixam id_versao vazio.
        boolean temVersao = movimentacao.getIdVersao() != null;
        String sql = temVersao
                ? "insert into movimentacoes (tipo, descricao, id_versao) values (?, ?, ?)"
                : "insert into movimentacoes (tipo, descricao) values (?, ?)";
        try (Connection conn = FabricaConexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, movimentacao.getTipo());
            ps.setString(2, movimentacao.getDescricao());
            if (temVersao) {
                ps.setInt(3, movimentacao.getIdVersao());
            }
            ps.execute();
        }
    }

    public List<Movimentacao> consultarRecentes() throws SQLException, ClassNotFoundException {
        List<Movimentacao> lista = new ArrayList<>();
        String sql = "select * from movimentacoes order by id desc limit ?";
        try (Connection conn = FabricaConexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, LIMITE_HISTORICO);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Movimentacao m = new Movimentacao();
                    m.setId(rs.getInt("id"));
                    m.setTipo(rs.getString("tipo"));
                    m.setDescricao(rs.getString("descricao"));
                    m.setDataHora(rs.getTimestamp("data_hora"));
                    m.setIdVersao(rs.getObject("id_versao", Integer.class));
                    lista.add(m);
                }
            }
        }
        return lista;
    }
}
