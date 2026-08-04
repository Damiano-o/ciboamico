package it.uniroma2.ispw.ciboamico.boundary.cli;

import it.uniroma2.ispw.ciboamico.bean.ProdottoBean;
import it.uniroma2.ispw.ciboamico.bean.UtenteBean;
import it.uniroma2.ispw.ciboamico.boundary.IView;
import it.uniroma2.ispw.ciboamico.control.GestisciInventarioController;

import java.time.LocalDate;
import java.util.List;

/**
 * Boundary CLI — Dispensa/Inventario (UC-01). Stesso flusso della
 * InventarioView JavaFX: aggiunge prodotto (FR-01), mostra inventario
 * ordinato (FR-02) e avvisi di scadenza (FR-03).
 */
public class InventarioCLIView implements IView {

    private final CLIContext ctx;
    private final GestisciInventarioController controller;

    public InventarioCLIView(CLIContext ctx) {
        this.ctx = ctx;
        this.controller = new GestisciInventarioController(ctx.getFactory());
    }

    @Override
    public void display() {
        UtenteBean utente = ctx.getLoggedUser();
        if (utente == null) {
            System.out.println("Nessun utente loggato.");
            return;
        }
        System.out.println("\n=== Dispensa e Inventario (UC-01) ===");
        System.out.println("[1] Visualizza inventario ordinato");
        System.out.println("[2] Prodotti in scadenza");
        System.out.println("[3] Aggiungi prodotto");
        System.out.println("[4] Indietro");
        String scelta = ctx.leggiStringa("Scelta: ");

        switch (scelta) {
            case "1" -> visualizzaInventario(utente.getEmail());
            case "2" -> prodottiInScadenza(utente.getEmail());
            case "3" -> aggiungiProdotto(utente.getEmail());
            default -> { /* indietro */ }
        }
    }

    private void visualizzaInventario(String email) {
        List<ProdottoBean> prodotti = controller.visualizzaInventarioOrdinato(email);
        if (prodotti.isEmpty()) {
            System.out.println("Inventario vuoto.");
            return;
        }
        for (ProdottoBean p : prodotti) {
            System.out.printf("- %s | %.2f %s | scad. %s | %s%n",
                    p.getNome(), p.getQuantita(), p.getUnitaMisura(),
                    p.getScadenza(), p.getPosizione());
        }
    }

    private void prodottiInScadenza(String email) {
        List<ProdottoBean> inScadenza = controller.prodottiInScadenza(email);
        if (inScadenza.isEmpty()) {
            System.out.println("Nessun prodotto in scadenza nei prossimi 7 giorni.");
            return;
        }
        System.out.println("⚠ Prodotti in scadenza:");
        for (ProdottoBean p : inScadenza) {
            System.out.printf("- %s (scad. %s)%n", p.getNome(), p.getScadenza());
        }
    }

    private void aggiungiProdotto(String email) {
        try {
            ProdottoBean bean = new ProdottoBean();
            bean.setNome(ctx.leggiStringa("Nome prodotto: "));
            bean.setQuantita(ctx.leggiDouble("Quantità: "));
            System.out.print("Scadenza (YYYY-MM-DD, invio = tra 7 giorni): ");
            String data = ctx.leggiStringa("");
            bean.setScadenza(data.isEmpty() ? LocalDate.now().plusDays(7) : LocalDate.parse(data));
            bean.setPosizione(ctx.leggiStringa("Posizione (Frigo/Dispensa): "));
            System.out.print("Unità (PEZZI/GRAMMI/LITRI/ML, invio = PEZZI): ");
            String unita = ctx.leggiStringa("");
            bean.setUnitaMisura(unita.isEmpty() ? "PEZZI" : unita);
            controller.aggiungiProdotto(bean, email);
            System.out.println("Prodotto aggiunto ✓");
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }
}
