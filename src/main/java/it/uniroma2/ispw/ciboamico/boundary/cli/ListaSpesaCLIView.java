package it.uniroma2.ispw.ciboamico.boundary.cli;

import it.uniroma2.ispw.ciboamico.bean.ProdottoBean;
import it.uniroma2.ispw.ciboamico.bean.UtenteBean;
import it.uniroma2.ispw.ciboamico.boundary.IView;
import it.uniroma2.ispw.ciboamico.control.GestisciListaSpesaController;

import java.util.List;

/**
 * Boundary CLI — Lista della Spesa (UC-05). Calcola le mancanze di una
 * ricetta rispetto alla dispensa dell'utente (FR-10).
 */
public class ListaSpesaCLIView implements IView {

    private final CLIContext ctx;
    private final GestisciListaSpesaController controller;

    public ListaSpesaCLIView(CLIContext ctx) {
        this.ctx = ctx;
        this.controller = new GestisciListaSpesaController(ctx.getFactory());
    }

    @Override
    public void display() {
        UtenteBean utente = ctx.getLoggedUser();
        if (utente == null) {
            System.out.println("Nessun utente loggato.");
            return;
        }
        System.out.println("\n=== Lista della Spesa (UC-05) ===");
        String nomeRicetta = ctx.leggiStringa("Nome ricetta: ");
        List<ProdottoBean> mancanti = controller.calcolaMancanze(utente.getEmail(), nomeRicetta);
        if (mancanti.isEmpty()) {
            System.out.println("Tutto disponibile: nessun acquisto necessario ✓");
            return;
        }
        System.out.println("Mancano " + mancanti.size() + " ingredienti:");
        for (ProdottoBean p : mancanti) {
            System.out.printf("- %s — %.2f %s%n", p.getNome(), p.getQuantita(), p.getUnitaMisura());
        }
    }
}
