package it.uniroma2.ispw.ciboamico.boundary;

import it.uniroma2.ispw.ciboamico.bean.ProdottoBean;
import it.uniroma2.ispw.ciboamico.control.GestisciCatalogoVenditoreController;
import it.uniroma2.ispw.ciboamico.persistence.factory.DAOFactory;
import it.uniroma2.ispw.ciboamico.bootstrap.ApplicationModeManager;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

/**
 * Boundary JavaFX — Catalogo Venditore (UC-05).
 * Il venditore approvato pubblica prodotti con prezzo (BR-02, BR-06).
 * Scambia SOLO ProdottoBean con il controller (il venditore è risolto
 * dal controller via sessione, mai passato dalla view).
 */
public class CatalogoVenditoreView {

    private final GestisciCatalogoVenditoreController controller;

    public CatalogoVenditoreView() {
        this.controller = new GestisciCatalogoVenditoreController(
                ApplicationModeManager.getInstance().getDAOFactory());
    }

    public CatalogoVenditoreView(DAOFactory factory) {
        this.controller = new GestisciCatalogoVenditoreController(factory);
    }

    public Parent build() {
        TextField nome = new TextField();
        nome.setPromptText("Nome prodotto");
        TextField prezzo = new TextField();
        prezzo.setPromptText("Prezzo (EUR)");
        TextField quantita = new TextField();
        quantita.setPromptText("Quantità disponibile");
        DatePicker scadenza = new DatePicker(LocalDate.now().plusMonths(1));
        ComboBox<String> unita = new ComboBox<>();
        unita.getItems().addAll("PEZZI", "GRAMMI", "LITRI");
        unita.setValue("PEZZI");
        Label messaggio = new Label();

        Button pubblica = new Button("Pubblica prodotto");
        pubblica.setOnAction(e -> {
            try {
                ProdottoBean bean = new ProdottoBean();
                bean.setNome(nome.getText());
                bean.setPrezzo(Double.parseDouble(prezzo.getText()));
                bean.setQuantita(Double.parseDouble(quantita.getText()));
                bean.setScadenza(scadenza.getValue());
                bean.setUnitaMisura(unita.getValue());
                controller.pubblicaProdotto(bean);
                messaggio.setText("Prodotto pubblicato ✓");
                nome.clear(); prezzo.clear(); quantita.clear();
            } catch (Exception ex) {
                messaggio.setText("Errore: " + ex.getMessage());
            }
        });

        Button indietro = new Button("← Home");
        indietro.setOnAction(e -> Navigator.getInstance().switchTo("home"));

        VBox root = new VBox(10, new Label("Pubblica prodotto (UC-05)"),
                nome, prezzo, quantita, scadenza, unita, pubblica, messaggio, indietro);
        root.setPrefSize(900, 640);
        return root;
    }
}
