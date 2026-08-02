package it.uniroma2.ispw.ciboamico.persistence.impl.jdbc;

/**
 * Eccezione di dominio per la persistenza (NFR-06):
 * incapsula SQLException/IOException senza propagarle ai layer superiori.
 */
public class DAOException extends RuntimeException {

    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }
}
