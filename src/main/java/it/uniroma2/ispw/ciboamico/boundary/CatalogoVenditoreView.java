package it.uniroma2.ispw.ciboamico.boundary;

import it.uniroma2.ispw.ciboamico.bean.ProdottoBean;
import it.uniroma2.ispw.ciboamico.control.GestisciCatalogoVenditoreController;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

/**
 * Boundary JavaFX — Catalogo Venditore (UC-05).
 * Il venditore approvato pubblica prodotti con prezzo (BR-02, BR-06).
 * Scambia SOLO ProdottoBean con il controller (il venditore è risolto dal
 * controller via sessione). UI: form in card laterale.
 */
public class CatalogoVenditoreView {

    private final GestisciCatalogoVenditoreController controller;

    public CatalogoVenditoreView() {
        this.controller = new GestisciCatalogoVenditoreController();
    }

    public Parent build() {
        TextField nome = new TextField();
        nome.setPromptText("es. Miele locale");
        TextField prezzo = new TextField();
        prezzo.setPromptText("es. 6.50");
        TextField quantita = new TextField();
        quantita.setPromptText("Quantità disponibile");
        DatePicker scadenza = new DatePicker(LocalDate.now().plusMonths(1));
        ComboBox<String> unita = new ComboBox<>();
        unita.getItems().addAll("PEZZI", "GRAMMI", "LITRI", "ML");
        unita.setValue("PEZZI");
        Label messaggio = new Label("Compila i campi e pubblica il prodotto.");
        messaggio.getStyleClass().add("page-subtitle");
        messaggio.setWrapText(true);

        Button pubblica = new Button("Pubblica prodotto");
        pubblica.setMaxWidth(Double.MAX_VALUE);
        pubblica.setOnAction(e -> {
            messaggio.setText("");
            try {
                ProdottoBean bean = new ProdottoBean();
                bean.setNome(nome.getText());
                bean.setPrezzo(Double.parseDouble(prezzo.getText()));
                bean.setQuantita(Double.parseDouble(quantita.getText()));
                bean.setScadenza(scadenza.getValue());
                bean.setUnitaMisura(unita.getValue());
                controller.pubblicaProdotto(bean);
                messaggio.setText("✓ Prodotto pubblicato sul marketplace.");
                nome.clear(); prezzo.clear(); quantita.clear();
            } catch (Exception ex) {
                messaggio.setText("Errore: " + ex.getMessage());
            }
        });

        VBox form = new VBox(8,
                UiKit.field("Nome"), nome, UiKit.field("Prezzo (EUR)"), prezzo,
                UiKit.field("Quantità"), quantita, UiKit.field("Scadenza"), scadenza,
                UiKit.field("Unità"), unita, pubblica, messaggio);
        form.getStyleClass().add("form-panel");

        BorderPane corpo = new BorderPane();
        corpo.setCenter(form);
        corpo.setPadding(new Insets(16, 0, 20, 0));

        return UiKit.pagina("Catalogo venditore", "UC-05 · pubblica e vendi",
                corpo, "catalogo");
    }
}