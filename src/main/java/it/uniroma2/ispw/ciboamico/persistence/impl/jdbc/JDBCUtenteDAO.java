package it.uniroma2.ispw.ciboamico.persistence.impl.jdbc;

import it.uniroma2.ispw.ciboamico.entity.Utente;
import it.uniroma2.ispw.ciboamico.persistence.dao.UtenteDAO;

import java.sql.*;

/**
 * DAO JDBC per Utente — PreparedStatement anti SQL-injection (NFR-02).
 * Le eccezioni SQL sono incapsulate in DAOException (NFR-06).
 */
public class JDBCUtenteDAO implements UtenteDAO {

    private Connection getConnection() throws SQLException {
        // Configurazione reale in application.properties
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/ciboamico", "root", "root");
    }

    @Override
    public Utente findByEmail(String email) {
        String sql = "SELECT nome, email, password_hash FROM utenti WHERE email = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Utente(rs.getString("nome"), rs.getString("email"),
                            rs.getString("password_hash"));
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DAOException("Errore ricerca utente", e);
        }
    }

    @Override
    public Utente save(Utente utente) {
        String sql = "INSERT INTO utenti (nome, email, password_hash) VALUES (?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE nome = VALUES(nome)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            try {
                ps.setString(1, utente.getNome());
                ps.setString(2, utente.getEmail());
                ps.setString(3, utente.getPasswordHash());
                ps.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
            return utente;
        } catch (SQLException e) {
            throw new DAOException("Errore salvataggio utente", e);
        }
    }
}
