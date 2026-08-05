package it.uniroma2.ispw.ciboamico.boundary;

import it.uniroma2.ispw.ciboamico.bean.RicettaBean;
import it.uniroma2.ispw.ciboamico.bean.UtenteBean;
import it.uniroma2.ispw.ciboamico.control.TrovaRicetteController;
import it.uniroma2.ispw.ciboamico.pattern.facade.RicettaMatchingFacade;
import it.uniroma2.ispw.ciboamico.pattern.singleton.SessionManager;
import it.uniroma2.ispw.ciboamico.persistence.factory.DAOFactory;
import it.uniroma2.ispw.ciboamico.bootstrap.ApplicationModeManager;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Boundary JavaFX — Trova Ricette Compatibili (UC-02).
 * Scambia SOLO RicettaBean con il controller (Facade + Strategy interna).
 * UI minimalista: bottone trova + griglia di card con ingredienti.
 */
public class RicetteView {

    private final TrovaRicetteController controller;

    public RicetteView() {
        this.controller = new TrovaRicetteController(
                ApplicationModeManager.getInstance().getDAOFactory(),
                RicettaMatchingFacade.conSostituzione());
    }

    public RicetteView(DAOFactory factory) {
        this.controller = new TrovaRicetteController(factory,
                RicettaMatchingFacade.conSostituzione());
    }

    public Parent build() {
        UtenteBean utente = SessionManager.getInstance().getLoggedUser();
        Button trova = new Button("Trova ricette compatibili");
        trova.setId("btn-trova");
        trova.setMaxWidth(Double.MAX_VALUE);

        Label messaggio = new Label("Premi il pulsante per cercare le ricette "
                + "compatibili con la tua dispensa.");
        messaggio.getStyleClass().add("page-subtitle");
        messaggio.setWrapText(true);

        FlowPane griglia = new FlowPane(16, 16);
        griglia.setPadding(new Insets(4, 0, 20, 0));

        trova.setOnAction(e -> {
            List<RicettaBean> ricette =
                    controller.findCompatible(utente.getEmail());
            griglia.getChildren().clear();
            if (ricette.isEmpty()) {
                messaggio.setText("Nessuna ricetta compatibile con l'inventario.");
            } else {
                messaggio.setText(ricette.size() + " ricette compatibili trovate:");
            }
            ricette.forEach(r -> griglia.getChildren()
                    .add(UiKit.card(r.getNome(),
                            "Ingredienti: " + String.join(", ", r.getIngredientiNomi()),
                            r.getStato(), "badge-ok")));
        });

        VBox corpo = new VBox(10, trova, messaggio, griglia);
        corpo.setPadding(new Insets(16, 0, 0, 0));
        return UiKit.pagina("Ricette compatibili", "UC-02 · usa la tua dispensa", corpo, "ricette");
    }
}