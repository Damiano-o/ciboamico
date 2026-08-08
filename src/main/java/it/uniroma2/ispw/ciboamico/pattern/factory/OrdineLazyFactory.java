package it.uniroma2.ispw.ciboamico.pattern.factory;

import it.uniroma2.ispw.ciboamico.entity.Ordine;
import it.uniroma2.ispw.ciboamico.entity.Utente;
import it.uniroma2.ispw.ciboamico.exception.BusinessValidationException;
import it.uniroma2.ispw.ciboamico.persistence.dao.OrdineDAO;
import it.uniroma2.ispw.ciboamico.persistence.factory.DAOFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * LazyFactory (pattern Lazy Initialization + caching, come nei progetti
 * 30/30): crea gli Ordini assegnando l'id dal DAO (Information Expert)
 * e mantiene una cache in RAM degli ordini creati nella sessione.
 * Singleton thread-safe (Holder Idiom).
 *
 * Motivazione (verifica NotebookLM 2026-08-04): generare l'id nel
 * controller (System.currentTimeMillis) è un errore di accoppiamento:
 * il DAO è il creator/information expert del proprio tipo di dato.
 * La cache in RAM è una scelta di design premiata da De Angelis.
 */
public final class OrdineLazyFactory {

    private final OrdineDAO ordineDAO;
    private final List<Ordine> cacheOrdini = new ArrayList<>();

    private OrdineLazyFactory(DAOFactory factory) {
        this.ordineDAO = factory.getOrdineDAO();
    }

    private static class Container {
        private static OrdineLazyFactory INSTANCE;
    }

    /** Configura la factory con la DAOFactory attiva (chiamata UNA volta al bootstrap).
     *  Separate from getInstance() per evitare doppia responsabilità (PMD SingleMethodSingleton). */
    public static synchronized void configure(DAOFactory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("La DAOFactory di configurazione non può essere nulla.");
        }
        if (Container.INSTANCE != null) {
            throw new IllegalStateException("OrdineLazyFactory già configurata: chiamare configure una sola volta (bootstrap).");
        }
        Container.INSTANCE = new OrdineLazyFactory(factory);
    }

    /** Istanza configurata (da usare dopo il bootstrap); fallisce se non è stata chiamata configure(). */
    public static synchronized OrdineLazyFactory getInstance() {
        if (Container.INSTANCE == null) {
            throw new IllegalStateException("OrdineLazyFactory non configurata: chiamare configure(DAOFactory) prima (bootstrap).");
        }
        return Container.INSTANCE;
    }

    /**
     * Crea un nuovo ordine in stato CREATED assegnando l'id dal DAO.
     * L'ordine viene aggiunto alla cache in RAM (non ancora persistito).
     */
    public Ordine newOrdine(Utente compratore, Utente venditore)
            throws BusinessValidationException {
        long id = ordineDAO.getNextId();
        Ordine ordine = new Ordine(id, compratore, venditore);
        cacheOrdini.add(ordine);
        return ordine;
    }

    /** Ordini creati nella sessione corrente (cache in RAM). */
    public List<Ordine> getCacheOrdini() {
        return new ArrayList<>(cacheOrdini);
    }

    /** Resetta la cache e l'istanza (per test e cambio modalità). */
    public static synchronized void reset() {
        Container.INSTANCE = null;
    }
}
