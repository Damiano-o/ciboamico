package it.uniroma2.ispw.ciboamico.pattern.observer;

import it.uniroma2.ispw.ciboamico.entity.Ordine;
import it.uniroma2.ispw.ciboamico.entity.OrdineEventListener;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Observer concreto: notifica il venditore al cambio stato dell'ordine.
 * In produzione delegherebbe a Jakarta Mail; qui logica funzionale pura.
 */
public class VenditoreNotifier implements OrdineEventListener {

    private static final Logger LOG = Logger.getLogger(VenditoreNotifier.class.getName());

    @Override
    public void onStatoCambiato(Ordine ordine) {
        // Funzionale: il venditore riceve una notifica di vendita.
        // (Implementazione reale: invio email via Jakarta Mail al recapito del venditore)
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info("[NOTIFICA] Ordine " + ordine.getIdOrdine()
                    + " del venditore " + ordine.getVenditore().getEmail()
                    + " -> stato " + ordine.getStato());
        }
    }
}
