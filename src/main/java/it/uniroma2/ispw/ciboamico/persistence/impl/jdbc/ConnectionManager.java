package it.uniroma2.ispw.ciboamico.persistence.impl.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestione centralizzata della connessione JDBC (config da application.properties).
 */
public final class ConnectionManager {

    private static final String URL =
            System.getProperty("ciboamico.db.url", "jdbc:mysql://localhost:3306/ciboamico");
    private static final String USER =
            System.getProperty("ciboamico.db.user", "root");
    private static final String PASSWORD =
            System.getProperty("ciboamico.db.password", "root");

    private ConnectionManager() { }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
