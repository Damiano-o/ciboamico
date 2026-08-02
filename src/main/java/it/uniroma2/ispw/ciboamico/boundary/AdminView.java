package it.uniroma2.ispw.ciboamico.boundary;

import it.uniroma2.ispw.ciboamico.bean.RicettaBean;
import it.uniroma2.ispw.ciboamico.control.ApprovazioneController;
import it.uniroma2.ispw.ciboamico.persistence.factory.DAOFactory;
import it.uniroma2.ispw.ciboamico.bootstrap.ApplicationModeManager;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Boundary JavaFX — Amministratore (UC-08 Approva Venditore,
 * UC-09 Approva Ricetta). Scambia SOLO RicettaBean/String con il controller.
 */
public class AdminView {

    private final ApprovazioneController controller;

    public AdminView() {
        this.controller = new ApprovazioneController(
                ApplicationModeManager.getInstance().getDAOFactory());
    }

    public AdminView(DAOFactory factory) {
        this.controller = new ApprovazioneController(factory);
    }

    public Parent build() {
        Label titolo = new Label("Amministrazione (UC-08/09)");
        TextField emailVenditore = new TextField();
        emailVenditore.setPromptText("Email venditore");
        TextField nomeRicetta = new TextField();
        nomeRicetta.setPromptText("Nome ricetta in attesa");
        ListView<String> attesa = new ListView<>();
        Label messaggio = new Label();

        Button aggiorna = new Button("Ricette in attesa");
        aggiorna.setId("btn-attesa");
        aggiorna.setOnAction(e -> {
            List<RicettaBean> ricette = controller.ricetteInAttesa();
            attesa.getItems().setAll(ricette.stream()
                    .map(r -> r.getNome() + " — " + r.getStato())
                    .toList());
        });

        Button approvaVenditore = new Button("Approva venditore");
        approvaVenditore.setOnAction(e -> {
            try {
                controller.approvaVenditore(emailVenditore.getText(), true);
                messaggio.setText("Venditore approvato ✓");
            } catch (Exception ex) {
                messaggio.setText("Errore: " + ex.getMessage());
            }
        });

        Button approvaRicetta = new Button("Approva ricetta");
        approvaRicetta.setOnAction(e -> {
            try {
                controller.approvaRicetta(nomeRicetta.getText(), true);
                messaggio.setText("Ricetta approvata ✓");
                aggiorna.fire();
            } catch (Exception ex) {
                messaggio.setText("Errore: " + ex.getMessage());
            }
        });

        Button indietro = new Button("← Home");
        indietro.setOnAction(e -> Navigator.getInstance().switchTo("home"));

        VBox root = new VBox(10, titolo, aggiorna, attesa,
                new Label("Approva venditore:"), emailVenditore, approvaVenditore,
                new Label("Approva ricetta:"), nomeRicetta, approvaRicetta,
                messaggio, indietro);
        root.setPrefSize(900, 640);
        return root;
    }
}
