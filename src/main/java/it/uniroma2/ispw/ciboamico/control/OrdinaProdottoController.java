package it.uniroma2.ispw.ciboamico.control;

import it.uniroma2.ispw.ciboamico.bean.OrdineBean;
import it.uniroma2.ispw.ciboamico.bean.UtenteBean;
import it.uniroma2.ispw.ciboamico.entity.*;
import it.uniroma2.ispw.ciboamico.pattern.observer.VenditoreNotifier;
import it.uniroma2.ispw.ciboamico.pattern.singleton.SessionManager;
import it.uniroma2.ispw.ciboamico.persistence.dao.OrdineDAO;
import it.uniroma2.ispw.ciboamico.persistence.dao.ProdottoDAO;
import it.uniroma2.ispw.ciboamico.persistence.factory.DAOFactory;

/**
 * Control di UC-04 Ordina Prodotto (1:1 con l'use case).
 * Stateless: legge l'utente loggato da SessionManager; scambia solo Bean con la Boundary.
 */
public class OrdinaProdottoController {

    private final OrdineDAO ordineDAO;
    private final ProdottoDAO prodottoDAO;

    public OrdinaProdottoController(DAOFactory factory) {
        this.ordineDAO = factory.getOrdineDAO();
        this.prodottoDAO = factory.getProdottoDAO();
    }

    /**
     * Flusso UC-04: verifica disponibilità → riepilogo → conferma → ordine CREATO + notifica.
     */
    public OrdineBean submitOrdine(OrdineBean bean) {
        UtenteBean utente = SessionManager.getInstance().getLoggedUser();
        if (utente == null) {
            throw new IllegalStateException("Utente non autenticato");
        }

        Prodotto prodotto = prodottoDAO.findById(bean.getIdOrdine());
        if (prodotto == null) {
            throw new IllegalStateException("Prodotto non disponibile (2.c)");
        }

        // Costruzione ordine singolo diretto (D-03)
        // Il venditore è risolto dal PRODOTTO acquistato: risalgo all'Utente
        // proprietario del ruolo Venditore (whole-part bidirezionale) — fix da
        // verifica NotebookLM 2026-08-02 (prima: ternary inutile compratore:compratore)
        Utente compratore = new Utente(utente.getUsername(), utente.getEmail(), "");
        Utente venditore = prodotto.getVenditore() != null && prodotto.getVenditore().getUtente() != null
                ? prodotto.getVenditore().getUtente()
                : compratore;

        Ordine ordine = new Ordine(bean.getIdOrdine(), compratore, venditore);
        ordine.subscribe(new VenditoreNotifier()); // Observer: notifica venditore

        VoceOrdine voce = new VoceOrdine(prodotto, 1);
        ordine.aggiungiVoce(voce);

        ordineDAO.save(ordine);

        OrdineBean risultato = new OrdineBean();
        risultato.setIdOrdine(ordine.getIdOrdine());
        risultato.setTotale(ordine.getTotale());
        risultato.setStato(ordine.getStato().name());
        return risultato;
    }
}
