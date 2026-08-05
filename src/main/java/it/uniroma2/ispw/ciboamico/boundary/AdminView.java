package it.uniroma2.ispw.ciboamico.boundary;

import it.uniroma2.ispw.ciboamico.bean.RicettaBean;
import it.uniroma2.ispw.ciboamico.control.ApprovazioneController;
import it.uniroma2.ispw.ciboamico.persistence.factory.DAOFactory;
import it.uniroma2.ispw.ciboamico.bootstrap.ApplicationModeManager;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Boundary JavaFX — Amministratore (UC-08 Approva Venditore,
 * UC-09 Approva Ricetta). Scambia SOLO RicettaBean/String con il controller.
 * UI: pannelli di approvazione in card + lista attese.
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
        Label messaggio = new Label("Gestisci venditori e ricette in attesa di approvazione.");
        messaggio.getStyleClass().add("page-subtitle");
        messaggio.setWrapText(true);

        Button aggiorna = new Button("Ricette in attesa");
        aggiorna.setId("btn-attesa");
        aggiorna.setMaxWidth(Double.MAX_VALUE);

        FlowPane attesa = new FlowPane(16, 16);
        attesa.setPadding(new Insets(4, 0, 20, 0));

        // Approva venditore
        TextField emailVenditore = new TextField();
        emailVenditore.setPromptText("Email venditore");
        Button approvaVenditore = new Button("Approva venditore");
        Label msgVenditore = new Label();

        // Approva ricetta
        TextField nomeRicetta = new TextField();
        nomeRicetta.setPromptText("Nome ricetta in attesa");
        Button approvaRicetta = new Button("Approva ricetta");
        Label msgRicetta = new Label();

        approvaVenditore.setOnAction(e -> {
            try {
                controller.approvaVenditore(emailVenditore.getText(), true);
                msgVenditore.setText("✓ Venditore approvato.");
            } catch (Exception ex) {
                msgVenditore.setText("Errore: " + ex.getMessage());
            }
        });

        approvaRicetta.setOnAction(e -> {
            try {
                controller.approvaRicetta(nomeRicetta.getText(), true);
                msgRicetta.setText("✓ Ricetta approvata.");
                aggiorna.fire();
            } catch (Exception ex) {
                msgRicetta.setText("Errore: " + ex.getMessage());
            }
        });

        aggiorna.setOnAction(e -> {
            List<RicettaBean> ricette = controller.ricetteInAttesa();
            attesa.getChildren().clear();
            if (ricette.isEmpty()) {
                attesa.getChildren().add(UiKit.card("Nessuna ricetta in attesa",
                        "Tutte le ricette sono state gestite."));
            }
            ricette.forEach(r -> attesa.getChildren().add(
                    UiKit.card(r.getNome(), "Stato: " + r.getStato())));
        });

        VBox pvs = new VBox(8, UiKit.field("Email venditore"), emailVenditore,
                approvaVenditore, msgVenditore);
        pvs.getStyleClass().add("form-panel");
        VBox prc = new VBox(8, UiKit.field("Nome ricetta in attesa"), nomeRicetta,
                approvaRicetta, msgRicetta);
        prc.getStyleClass().add("form-panel");

        HBox pannelli = new HBox(16, pvs, prc);
        pannelli.setPadding(new Insets(0, 0, 20, 0));

        VBox corpo = new VBox(10, aggiorna, attesa, pannelli,
                messaggio, UiKit.homeBtn());
        corpo.setPadding(new Insets(16, 0, 0, 0));
        return UiKit.pagina("Amministrazione", "UC-08/09 · approvazioni", corpo, "admin");
    }
}