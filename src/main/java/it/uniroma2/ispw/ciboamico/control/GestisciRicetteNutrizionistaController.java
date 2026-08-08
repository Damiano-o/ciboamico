package it.uniroma2.ispw.ciboamico.control;

import it.uniroma2.ispw.ciboamico.bean.RicettaBean;
import it.uniroma2.ispw.ciboamico.bean.UtenteBean;
import it.uniroma2.ispw.ciboamico.entity.Ingrediente;
import it.uniroma2.ispw.ciboamico.entity.Prodotto;
import it.uniroma2.ispw.ciboamico.entity.Ricetta;
import it.uniroma2.ispw.ciboamico.entity.RuoloNutrizionista;
import it.uniroma2.ispw.ciboamico.entity.UnitaEnum;
import it.uniroma2.ispw.ciboamico.entity.Utente;
import it.uniroma2.ispw.ciboamico.pattern.singleton.SessionManager;
import it.uniroma2.ispw.ciboamico.exception.BusinessValidationException;
import it.uniroma2.ispw.ciboamico.persistence.dao.RicettaDAO;
import it.uniroma2.ispw.ciboamico.persistence.dao.UtenteDAO;
import it.uniroma2.ispw.ciboamico.persistence.factory.DAOFactory;

import java.time.ZoneId;
import java.time.LocalDate;

/**
 * Control di UC-07 Gestisci Ricette Nutrizionista.
 * Crea ricette con stato PROPOSTA (BR-05: min 2 ingredienti).
 * Flusso Bean-only: input RicettaBean, l'autore (RuoloNutrizionista) è
 * risolto dal DAO tramite l'utente loggato in sessione.
 */
public class GestisciRicetteNutrizionistaController {

    private final RicettaDAO ricettaDAO;
    private final UtenteDAO utenteDAO;

    public GestisciRicetteNutrizionistaController(DAOFactory factory) {
        this.ricettaDAO = factory.getRicettaDAO();
        this.utenteDAO = factory.getUtenteDAO();
    }

    /** Costruttore no-arg (stile 30/30): persistenza dal ServiceLocator. */
    public GestisciRicetteNutrizionistaController() {
        this(it.uniroma2.ispw.ciboamico.bootstrap.ApplicationModeManager.getInstance().getDAOFactory());
    }

    public RicettaBean creaRicetta(RicettaBean bean)
            throws BusinessValidationException {
        if (!bean.haAlmenoDueIngredienti()) {
            throw new IllegalArgumentException("La ricetta deve avere almeno 2 ingredienti (BR-05)");
        }
        RuoloNutrizionista autore = risolviAutoreCorrente();
        Ricetta ricetta = new Ricetta(bean.getNome(), bean.getIstruzioni(), autore);
        for (int i = 0; i < bean.getIngredientiNomi().size(); i++) {
            Prodotto p = new Prodotto(bean.getIngredientiNomi().get(i), 1.0, 100,
                    LocalDate.now(ZoneId.systemDefault()).plusYears(1), UnitaEnum.GRAMMI, null);
            double dose = i < bean.getDosi().size() ? bean.getDosi().get(i) : 1.0;
            ricetta.aggiungiIngrediente(new Ingrediente(p, dose, UnitaEnum.GRAMMI));
        }
        ricettaDAO.save(ricetta); // stato PROPOSTA
        return bean;
    }

    /** Risolve il RuoloNutrizionista dell'utente loggato interrogando il DAO. */
    private RuoloNutrizionista risolviAutoreCorrente() {
        UtenteBean sessione = SessionManager.getInstance().getLoggedUser();
        if (sessione == null) {
            throw new IllegalStateException("Utente non autenticato");
        }
        Utente utente = utenteDAO.findByEmail(sessione.getEmail());
        if (utente == null || !utente.haRuolo(RuoloNutrizionista.class)) {
            throw new IllegalStateException("Nutrizionista non trovato");
        }
        return utente.getRuolo(RuoloNutrizionista.class);
    }
}
