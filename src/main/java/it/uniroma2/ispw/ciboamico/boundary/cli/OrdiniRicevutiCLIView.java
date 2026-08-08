package it.uniroma2.ispw.ciboamico.boundary.cli;

import it.uniroma2.ispw.ciboamico.bean.OrdineBean;
import it.uniroma2.ispw.ciboamico.bean.UtenteBean;
import it.uniroma2.ispw.ciboamico.boundary.IView;
import it.uniroma2.ispw.ciboamico.control.GestisciOrdiniRicevutiController;

import java.util.List;

/**
 * Boundary CLI — Ordini Ricevuti (UC-10). Visualizza gli ordini del
 * venditore e aggiorna lo stato tramite GestisciOrdiniRicevutiController.
 */
public class OrdiniRicevutiCLIView implements IView {

    private final CLIContext ctx;
    private final GestisciOrdiniRicevutiController controller;

    public OrdiniRicevutiCLIView(CLIContext ctx) {
        this.ctx = ctx;
        this.controller = new GestisciOrdiniRicevutiController(ctx.getFactory());
    }

    @Override
    public void display() {
        UtenteBean utente = ctx.getLoggedUser();
        if (utente == null) {
            System.out.println("Nessun utente loggato.");
            return;
        }
        System.out.println("\n=== Ordini Ricevuti (UC-10) ===");
        List<OrdineBean> ordini = controller.visualizzaOrdiniRicevuti(utente.getEmail());
        if (ordini.isEmpty()) {
            System.out.println("Nessun ordine ricevuto.");
            return;
        }
        for (OrdineBean o : ordini) {
            System.out.printf("- Ordine %d — %s — %.2f EUR%n",
                    o.getIdOrdine(), o.getStato(), o.getTotale());
        }
        if (ctx.leggiSiNo("Vuoi aggiornare lo stato di un ordine?")) {
            try {
                Long id = ctx.leggiLong("ID ordine: ");
                List<String> stati = controller.getStatiAggiornabili();
                System.out.println("Stati disponibili: " + stati);
                String nuovoStato = ctx.leggiStringa("Nuovo stato: ");
                OrdineBean o = controller.aggiornaStato(id, nuovoStato);
                System.out.println("Stato aggiornato → " + o.getStato());
            } catch (Exception e) {
                System.out.println("Errore: " + e.getMessage());
            }
        }
    }
}
