package it.uniroma2.ispw.ciboamico.boundary.cli;

import it.uniroma2.ispw.ciboamico.bean.OrdineBean;
import it.uniroma2.ispw.ciboamico.bean.PaymentInfoBean;
import it.uniroma2.ispw.ciboamico.bean.UtenteBean;
import it.uniroma2.ispw.ciboamico.boundary.IView;
import it.uniroma2.ispw.ciboamico.control.OrdinaProdottoController;
import it.uniroma2.ispw.ciboamico.pattern.singleton.SessionManager;

/**
 * Boundary CLI — Pagamento (passo 6 + estensione 6a UC-04).
 * Legge il prodotto in checkout da {@link SessionManager#getOrdineInCorso()},
 * raccoglie i dati carta e invoca processaPagamento via Bean.
 */
public class PaymentCLIView implements IView {

    private final CLIContext ctx;
    private final OrdinaProdottoController controller;

    public PaymentCLIView(CLIContext ctx) {
        this.ctx = ctx;
        this.controller = new OrdinaProdottoController(ctx.getFactory());
    }

    @Override
    public void display() {
        UtenteBean utente = ctx.getLoggedUser();
        OrdineBean ordine = SessionManager.getInstance().getOrdineInCorso();
        if (utente == null || ordine == null) {
            System.out.println("Sessione o ordine in checkout non disponibile.");
            return;
        }
        System.out.println("\n=== Pagamento (UC-04) ===");
        System.out.printf("Prodotto: %s — %.2f EUR%n", ordine.getNomeProdotto(), ordine.getTotale());
        String numero = ctx.leggiStringa("Numero carta (invio = 0000000000000000): ");
        String cvv = ctx.leggiStringa("CVV (3 cifre): ");
        if (numero.isEmpty()) {
            numero = "0000000000000000";
        }
        PaymentInfoBean payment = new PaymentInfoBean();
        payment.setNumeroCarta(numero.trim());
        payment.setIntestatario(ctx.getLoggedUser() != null ? ctx.getLoggedUser().getUsername() : "");
        payment.setScadenza("12/29");
        payment.setCvv(cvv.isBlank() ? "000" : cvv.trim());
        payment.setImportoInCent(Math.round(ordine.getTotale() * 100));

        try {
            OrdineBean risultato = controller.processaPagamento(ordine, utente, payment);
            SessionManager.getInstance().setOrdineInCorso(null);
            System.out.printf("Pagamento riuscito ✓ — ordine %s, totale %.2f EUR%n",
                    risultato.getStato(), risultato.getTotale());
        } catch (Exception e) {
            System.out.println("Pagamento negato: " + e.getMessage());
        }
    }
}