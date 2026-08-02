package it.uniroma2.ispw.ciboamico.persistence.dao;

import it.uniroma2.ispw.ciboamico.entity.Ricetta;

import java.util.List;

/**
 * Interfaccia DAO per Ricetta.
 */
public interface RicettaDAO {

    List<Ricetta> findAll();
    List<Ricetta> findByStato(String stato);
    Ricetta save(Ricetta ricetta);
}
