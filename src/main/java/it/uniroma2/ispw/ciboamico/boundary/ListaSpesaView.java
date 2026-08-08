package it.uniroma2.ispw.ciboamico.boundary;

import it.uniroma2.ispw.ciboamico.bean.ProdottoBean;
import it.uniroma2.ispw.ciboamico.bean.UtenteBean;
import it.uniroma2.ispw.ciboamico.control.GestisciListaSpesaController;
import it.uniroma2.ispw.ciboamico.pattern.singleton.SessionManager;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Boundary JavaFX — Lista della Spesa (UC-03).
 * Calcola gli ingredienti mancanti per una ricetta (FR-05).
 * Scambia SOLO ProdottoBean con il controller (Bean-only).
 * UI: campo ricetta + griglia di card dei mancanti.
 */
public class ListaSpesaView {

    private final GestisciListaSpesaController controller;

    public ListaSpesaView() {
        this.controller = new GestisciListaSpesaController();
    }

    public Parent build() {
        UtenteBean utente = SessionManager.getInstance().getLoggedUser();

        TextField nomeRicetta = new TextField();
        nomeRicetta.setPromptText("es. Pasta al pomodoro");
        nomeRicetta.setMaxWidth(Double.MAX_VALUE);
        Button calcola = new Button("Calcola ingredienti mancanti");
        calcola.setId("btn-calcola");
        calcola.setMaxWidth(Double.MAX_VALUE);

        Label messaggio = new Label("Scrivi il nome della ricetta per generare "
                + "la lista acquisti.");
        messaggio.getStyleClass().add("page-subtitle");
        messaggio.setWrapText(true);

        FlowPane griglia = new FlowPane(16, 16);
        griglia.setPadding(new Insets(4, 0, 20, 0));

        calcola.setOnAction(e -> {
            List<ProdottoBean> mancanti =
                    controller.calcolaMancanze(utente.getEmail(), nomeRicetta.getText());
            griglia.getChildren().clear();
            if (mancanti.isEmpty()) {
                messaggio.setText("Tutto disponibile: nessun acquisto necessario ✓");
            } else {
                messaggio.setText("Mancano " + mancanti.size() + " ingredienti:");
            }
            mancanti.forEach(p -> griglia.getChildren().add(
                    UiKit.card(p.getNome(),
                            p.getQuantita() + " " + p.getUnitaMisura())));
        });

        // Form compatto in alto
        VBox form = new VBox(8, UiKit.field("Nome ricetta"), nomeRicetta, calcola);
        form.getStyleClass().add("form-panel");
        form.setPrefWidth(380);
        form.setMaxWidth(420);

        VBox corpo = new VBox(12, form, messaggio, griglia);
        corpo.setPadding(new Insets(16, 0, 0, 0));
        return UiKit.pagina("Lista della spesa", "UC-03 · ingredienti da acquistare", corpo, "lista-spesa");
    }
}