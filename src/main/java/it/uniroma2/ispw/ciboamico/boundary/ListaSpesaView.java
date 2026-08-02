package it.uniroma2.ispw.ciboamico.boundary;

import it.uniroma2.ispw.ciboamico.bean.ProdottoBean;
import it.uniroma2.ispw.ciboamico.bean.UtenteBean;
import it.uniroma2.ispw.ciboamico.control.GestisciListaSpesaController;
import it.uniroma2.ispw.ciboamico.pattern.singleton.SessionManager;
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
 * Boundary JavaFX — Lista della Spesa (UC-03).
 * Calcola gli ingredienti mancanti per una ricetta (FR-05).
 * Scambia SOLO ProdottoBean con il controller (Bean-only).
 */
public class ListaSpesaView {

    private final GestisciListaSpesaController controller;

    public ListaSpesaView() {
        this.controller = new GestisciListaSpesaController(
                ApplicationModeManager.getInstance().getDAOFactory());
    }

    public ListaSpesaView(DAOFactory factory) {
        this.controller = new GestisciListaSpesaController(factory);
    }

    public Parent build() {
        UtenteBean utente = SessionManager.getInstance().getLoggedUser();
        Label titolo = new Label("Lista della spesa (UC-03)");
        TextField nomeRicetta = new TextField();
        nomeRicetta.setPromptText("Nome ricetta");
        ListView<String> elenco = new ListView<>();
        Label messaggio = new Label();

        Button calcola = new Button("Calcola ingredienti mancanti");
        calcola.setOnAction(e -> {
            List<ProdottoBean> mancanti =
                    controller.calcolaMancanze(utente.getEmail(), nomeRicetta.getText());
            if (mancanti.isEmpty()) {
                messaggio.setText("Tutto disponibile: nessun acquisto necessario ✓");
            } else {
                messaggio.setText("Mancano " + mancanti.size() + " ingredienti:");
            }
            elenco.getItems().setAll(mancanti.stream()
                    .map(p -> p.getNome() + " — " + p.getQuantita() + " " + p.getUnitaMisura())
                    .toList());
        });

        Button indietro = new Button("← Home");
        indietro.setOnAction(e -> Navigator.getInstance().switchTo("home"));

        VBox root = new VBox(10, titolo, nomeRicetta, calcola, messaggio, elenco, indietro);
        root.setPrefSize(900, 640);
        return root;
    }
}
