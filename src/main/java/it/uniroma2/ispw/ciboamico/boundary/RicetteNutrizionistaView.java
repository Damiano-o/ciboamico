package it.uniroma2.ispw.ciboamico.boundary;

import it.uniroma2.ispw.ciboamico.bean.RicettaBean;
import it.uniroma2.ispw.ciboamico.control.GestisciRicetteNutrizionistaController;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

/**
 * Boundary JavaFX — Gestisci Ricette (UC-07, Nutrizionista).
 * Crea ricette con minimo 2 ingredienti (BR-05). L'autore è risolto dal
 * controller via sessione; la view scambia SOLO RicettaBean.
 * UI: form in card con campi strutturati.
 */
public class RicetteNutrizionistaView {

    private final GestisciRicetteNutrizionistaController controller;

    public RicetteNutrizionistaView() {
        this.controller = new GestisciRicetteNutrizionistaController();
    }

    public Parent build() {
        TextField nome = new TextField();
        nome.setPromptText("es. Pasta al pomodoro");
        TextArea istruzioni = new TextArea();
        istruzioni.setPromptText("Passaggi per preparare la ricetta");
        istruzioni.setPrefRowCount(4);
        TextField ingredienti = new TextField();
        ingredienti.setPromptText("es. Pasta, Pomodoro, Basilico");
        TextField dosi = new TextField();
        dosi.setPromptText("es. 500, 3, 2");
        Label messaggio = new Label("Crea una ricetta con almeno 2 ingredienti.");
        messaggio.getStyleClass().add("page-subtitle");
        messaggio.setWrapText(true);

        Button crea = new Button("Crea ricetta");
        crea.setMaxWidth(Double.MAX_VALUE);
        crea.setOnAction(e -> {
            messaggio.setText("");
            try {
                RicettaBean bean = new RicettaBean();
                bean.setNome(nome.getText());
                bean.setIstruzioni(istruzioni.getText());
                List<String> nomi = new ArrayList<>(
                        List.of(ingredienti.getText().split(",")).stream()
                                .map(String::trim).filter(s -> !s.isBlank()).toList());
                bean.setIngredientiNomi(nomi);
                List<Double> dosiList = new ArrayList<>();
                for (String d : dosi.getText().split(",")) {
                    String t = d.trim();
                    if (!t.isBlank()) {
                        dosiList.add(Double.parseDouble(t));
                    }
                }
                bean.setDosi(dosiList);
                controller.creaRicetta(bean);
                messaggio.setText("✓ Ricetta creata (stato PROPOSTA, in attesa di approvazione).");
                nome.clear(); istruzioni.clear(); ingredienti.clear(); dosi.clear();
            } catch (Exception ex) {
                messaggio.setText("Errore: " + ex.getMessage());
            }
        });

        VBox form = new VBox(8,
                UiKit.field("Nome ricetta"), nome,
                UiKit.field("Istruzioni"), istruzioni,
                UiKit.field("Ingredienti (min 2, separati da virgola)"), ingredienti,
                UiKit.field("Dosi (stesso ordine, separati da virgola)"), dosi,
                crea, messaggio);
        form.getStyleClass().add("form-panel");
        form.setPrefWidth(380);
        form.setMaxWidth(420);

        VBox corpo = new VBox(8, form);
        corpo.setPadding(new Insets(16, 0, 20, 0));
        return UiKit.pagina("Crea ricetta", "UC-07 · nutrizionista", corpo, "ricette");
    }
}