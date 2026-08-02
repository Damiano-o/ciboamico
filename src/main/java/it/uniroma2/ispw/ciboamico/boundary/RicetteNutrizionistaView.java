package it.uniroma2.ispw.ciboamico.boundary;

import it.uniroma2.ispw.ciboamico.bean.RicettaBean;
import it.uniroma2.ispw.ciboamico.control.GestisciRicetteNutrizionistaController;
import it.uniroma2.ispw.ciboamico.persistence.factory.DAOFactory;
import it.uniroma2.ispw.ciboamico.bootstrap.ApplicationModeManager;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

/**
 * Boundary JavaFX — Gestisci Ricette (UC-07, Nutrizionista).
 * Crea ricette con minimo 2 ingredienti (BR-05). L'autore è risolto dal
 * controller via sessione; la view scambia SOLO RicettaBean.
 */
public class RicetteNutrizionistaView {

    private final GestisciRicetteNutrizionistaController controller;

    public RicetteNutrizionistaView() {
        this.controller = new GestisciRicetteNutrizionistaController(
                ApplicationModeManager.getInstance().getDAOFactory());
    }

    public RicetteNutrizionistaView(DAOFactory factory) {
        this.controller = new GestisciRicetteNutrizionistaController(factory);
    }

    public Parent build() {
        TextField nome = new TextField();
        nome.setPromptText("Nome ricetta");
        TextArea istruzioni = new TextArea();
        istruzioni.setPromptText("Istruzioni");
        TextField ingredienti = new TextField();
        ingredienti.setPromptText("Ingredienti separati da virgola (min 2)");
        TextField dosi = new TextField();
        dosi.setPromptText("Dosi separati da virgola (es. 500, 3)");
        Label messaggio = new Label();

        Button crea = new Button("Crea ricetta");
        crea.setOnAction(e -> {
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
                    if (!t.isBlank()) dosiList.add(Double.parseDouble(t));
                }
                bean.setDosi(dosiList);
                controller.creaRicetta(bean);
                messaggio.setText("Ricetta creata ✓ (stato PROPOSTA)");
                nome.clear(); istruzioni.clear(); ingredienti.clear(); dosi.clear();
            } catch (Exception ex) {
                messaggio.setText("Errore: " + ex.getMessage());
            }
        });

        Button indietro = new Button("← Home");
        indietro.setOnAction(e -> Navigator.getInstance().switchTo("home"));

        VBox root = new VBox(10, new Label("Crea ricetta (UC-07)"),
                nome, istruzioni, ingredienti, dosi, crea, messaggio, indietro);
        root.setPrefSize(900, 640);
        return root;
    }
}
