package it.uniroma2.ispw.ciboamico.boundary.cli;

import it.uniroma2.ispw.ciboamico.bean.ProdottoBean;
import it.uniroma2.ispw.ciboamico.boundary.IView;
import it.uniroma2.ispw.ciboamico.control.GestisciCatalogoVenditoreController;

import java.time.LocalDate;

/**
 * Boundary CLI — Catalogo Venditore (UC-07). Pubblica un prodotto nel
 * marketplace locale tramite GestisciCatalogoVenditoreController.
 */
public class CatalogoVenditoreCLIView implements IView {

    private final CLIContext ctx;
    private final GestisciCatalogoVenditoreController controller;

    public CatalogoVenditoreCLIView(CLIContext ctx) {
        this.ctx = ctx;
        this.controller = new GestisciCatalogoVenditoreController(ctx.getFactory());
    }

    @Override
    public void display() {
        System.out.println("\n=== Catalogo Venditore (UC-07) ===");
        try {
            ProdottoBean bean = new ProdottoBean();
            bean.setNome(ctx.leggiStringa("Nome prodotto: "));
            bean.setPrezzo(ctx.leggiDouble("Prezzo (EUR): "));
            bean.setQuantita(ctx.leggiDouble("Quantità disponibile: "));
            System.out.print("Scadenza (YYYY-MM-DD, invio = oggi): ");
            String data = ctx.leggiStringa("");
            bean.setScadenza(data.isEmpty() ? LocalDate.now() : LocalDate.parse(data));
            System.out.print("Unità (PEZZI/GRAMMI/LITRI, invio = PEZZI): ");
            String unita = ctx.leggiStringa("");
            bean.setUnitaMisura(unita.isEmpty() ? "PEZZI" : unita);
            controller.pubblicaProdotto(bean);
            System.out.println("Prodotto pubblicato ✓");
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }
}
