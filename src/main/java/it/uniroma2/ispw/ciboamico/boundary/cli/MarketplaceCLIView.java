package it.uniroma2.ispw.ciboamico.boundary.cli;

import it.uniroma2.ispw.ciboamico.bean.OrdineBean;
import it.uniroma2.ispw.ciboamico.bean.ProdottoBean;
import it.uniroma2.ispw.ciboamico.bean.UtenteBean;
import it.uniroma2.ispw.ciboamico.boundary.IView;
import it.uniroma2.ispw.ciboamico.control.OrdinaProdottoController;

import java.util.List;

/**
 * Boundary CLI — Marketplace (UC-06). Mostra i prodotti disponibili e
 * crea un ordine tramite OrdinaProdottoController (scambio Solo Bean).
 */
public class MarketplaceCLIView implements IView {

    private final CLIContext ctx;
    private final OrdinaProdottoController controller;

    public MarketplaceCLIView(CLIContext ctx) {
        this.ctx = ctx;
        this.controller = new OrdinaProdottoController(ctx.getFactory());
    }

    @Override
    public void display() {
        UtenteBean utente = ctx.getLoggedUser();
        if (utente == null) {
            System.out.println("Nessun utente loggato.");
            return;
        }
        System.out.println("\n=== Marketplace (UC-06) ===");
        List<ProdottoBean> prodotti = controller.getProdottiDisponibili();
        if (prodotti.isEmpty()) {
            System.out.println("Nessun prodotto in vendita.");
            return;
        }
        for (ProdottoBean p : prodotti) {
            System.out.printf("- %s — %.2f EUR — %.0f disponibili%n",
                    p.getNome(), p.getPrezzo(), p.getQuantita());
        }
        String nomeProdotto = ctx.leggiStringa("Nome prodotto da ordinare (invio = annulla): ");
        if (nomeProdotto.isEmpty()) {
            return;
        }
        try {
            OrdineBean bean = new OrdineBean();
            bean.setNomeProdotto(nomeProdotto);
            OrdineBean risultato = controller.submitOrdine(bean, utente);
            System.out.printf("Ordine creato ✓ — stato %s, totale %.2f EUR%n",
                    risultato.getStato(), risultato.getTotale());
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }
}
