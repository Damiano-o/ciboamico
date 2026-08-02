package it.uniroma2.ispw.ciboamico.persistence.dao;

import it.uniroma2.ispw.ciboamico.entity.Ordine;

import java.util.List;

/**
 * Interfaccia DAO per Ordine.
 */
public interface OrdineDAO {

    Ordine save(Ordine ordine);
    Ordine findById(Long id);
    List<Ordine> findByVenditore(String venditoreEmail);
    List<Ordine> findByCompratore(String compratoreEmail);
}
