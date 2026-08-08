package it.uniroma2.ispw.ciboamico.boundary.cli;

import it.uniroma2.ispw.ciboamico.bean.UtenteBean;
import it.uniroma2.ispw.ciboamico.boundary.IView;

import java.util.HashMap;
import java.util.Map;

/**
 * Boundary CLI — Home (atterraggio post-login). Menu testuale che
 * instrada verso le altre view CLI (mappa nome → IView, ruolo-aware).
 */
public class HomeCLIView implements IView {

    private final CLIContext ctx;
    private final Map<String, IView> menu = new HashMap<>();

    public HomeCLIView(CLIContext ctx,
                       IView inventario, IView ricette, IView listaSpesa,
                       IView marketplace, IView catalogo, IView ordini,
                       IView nutrizionista, IView admin) {
        this.ctx = ctx;
        menu.put("inventario", inventario);
        menu.put("ricette", ricette);
        menu.put("lista-spesa", listaSpesa);
        menu.put("marketplace", marketplace);
        menu.put("catalogo", catalogo);
        menu.put("ordini", ordini);
        menu.put("crea-ricetta", nutrizionista);
        menu.put("admin", admin);
    }

    @Override
    public void display() {
        UtenteBean utente = ctx.getLoggedUser();
        System.out.println("\n=== CiboAmico — Home ===");
        System.out.println("Benvenuto, " + (utente != null ? utente.getUsername() : "ospite")
                + (utente != null ? " (ruolo: " + utente.getRuoloAttivo() + ")" : ""));
        System.out.println("[1] Dispensa e inventario");
        System.out.println("[2] Trova ricette compatibili");
        System.out.println("[3] Lista della spesa");
        System.out.println("[4] Marketplace locale");
        System.out.println("[5] Catalogo venditore");
        System.out.println("[6] Ordini ricevuti");
        System.out.println("[7] Crea ricetta (nutrizionista)");
        System.out.println("[8] Admin");
        System.out.println("[9] Esci");
        String scelta = ctx.leggiStringa("Scelta: ");

        switch (scelta) {
            case "1" -> menu.get("inventario").display();
            case "2" -> menu.get("ricette").display();
            case "3" -> menu.get("lista-spesa").display();
            case "4" -> menu.get("marketplace").display();
            case "5" -> menu.get("catalogo").display();
            case "6" -> menu.get("ordini").display();
            case "7" -> menu.get("crea-ricetta").display();
            case "8" -> menu.get("admin").display();
            case "9" -> System.out.println("Arrivederci!");
            default -> System.out.println("Scelta non valida.");
        }
    }
}
