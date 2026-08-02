package it.uniroma2.ispw.ciboamico.entity;

/**
 * Listener del pattern Observer — notifiche di cambio stato ordine.
 */
public interface OrdineEventListener {
    void onStatoCambiato(Ordine ordine);
}
