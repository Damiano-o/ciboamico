package it.uniroma2.ispw.ciboamico.control;

import it.uniroma2.ispw.ciboamico.bean.ProdottoBean;
import it.uniroma2.ispw.ciboamico.entity.*;
import it.uniroma2.ispw.ciboamico.exception.BusinessValidationException;
import it.uniroma2.ispw.ciboamico.bean.UtenteBean;
import it.uniroma2.ispw.ciboamico.pattern.singleton.SessionManager;
import it.uniroma2.ispw.ciboamico.persistence.dao.ProdottoDAO;
import it.uniroma2.ispw.ciboamico.persistence.dao.UtenteDAO;
import it.uniroma2.ispw.ciboamico.persistence.factory.DAOFactory;

import java.time.ZoneId;
import java.time.LocalDate;

/**
 * Control di UC-05 Gestisci Catalogo Venditore.
 * Solo venditori APPROVATI possono pubblicare (BR-02); prezzo>0 (BR-06),
 * quantità>=0 (BR-03), non scaduto (BR-01).
 * Flusso Bean-only: input ProdottoBean, il venditore è risolto dal DAO
 * tramite l'utente loggato in sessione (compartimento stagno BCE).
 */
public class GestisciCatalogoVenditoreController {

    private final ProdottoDAO prodottoDAO;
    private final UtenteDAO utenteDAO;

    public GestisciCatalogoVenditoreController(DAOFactory factory) {
        this.prodottoDAO = factory.getProdottoDAO();
        this.utenteDAO = factory.getUtenteDAO();
    }

    public ProdottoBean pubblicaProdotto(ProdottoBean bean)
            throws BusinessValidationException {
        RuoloVenditore venditore = risolviVenditoreCorrente();
        if (venditore.getStato() != StatoVenditoreEnum.APPROVATO) {
            throw new IllegalStateException("Venditore non approvato (1.a, BR-02)");
        }
        if (bean.getPrezzo() == null || bean.getPrezzo() <= 0) {
            throw new IllegalArgumentException("Il prezzo deve essere maggiore di 0 (BR-06)");
        }
        Prodotto prodotto = new Prodotto(
                bean.getNome(),
                bean.getPrezzo(),
                bean.getQuantita().intValue(),
                bean.getScadenza() != null ? bean.getScadenza() : LocalDate.now(ZoneId.systemDefault()).plusYears(1),
                UnitaEnum.valueOf(bean.getUnitaMisura()),
                venditore);
        prodottoDAO.save(prodotto);
        return bean;
    }

    /**
     * Risolve il RuoloVenditore dell'utente loggato interrogando il DAO
     * (niente più fake hardcoded). Lancia se non autenticato o senza ruolo.
     */
    private RuoloVenditore risolviVenditoreCorrente() {
        UtenteBean sessione = SessionManager.getInstance().getLoggedUser();
        if (sessione == null) {
            throw new IllegalStateException("Utente non autenticato");
        }
        Utente utente = utenteDAO.findByEmail(sessione.getEmail());
        if (utente == null) {
            throw new IllegalStateException("Venditore non trovato");
        }
        RuoloVenditore ruolo = utente.getRuolo(RuoloVenditore.class);
        if (ruolo == null) {
            throw new IllegalStateException("Venditore non trovato");
        }
        return ruolo;
    }
}
