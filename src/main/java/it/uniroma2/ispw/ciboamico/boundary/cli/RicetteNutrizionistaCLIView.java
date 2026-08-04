package it.uniroma2.ispw.ciboamico.boundary.cli;

import it.uniroma2.ispw.ciboamico.bean.RicettaBean;
import it.uniroma2.ispw.ciboamico.boundary.IView;
import it.uniroma2.ispw.ciboamico.control.GestisciRicetteNutrizionistaController;

import java.util.ArrayList;
import java.util.List;

/**
 * Boundary CLI — Ricette Nutrizionista (UC-08). Crea una ricetta con
 * ingredienti e dosi tramite GestisciRicetteNutrizionistaController.
 */
public class RicetteNutrizionistaCLIView implements IView {

    private final CLIContext ctx;
    private final GestisciRicetteNutrizionistaController controller;

    public RicetteNutrizionistaCLIView(CLIContext ctx) {
        this.ctx = ctx;
        this.controller = new GestisciRicetteNutrizionistaController(ctx.getFactory());
    }

    @Override
    public void display() {
        System.out.println("\n=== Ricette Nutrizionista (UC-08) ===");
        try {
            RicettaBean bean = new RicettaBean();
            bean.setNome(ctx.leggiStringa("Nome ricetta: "));
            bean.setIstruzioni(ctx.leggiStringa("Istruzioni: "));
            List<String> nomi = new ArrayList<>(List.of(ctx.leggiStringa(
                    "Ingredienti separati da virgola (min 2): ").split(",")));
            bean.setIngredientiNomi(nomi);
            List<Double> dosi = new ArrayList<>();
            for (String d : ctx.leggiStringa("Dosi separati da virgola (es. 500,3): ").split(",")) {
                dosi.add(Double.parseDouble(d.trim()));
            }
            bean.setDosi(dosi);
            controller.creaRicetta(bean);
            System.out.println("Ricetta creata ✓ (stato PROPOSTA)");
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }
}
