package it.uniroma2.ispw.ciboamico.persistence.impl.jdbc;

import it.uniroma2.ispw.ciboamico.entity.Ricetta;
import it.uniroma2.ispw.ciboamico.entity.RuoloNutrizionista;
import it.uniroma2.ispw.ciboamico.persistence.dao.RicettaDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO JDBC per Ricetta — con join ingredienti.
 */
public class JDBCRicettaDAO implements RicettaDAO {

    @Override
    public List<Ricetta> findAll() {
        return findByStato(null);
    }

    @Override
    public List<Ricetta> findByStato(String stato) {
        String sql = "SELECT id, nome, istruzioni, stato, autore_email FROM ricette";
        if (stato != null) {
            sql += " WHERE stato = ?";
        }
        List<Ricetta> risultati = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (stato != null) {
                ps.setString(1, stato);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Ricetta r = new Ricetta(rs.getString("nome"),
                            rs.getString("istruzioni"), new RuoloNutrizionista());
                    r.setStato(it.uniroma2.ispw.ciboamico.entity.StatoRicettaEnum
                            .valueOf(rs.getString("stato")));
                    risultati.add(r);
                }
            }
            return risultati;
        } catch (SQLException e) {
            throw new DAOException("Errore lettura ricette", e);
        }
    }

    @Override
    public Ricetta save(Ricetta ricetta) {
        String sql = "INSERT INTO ricette (nome, istruzioni, stato, autore_email) "
                + "VALUES (?, ?, ?, 'demo')";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            conn.setAutoCommit(false);
            try {
                ps.setString(1, ricetta.getNome());
                ps.setString(2, ricetta.getIstruzioni());
                ps.setString(3, ricetta.getStato().name());
                ps.executeUpdate();
                conn.commit();
            } catch (SQLException tex) {
                conn.rollback();
                throw tex;
            } finally {
                conn.setAutoCommit(true);
            }
            return ricetta;
        } catch (SQLException e) {
            throw new DAOException("Errore salvataggio ricetta", e);
        }
    }
}
