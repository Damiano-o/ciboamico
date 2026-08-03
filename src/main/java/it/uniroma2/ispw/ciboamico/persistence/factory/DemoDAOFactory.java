package it.uniroma2.ispw.ciboamico.persistence.factory;

import it.uniroma2.ispw.ciboamico.entity.*;
import it.uniroma2.ispw.ciboamico.exception.BusinessValidationException;
import it.uniroma2.ispw.ciboamico.persistence.dao.OrdineDAO;
import it.uniroma2.ispw.ciboamico.persistence.dao.ProdottoDAO;
import it.uniroma2.ispw.ciboamico.persistence.dao.RicettaDAO;
import it.uniroma2.ispw.ciboamico.persistence.dao.UtenteDAO;
import it.uniroma2.ispw.ciboamico.persistence.impl.demo.DemoOrdineDAO;
import it.uniroma2.ispw.ciboamico.persistence.impl.demo.DemoProdottoDAO;
import it.uniroma2.ispw.ciboamico.persistence.impl.demo.DemoRicettaDAO;
import it.uniroma2.ispw.ciboamico.persistence.impl.demo.DemoUtenteDAO;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.List;

/**
 * Factory DEMO: DAO in-memory con dati seed (utenti, prodotti, ricette).
 * Riutilizza le stesse istanze DAO: lo spazio dati è condiviso tra chiamate
 * della stessa factory (ma isolato tra factory diverse — test indipendenti).
 * Il seed rende l'applicazione utilizzabile in modalità demo senza DB.
 */
public class DemoDAOFactory extends DAOFactory {

    private final UtenteDAO utenteDAO = new DemoUtenteDAO();
    private final ProdottoDAO prodottoDAO = new DemoProdottoDAO();
    private final RicettaDAO ricettaDAO = new DemoRicettaDAO();
    private final OrdineDAO ordineDAO = new DemoOrdineDAO();

    public DemoDAOFactory() {
        // Niente seed nel costruttore: i test partono da stato vuoto.
        // Il bootstrap demo chiama esplicitamente seedDemoData().
    }

    /** Carica dati dimostrativi (chiamata dal bootstrap in modalità DEMO). */
    public void seedDemoData() {
        try {
            seed();
        } catch (BusinessValidationException e) {
            throw new IllegalStateException("Seed demo corrotto", e);
        }
    }

    /** Carica dati dimostrativi: 4 utenti (tutti i ruoli), prodotti, ricette. */
    private void seed() throws BusinessValidationException {
        // Utente client
        Utente mario = new Utente("Mario", "mario@cibo.it", hash("password123"));
        mario.aggiungiRuolo(new RuoloCliente());
        utenteDAO.save(mario);

        // Venditore approvato
        Utente marco = new Utente("Marco", "marco@cibo.it", hash("password123"));
        RuoloVenditore rv = new RuoloVenditore("RM", "marco@cibo.it");
        rv.setStato(StatoVenditoreEnum.APPROVATO);
        marco.aggiungiRuolo(rv);
        utenteDAO.save(marco);

        // Nutrizionista
        Utente anna = new Utente("Anna", "anna@cibo.it", hash("password123"));
        anna.aggiungiRuolo(new RuoloNutrizionista());
        utenteDAO.save(anna);

        // Amministratore
        Utente admin = new Utente("Admin", "admin@cibo.it", hash("password123"));
        admin.aggiungiRuolo(new RuoloAmministratore());
        utenteDAO.save(admin);

        // Prodotti del venditore (marketplace)
        Prodotto miele = new Prodotto("Miele locale", 6.50, 20,
                LocalDate.now().plusMonths(6), UnitaEnum.PEZZI, rv);
        Prodotto pomodori = new Prodotto("Pomodori", 2.20, 50,
                LocalDate.now().plusDays(10), UnitaEnum.GRAMMI, rv);
        prodottoDAO.save(miele);
        prodottoDAO.save(pomodori);

        // Inventario del client (per il matching ricette)
        prodottoDAO.saveInventario("mario@cibo.it",
                new ProdottoInventario("Pasta", 1000, LocalDate.now().plusDays(200),
                        "Dispensa", UnitaEnum.GRAMMI, null));
        prodottoDAO.saveInventario("mario@cibo.it",
                new ProdottoInventario("Passata di pomodoro", 500, LocalDate.now().plusDays(30),
                        "Dispensa", UnitaEnum.GRAMMI, null));

        // Ricetta PROPOSTA del nutrizionista
        Ricetta ricetta = new Ricetta("Pasta al pomodoro", "Bollire la pasta e condire.",
                anna.getRuolo(RuoloNutrizionista.class));
        ricetta.aggiungiIngrediente(new Ingrediente(
                new Prodotto("Pasta", 1.0, 100, LocalDate.now().plusYears(1),
                        UnitaEnum.GRAMMI, null), 500, UnitaEnum.GRAMMI));
        ricetta.aggiungiIngrediente(new Ingrediente(
                new Prodotto("Passata di pomodoro", 1.0, 100, LocalDate.now().plusYears(1),
                        UnitaEnum.GRAMMI, null), 3, UnitaEnum.GRAMMI));
        ricettaDAO.save(ricetta);
    }

    /** Hash SHA-256 con salt fisso — identico ad AutenticazioneController. */
    private String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(("ciboamico-salt" + password)
                    .getBytes(StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(digest);
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public UtenteDAO getUtenteDAO() { return utenteDAO; }

    @Override
    public ProdottoDAO getProdottoDAO() { return prodottoDAO; }

    @Override
    public RicettaDAO getRicettaDAO() { return ricettaDAO; }

    @Override
    public OrdineDAO getOrdineDAO() { return ordineDAO; }
}
