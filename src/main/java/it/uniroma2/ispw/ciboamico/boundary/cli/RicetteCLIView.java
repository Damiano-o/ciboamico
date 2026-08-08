package it.uniroma2.ispw.ciboamico.boundary.cli;

import it.uniroma2.ispw.ciboamico.bean.RicettaBean;
import it.uniroma2.ispw.ciboamico.bean.UtenteBean;
import it.uniroma2.ispw.ciboamico.boundary.IView;
import it.uniroma2.ispw.ciboamico.control.TrovaRicetteController;
import it.uniroma2.ispw.ciboamico.pattern.facade.RicettaMatchingFacade;

import java.util.List;

/**
 * Boundary CLI — Trova Ricette Compatibili (UC-02). Stesso flusso della
 * RicetteView JavaFX: il matching avviene nel Facade (Strategy interna).
 */
public class RicetteCLIView implements IView {

    private final CLIContext ctx;
    private final TrovaRicetteController controller;

    public RicetteCLIView(CLIContext ctx) {
        this.ctx = ctx;
        this.controller = new TrovaRicetteController(ctx.getFactory(),
                RicettaMatchingFacade.conSostituzione());
    }

    @Override
    public void display() {
        UtenteBean utente = ctx.getLoggedUser();
        if (utente == null) {
            System.out.println("Nessun utente loggato.");
            return;
        }
        System.out.println("\n=== Ricette compatibili (UC-02) ===");
        List<RicettaBean> ricette = controller.findCompatible(utente.getEmail());
        if (ricette.isEmpty()) {
            System.out.println("Nessuna ricetta compatibile con la tua dispensa.");
            return;
        }
        for (RicettaBean r : ricette) {
            System.out.println("- " + r.getNome() + " (" + r.getIngredientiNomi().size() + " ingredienti)");
        }
    }
}
