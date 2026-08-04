package it.uniroma2.ispw.ciboamico.boundary.cli;

import it.uniroma2.ispw.ciboamico.bean.RicettaBean;
import it.uniroma2.ispw.ciboamico.boundary.IView;
import it.uniroma2.ispw.ciboamico.control.ApprovazioneController;

import java.util.List;

/**
 * Boundary CLI — Admin (UC-09/UC-12). Approva venditori (UC-12) e
 * ricette in attesa (UC-09) tramite ApprovazioneController.
 */
public class AdminCLIView implements IView {

    private final CLIContext ctx;
    private final ApprovazioneController controller;

    public AdminCLIView(CLIContext ctx) {
        this.ctx = ctx;
        this.controller = new ApprovazioneController(ctx.getFactory());
    }

    @Override
    public void display() {
        System.out.println("\n=== Admin (UC-09/UC-12) ===");
        List<RicettaBean> attesa = controller.ricetteInAttesa();
        if (attesa.isEmpty()) {
            System.out.println("Nessuna ricetta in attesa.");
        } else {
            System.out.println("Ricette in attesa:");
            for (RicettaBean r : attesa) {
                System.out.println("- " + r.getNome() + " — " + r.getStato());
            }
        }
        if (ctx.leggiSiNo("Approvare una ricetta?")) {
            String nome = ctx.leggiStringa("Nome ricetta: ");
            controller.approvaRicetta(nome, true);
            System.out.println("Ricetta approvata ✓");
        }
        if (ctx.leggiSiNo("Approvare un venditore?")) {
            String email = ctx.leggiStringa("Email venditore: ");
            controller.approvaVenditore(email, true);
            System.out.println("Venditore approvato ✓");
        }
    }
}
