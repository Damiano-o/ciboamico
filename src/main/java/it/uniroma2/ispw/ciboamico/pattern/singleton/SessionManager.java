package it.uniroma2.ispw.ciboamico.pattern.singleton;

import it.uniroma2.ispw.ciboamico.bean.UtenteBean;

/**
 * Singleton (Holder Idiom): custode dell'utente loggato.
 * I controller applicativi sono stateless e leggono la sessione da qui.
 */
public final class SessionManager {

    private UtenteBean loggedUser;

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
}
