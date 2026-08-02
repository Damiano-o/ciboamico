package it.uniroma2.ispw.ciboamico.pattern.observer;

import it.uniroma2.ispw.ciboamico.entity.Ordine;
import it.uniroma2.ispw.ciboamico.entity.OrdineEventListener;

import java.util.logging.Logger;

/**
 * Observer concreto: notifica l'utente compratore al cambio stato.
 */
public class UtenteNotifier implements OrdineEventListener {

    private static final Logger LOG = Logger.getLogger(UtenteNotifier.class.getName());

    @Override
    public void onStatoCambiato(Ordine ordine) {
        // Funzionale: l'utente viene informato dell'avanzamento dell'ordine.
        LOG.info("[NOTIFICA] Ordine " + ordine.getIdOrdine()
                + " del compratore " + ordine.getCompratore().getEmail()
                + " -> stato " + ordine.getStato());
    }
}
