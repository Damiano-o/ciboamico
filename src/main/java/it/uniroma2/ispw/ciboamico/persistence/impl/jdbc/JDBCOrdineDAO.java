package it.uniroma2.ispw.ciboamico.persistence.impl.jdbc;

import it.uniroma2.ispw.ciboamico.entity.Ordine;
import it.uniroma2.ispw.ciboamico.entity.StatoOrdineEnum;
import it.uniroma2.ispw.ciboamico.entity.Utente;
import it.uniroma2.ispw.ciboamico.persistence.dao.OrdineDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO JDBC per Ordine — salvataggio stato e totale.
 */
public class JDBCOrdineDAO implements OrdineDAO {

    private static final String COL_COMPRATORE = "compratore_email";
    private static final String COL_VENDITORE = "venditore_email";

    @Override
    public Ordine save(Ordine ordine) {
        String sql = "INSERT INTO ordini (id, compratore_email, venditore_email, stato, totale) "
                + "VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE stato = VALUES(stato), totale = VALUES(totale)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            try {
                ps.setLong(1, ordine.getIdOrdine());
                ps.setString(2, ordine.getCompratore().getEmail());
                ps.setString(3, ordine.getVenditore().getEmail());
                ps.setString(4, ordine.getStato().name());
                ps.setDouble(5, ordine.getTotale());
                ps.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
            return ordine;
        } catch (SQLException e) {
            throw new DAOException("Errore salvataggio ordine", e);
        }
    }

    @Override
    public Ordine findById(Long id) {
        String sql = "SELECT id, compratore_email, venditore_email, stato, totale FROM ordini WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Ordine o = new Ordine(rs.getLong("id"),
                            new Utente("c", rs.getString(COL_COMPRATORE), ""),
                            new Utente("v", rs.getString(COL_VENDITORE), ""));
                    o.ripristinaStato(StatoOrdineEnum.valueOf(rs.getString("stato")));
                    return o;
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DAOException("Errore ricerca ordine", e);
        }
    }

    @Override
    public List<Ordine> findByVenditore(String venditoreEmail) {
        String sql = "SELECT id, compratore_email, venditore_email, stato, totale "
                + "FROM ordini WHERE venditore_email = ?";
        List<Ordine> risultati = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, venditoreEmail);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    risultati.add(new Ordine(rs.getLong("id"),
                            new Utente("c", rs.getString(COL_COMPRATORE), ""),
                            new Utente("v", rs.getString(COL_VENDITORE), "")));
                }
            }
            return risultati;
        } catch (SQLException e) {
            throw new DAOException("Errore lettura ordini venditore", e);
        }
    }

    @Override
    public List<Ordine> findByCompratore(String compratoreEmail) {
        String sql = "SELECT id, compratore_email, venditore_email, stato, totale "
                + "FROM ordini WHERE compratore_email = ?";
        List<Ordine> risultati = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, compratoreEmail);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    risultati.add(new Ordine(rs.getLong("id"),
                            new Utente("c", rs.getString(COL_COMPRATORE), ""),
                            new Utente("v", rs.getString(COL_VENDITORE), "")));
                }
            }
            return risultati;
        } catch (SQLException e) {
            throw new DAOException("Errore lettura ordini compratore", e);
        }
    }
}
