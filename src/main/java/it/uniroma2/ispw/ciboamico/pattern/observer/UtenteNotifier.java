package it.uniroma2.ispw.ciboamico.pattern.observer;

import it.uniroma2.ispw.ciboamico.entity.Ordine;
import it.uniroma2.ispw.ciboamico.entity.OrdineEventListener;

/**
 * Observer concreto: notifica l'utente compratore al cambio stato.
 */
public class UtenteNotifier implements OrdineEventListener {

    @Override
    public void onStatoCambiato(Ordine ordine) {
        // Funzionale: l'utente viene informato dell'avanzamento dell'ordine.
        System.out.println("[NOTIFICA] Ordine " + ordine.getIdOrdine()
                + " del compratore " + ordine.getCompratore().getEmail()
                + " -> stato " + ordine.getStato());
    }
}
