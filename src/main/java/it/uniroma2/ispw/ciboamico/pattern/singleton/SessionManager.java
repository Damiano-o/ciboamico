package it.uniroma2.ispw.ciboamico.pattern.singleton;

import it.uniroma2.ispw.ciboamico.bean.UtenteBean;
import it.uniroma2.ispw.ciboamico.bean.OrdineBean;

/**
 * Singleton (Holder Idiom): custode dell'utente loggato e del checkout in
 * corso (UC-04). I controller applicativi sono stateless e leggono la
 * sessione da qui.
 */
public final class SessionManager {

    private UtenteBean loggedUser;
    private OrdineBean ordineInCorso;

    private SessionManager() { }

    private static class Container {
        private static final SessionManager INSTANCE = new SessionManager();
    }

    public static SessionManager getInstance() {
        return Container.INSTANCE;
    }

    public UtenteBean getLoggedUser() { return loggedUser; }
    public void setLoggedUser(UtenteBean loggedUser) { this.loggedUser = loggedUser; }
    public void logout() { this.loggedUser = null; }

    /** Ordine in checkout UC-04 (contiene il totale, scontato o pieno). */
    public OrdineBean getOrdineInCorso() { return ordineInCorso; }
    public void setOrdineInCorso(OrdineBean ordineInCorso) { this.ordineInCorso = ordineInCorso; }
}
