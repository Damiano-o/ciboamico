package it.uniroma2.ispw.ciboamico.control;

import it.uniroma2.ispw.ciboamico.bean.OrdineBean;
import it.uniroma2.ispw.ciboamico.bean.ProdottoBean;
import it.uniroma2.ispw.ciboamico.bean.UtenteBean;
import it.uniroma2.ispw.ciboamico.entity.*;
import it.uniroma2.ispw.ciboamico.exception.BusinessValidationException;
import java.util.List;
import it.uniroma2.ispw.ciboamico.pattern.factory.OrdineLazyFactory;
import it.uniroma2.ispw.ciboamico.pattern.observer.VenditoreNotifier;
import it.uniroma2.ispw.ciboamico.pattern.payment.PaymentGateway;
import it.uniroma2.ispw.ciboamico.pattern.payment.PaymentGatewayFactory;
import it.uniroma2.ispw.ciboamico.pattern.payment.PaymentRejectedException;
import it.uniroma2.ispw.ciboamico.bean.PaymentInfoBean;
import it.uniroma2.ispw.ciboamico.persistence.dao.BuonoDAO;
import it.uniroma2.ispw.ciboamico.persistence.dao.OrdineDAO;
import it.uniroma2.ispw.ciboamico.persistence.dao.ProdottoDAO;
import it.uniroma2.ispw.ciboamico.persistence.factory.DAOFactory;

/**
 * Control di UC-04 Ordina Prodotto (1:1 con l'use case).
 * Stateless: l'utente corrente viene ricevuto dalla view; il confine scambia
 * solo bean con il controller.
 */
public class OrdinaProdottoController {

    private final OrdineDAO ordineDAO;
    private final ProdottoDAO prodottoDAO;
    private final BuonoDAO buonoDAO;
    private final it.uniroma2.ispw.ciboamico.persistence.dao.UtenteDAO utenteDAO;
    private final PaymentGateway paymentGateway;

    public OrdinaProdottoController(DAOFactory factory) {
        this.ordineDAO = factory.getOrdineDAO();
        this.prodottoDAO = factory.getProdottoDAO();
        this.buonoDAO = factory.getBuonoDAO();
        this.utenteDAO = factory.getUtenteDAO();
        // Gateway di pagamento (passo 6 UC-04) risolto da factory dedicata (High
        // Cohesion): il pagamento NON è persistenza, quindi non vive nella DAOFactory.
        this.paymentGateway = PaymentGatewayFactory.createGateway();
    }

    /** Costruttore no-arg usato dall'applicazione; la factory attiva viene
     *  risolta dal gestore della modalità. Il costruttore con factory resta
     *  disponibile per i test e l'iniezione esplicita delle dipendenze. */
    public OrdinaProdottoController() {
        this(it.uniroma2.ispw.ciboamico.bootstrap.ApplicationModeManager.getInstance().getDAOFactory());
    }

    /**
     * Catalogo del marketplace come Bean (BCE: la boundary non tocca i DAO).
     */
    public List<ProdottoBean> getProdottiDisponibili() {
        return prodottoDAO.findAll().stream()
                .map(p -> {
                    ProdottoBean bean = new ProdottoBean();
                    bean.setNome(p.getNome());
                    bean.setPrezzo(p.getPrezzo());
                    bean.setQuantita((double) p.getQuantitaDisponibile());
                    return bean;
                })
                .toList();
    }

    /**
     * Flusso UC-04: verifica disponibilità → riepilogo → conferma → ordine CREATED + notifica.
     */
    OrdineBean submitOrdine(OrdineBean bean, UtenteBean utente)
            throws BusinessValidationException {
        if (utente == null) {
            throw new IllegalStateException("Utente non autenticato");
        }
        if (bean == null || bean.getNomeProdotto() == null || bean.getNomeProdotto().isBlank()) {
            throw new IllegalArgumentException("Prodotto non selezionato");
        }

        // La boundary seleziona il prodotto per nome (lookup esplicito, bean-only)
        Prodotto prodotto = prodottoDAO.findByNome(bean.getNomeProdotto());
        if (prodotto == null) {
            throw new IllegalStateException("Prodotto non disponibile (2.c)");
        }

        // Estensione 2a (out of stock): la Entity verifica la quantità richiesta
        // e riduce la disponibilità — BusinessValidationException se insufficiente.
        prodotto.riduciDisponibilita(1);
        prodottoDAO.update(prodotto);

        // Costruzione ordine singolo diretto (D-03)
        // Il venditore è risolto dal PRODOTTO acquistato: risalgo all'Utente
        // proprietario del ruolo Venditore; se il prodotto non espone un
        // proprietario, usa il compratore come fallback coerente col modello demo.
        Utente compratore = new Utente(utente.getUsername(), utente.getEmail(), "");
        Utente venditore = prodotto.getVenditore() != null && prodotto.getVenditore().getUtente() != null
                ? prodotto.getVenditore().getUtente()
                : compratore;

        // L'id è assegnato dal DAO (Information Expert) tramite la LazyFactory con cache
        Ordine ordine = OrdineLazyFactory.getInstance().newOrdine(compratore, venditore);
        ordine.subscribe(new VenditoreNotifier()); // Observer: notifica venditore

        VoceOrdine voce = new VoceOrdine(prodotto, 1);
        ordine.aggiungiVoce(voce);

        ordineDAO.save(ordine);

        // Dopo la persistenza: emette la notifica asincrona al venditore (Pattern Observer).
        // L'ordine resta in stato CREATED; la sottomissione consolidata su DB è l'evento
        // di dominio che attiva il listener del venditore.
        ordine.notificaSottomissione();

        OrdineBean risultato = new OrdineBean();
        risultato.setIdOrdine(ordine.getIdOrdine());
        risultato.setTotale(ordine.getTotale());
        risultato.setStato(ordine.getStato().name());
        return risultato;
    }

    /**
     * Flusso con pagamento (passo 6 + estensione 6a UC-04): chiede al
     * {@link PaymentGateway} l'autorizzazione all'addebito. Se rifiutata
     * lancia {@link BusinessValidationException} PRIMA di toccare scorte,
     * creare l'ordine o notificare il venditore (nessun ordine "fantasma").
     * Se autorizzato, procede con la logica di submit esistente.
     *
     * @return OrdineBean con l'esito della transazione
     */
    public OrdineBean processaPagamento(OrdineBean bean, UtenteBean utente, PaymentInfoBean payment)
            throws BusinessValidationException {
        if (payment == null || payment.getImportoInCent() <= 0) {
            throw new BusinessValidationException("Dati di pagamento non validi");
        }
        try {
            paymentGateway.autorizza(payment.getImportoInCent());
        } catch (PaymentRejectedException e) {
            throw new BusinessValidationException(e.getMessage());
        }
        return submitOrdine(bean, utente);
    }

    /**
     * Estensione 4a (buono promozionale): applica un buono valido all'ordine corrente.
     * Il controller coordina (GRASP Controller):
     * 1) carica il buono dal DAO; 2) valida esistenza/scadenza/appartenenza-venditore/monouso;
     * 3) delega l'applicazione e il ricalcolo all'entità Ordine (Information Expert).
     *
     * @param codiceBuono codice del buono da applicare
     * @param bean         ordine corrente (per risalire al prodotto/venditore)
     * @param utente       utente autenticato (per il monouso)
     * @return OrdineBean con il totale aggiornato dopo lo sconto
     * @throws BusinessValidationException se il buono non è valido per questo ordine
     */
    public OrdineBean applicaBuonoPromozionale(String codiceBuono, OrdineBean bean, UtenteBean utente)
            throws BusinessValidationException {
        if (codiceBuono == null || codiceBuono.isBlank()) {
            throw new BusinessValidationException("Codice buono mancante");
        }
        if (utente == null) {
            throw new IllegalStateException("Utente non autenticato");
        }
        if (bean == null || bean.getNomeProdotto() == null || bean.getNomeProdotto().isBlank()) {
            throw new IllegalArgumentException("Nessun prodotto nell'ordine");
        }

        // 1) Carica il buono (DAO lookup per codice)
        BuonoPromozionale buono = buonoDAO.findByCodice(codiceBuono);
        if (buono == null) {
            throw new BusinessValidationException("Buono promozionale non valido");
        }

        // 2a) Validazione temporale (scadenza)
        if (!buono.isValido()) {
            throw new BusinessValidationException("Buono promozionale scaduto o non ancora attivo");
        }

        // 2b) Validazione monouso (l'utente non deve averlo già riscattato)
        if (utente.getEmail() != null && haGiaUsatoBuono(utente.getEmail(), codiceBuono)) {
            throw new BusinessValidationException("Buono già utilizzato da questo utente");
        }

        // 2c) Validazione di appartenenza: il buono deve essere del venditore del prodotto
        Prodotto prodotto = prodottoDAO.findByNome(bean.getNomeProdotto());
        if (prodotto == null || prodotto.getVenditore() == null
                || prodotto.getVenditore().getUtente() == null) {
            throw new IllegalStateException("Prodotto o venditore non risolvibile");
        }
        String emailVenditoreBuono = buono.getVenditore().getUtente() != null
                ? buono.getVenditore().getUtente().getEmail()
                : buono.getVenditore().getRecapito();
        String emailVenditoreOrdine = prodotto.getVenditore().getUtente().getEmail();
        if (!emailVenditoreBuono.equals(emailVenditoreOrdine)) {
            throw new BusinessValidationException(
                    "Il buono promozionale non appartiene al venditore di questo prodotto");
        }

        // 3) Calcola e applica (Ordine = Context dello Strategy); qui costruisco un ordine
        // rappresentativo per ricalcolare il totale scontato del prodotto selezionato.
        Utente compratore = new Utente(utente.getUsername(), utente.getEmail(), "");
        Utente venditore = prodotto.getVenditore().getUtente();
        Ordine ordine = OrdineLazyFactory.getInstance().newOrdine(compratore, venditore);
        ordine.aggiungiVoce(new VoceOrdine(prodotto, 1));
        ordine.applicaBuono(buono);   // applica sconto + ricalcola (Information Expert)

        // Marca il buono come usato (monouso) sull'utente autenticato
        if (utente.getEmail() != null) {
            Utente persona = utenteDAO.findByEmail(utente.getEmail());
            if (persona != null) {
                persona.registraBuonoUtilizzato(buono.getCodice());
                utenteDAO.save(persona);
            }
        }

        OrdineBean risultato = new OrdineBean();
        risultato.setIdOrdine(bean.getIdOrdine());
        risultato.setNomeProdotto(bean.getNomeProdotto());
        risultato.setTotale(ordine.getTotale());
        risultato.setCodiceBuono(buono.getCodice());
        return risultato;
    }

    /** Recupera l'Utente dal DAO e verifica se ha già usato il buono (monouso). */
    private boolean haGiaUsatoBuono(String email, String codiceBuono) {
        Utente u = utenteDAO.findByEmail(email);
        return u != null && u.haUsatoBuono(codiceBuono);
    }
}
