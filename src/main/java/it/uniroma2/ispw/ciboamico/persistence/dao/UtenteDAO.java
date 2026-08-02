package it.uniroma2.ispw.ciboamico.persistence.dao;

import it.uniroma2.ispw.ciboamico.entity.Utente;

/**
 * Interfaccia DAO per Utente — implementata da JDBC, FS e Demo.
 */
public interface UtenteDAO {

    Utente findByEmail(String email);
    Utente save(Utente utente);
}
