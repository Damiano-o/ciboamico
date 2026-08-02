package it.uniroma2.ispw.ciboamico.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Subject del pattern Observer (GoF): gestisce i listener e li notifica
 * al cambio stato dell'ordine. Disaccoppia la logica ordine dalle notifiche.
 */
public class OrdineSubject {

    private final List<OrdineEventListener> listeners = new ArrayList<>();

    public void subscribe(OrdineEventListener listener) {
        listeners.add(listener);
    }

    public void unsubscribe(OrdineEventListener listener) {
        listeners.remove(listener);
    }

    public void notifyObservers(Ordine ordine) {
        for (OrdineEventListener listener : listeners) {
            listener.onStatoCambiato(ordine);
        }
    }
}
