package it.uniroma2.ispw.ciboamico.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Listener del pattern Observer — notifiche di cambio stato ordine.
 */
public interface OrdineEventListener {
    void onStatoCambiato(Ordine ordine);
}
