package it.uniroma2.ispw.ciboamico.boundary;

import it.uniroma2.ispw.ciboamico.bean.OrdineBean;
import it.uniroma2.ispw.ciboamico.exception.InvalidStateTransitionException;
import it.uniroma2.ispw.ciboamico.bean.UtenteBean;
import it.uniroma2.ispw.ciboamico.control.GestisciOrdiniRicevutiController;
import it.uniroma2.ispw.ciboamico.pattern.singleton.SessionManager;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Boundary JavaFX — Ordini Ricevuti (UC-06).
 * Il venditore visualizza e aggiorna lo stato degli ordini (BR-04).
 * Scambia SOLO OrdineBean con il controller. UI: card ordini + form stato.
 */
public class OrdiniRicevutiView {

    private final GestisciOrdiniRicevutiController controller;

    public OrdiniRicevutiView() {
        this.controller = new GestisciOrdiniRicevutiController();
    }

    public Parent build() {
        UtenteBean utente = SessionManager.getInstance().getLoggedUser();

        Button aggiorna = new Button("Visualizza ordini ricevuti");
        aggiorna.setId("btn-ordini");
        aggiorna.setMaxWidth(Double.MAX_VALUE);

        Label messaggio = new Label("Carica gli ordini ricevuti dal tuo negozio.");
        messaggio.getStyleClass().add("page-subtitle");
        messaggio.setWrapText(true);

        FlowPane griglia = new FlowPane(16, 16);
        griglia.setPadding(new Insets(4, 0, 20, 0));

        // Form aggiornamento stato
        TextField idOrdine = new TextField();
        idOrdine.setPromptText("ID ordine (es. 1)");
        ComboBox<String> nuovoStato = new ComboBox<>();
        nuovoStato.getItems().addAll(controller.getStatiAggiornabili());
        nuovoStato.setValue(controller.getStatiAggiornabili().isEmpty()
                ? "" : controller.getStatiAggiornabili().get(0));
        Button cambia = new Button("Aggiorna stato");
        cambia.setMaxWidth(Double.MAX_VALUE);
        Label statoMsg = new Label();
        statoMsg.setWrapText(true);

        cambia.setOnAction(e -> {
            try {
                OrdineBean o = controller.aggiornaStato(
                        Long.parseLong(idOrdine.getText()), nuovoStato.getValue());
                statoMsg.setText("✓ Stato aggiornato → " + o.getStato());
                eAggiorna(utente, griglia);
            } catch (InvalidStateTransitionException ex) {
                statoMsg.setText(ex.getMessage());
            } catch (Exception ex) {
                statoMsg.setText("Problema tecnico: riprovare più tardi.");
            }
        });

        aggiorna.setOnAction(e -> eAggiorna(utente, griglia));

        VBox form = new VBox(8,
                UiKit.field("ID ordine"), idOrdine,
                UiKit.field("Nuovo stato"), nuovoStato,
                cambia, statoMsg);
        form.getStyleClass().add("form-panel");

        VBox corpo = new VBox(10, aggiorna, messaggio,
                new Label("Ordini ricevuti"), griglia, form);
        corpo.setPadding(new Insets(16, 0, 0, 0));

        return UiKit.pagina("Ordini ricevuti", "UC-06 · gestisci gli ordini", corpo, "ordini");
    }

    private void eAggiorna(UtenteBean utente, FlowPane griglia) {
        List<OrdineBean> ordini = controller.visualizzaOrdiniRicevuti(utente.getEmail());
        griglia.getChildren().clear();
        ordini.forEach(o -> griglia.getChildren().add(
                UiKit.card("Ordine #" + o.getIdOrdine(),
                        o.getNomeProdotto() + " · " + String.format("%.2f", o.getTotale()) + " EUR",
                        o.getStato(), badgeFor(o.getStato()))));
    }

    private String badgeFor(String stato) {
        if (stato == null) {
            return "badge-ok";
        }
        return switch (stato) {
            case "CREATED", "CONFIRMED" -> "badge-urgente";
            case "IN_DELIVERY" -> "badge-ok";
            default -> "badge-ok";
        };
    }
}