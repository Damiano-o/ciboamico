package it.uniroma2.ispw.ciboamico.boundary;

import it.uniroma2.ispw.ciboamico.bean.RicettaBean;
import it.uniroma2.ispw.ciboamico.bean.UtenteBean;
import it.uniroma2.ispw.ciboamico.control.TrovaRicetteController;
import it.uniroma2.ispw.ciboamico.pattern.facade.RicettaMatchingFacade;
import it.uniroma2.ispw.ciboamico.pattern.singleton.SessionManager;
import it.uniroma2.ispw.ciboamico.persistence.factory.DAOFactory;
import it.uniroma2.ispw.ciboamico.bootstrap.ApplicationModeManager;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Boundary JavaFX — Trova Ricette Compatibili (UC-02).
 * Scambia SOLO RicettaBean con il controller (Facade + Strategy interna).
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
        Label titolo = new Label("Ricette compatibili (UC-02)");
        ListView<String> elenco = new ListView<>();
        Label messaggio = new Label();

        Button trova = new Button("Trova ricette");
        trova.setOnAction(e -> {
            List<RicettaBean> ricette = controller.findCompatible(utente.getEmail());
            if (ricette.isEmpty()) {
                messaggio.setText("Nessuna ricetta compatibile con l'inventario.");
            } else {
                messaggio.setText(ricette.size() + " ricette compatibili trovate:");
            }
            elenco.getItems().setAll(ricette.stream()
                    .map(r -> r.getNome() + " — " + String.join(", ", r.getIngredientiNomi()))
                    .toList());
        });

        Button indietro = new Button("← Home");
        indietro.setOnAction(e -> Navigator.getInstance().switchTo("home"));

        VBox root = new VBox(10, titolo, trova, messaggio, elenco, indietro);
        root.setPrefSize(900, 640);
        return root;
    }
}
