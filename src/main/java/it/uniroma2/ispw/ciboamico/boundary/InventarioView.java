package it.uniroma2.ispw.ciboamico.boundary;

import it.uniroma2.ispw.ciboamico.bean.ProdottoBean;
import it.uniroma2.ispw.ciboamico.bean.UtenteBean;
import it.uniroma2.ispw.ciboamico.control.GestisciInventarioController;
import it.uniroma2.ispw.ciboamico.pattern.singleton.SessionManager;
import it.uniroma2.ispw.ciboamico.persistence.factory.DAOFactory;
import it.uniroma2.ispw.ciboamico.bootstrap.ApplicationModeManager;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.List;

/**
 * Boundary JavaFX — Dispensa/Inventario (UC-01).
 * Aggiunge prodotto (FR-01), mostra inventario ordinato (FR-02) e
 * avvisi di scadenza (FR-03). Scambia SOLO ProdottoBean con il controller.
 */
public class InventarioView {

    private final GestisciInventarioController controller;

    public InventarioView() {
        this.controller = new GestisciInventarioController(
                ApplicationModeManager.getInstance().getDAOFactory());
    }

    public InventarioView(DAOFactory factory) {
        this.controller = new GestisciInventarioController(factory);
    }

    public Parent build() {
        UtenteBean utente = SessionManager.getInstance().getLoggedUser();

        TextField nome = new TextField();
        nome.setPromptText("Nome prodotto");
        TextField quantita = new TextField();
        quantita.setPromptText("Quantità");
        DatePicker scadenza = new DatePicker(LocalDate.now().plusDays(7));
        TextField posizione = new TextField();
        posizione.setPromptText("Posizione (Frigo/Dispensa)");
        ComboBox<String> unita = new ComboBox<>();
        unita.getItems().addAll("PEZZI", "GRAMMI", "LITRI", "ML");
        unita.setValue("PEZZI");

        Label messaggio = new Label();
        Button aggiungi = new Button("Aggiungi prodotto");
        Button aggiorna = new Button("Aggiorna elenco");
        ListView<String> elenco = new ListView<>();
        ListView<String> scadenzaAvviso = new ListView<>();

        aggiungi.setOnAction(e -> {
            try {
                ProdottoBean bean = new ProdottoBean();
                bean.setNome(nome.getText());
                bean.setQuantita(Double.parseDouble(quantita.getText()));
                bean.setScadenza(scadenza.getValue());
                bean.setPosizione(posizione.getText());
                bean.setUnitaMisura(unita.getValue());
                controller.aggiungiProdotto(bean, utente.getEmail());
                messaggio.setText("Prodotto aggiunto ✓");
                nome.clear(); quantita.clear(); posizione.clear();
                aggiorna.fire();
            } catch (Exception ex) {
                messaggio.setText("Errore: " + ex.getMessage());
            }
        });

        aggiorna.setOnAction(e -> {
            String email = utente.getEmail();
            List<ProdottoBean> ordinati = controller.visualizzaInventarioOrdinato(email);
            elenco.getItems().setAll(ordinati.stream()
                    .map(p -> p.getNome() + " — " + p.getQuantita().intValue()
                            + " " + p.getUnitaMisura() + " — scade " + p.getScadenza())
                    .toList());
            scadenzaAvviso.getItems().setAll(controller.prodottiInScadenza(email).stream()
                    .map(p -> "⚠ " + p.getNome() + " scade il " + p.getScadenza())
                    .toList());
        });

        Button indietro = new Button("← Home");
        indietro.setOnAction(e -> Navigator.getInstance().switchTo("home"));

        VBox root = new VBox(8, new Label("Dispensa (UC-01)"),
                new Label("Aggiungi prodotto:"), nome, quantita, scadenza,
                posizione, unita, aggiungi, messaggio, aggiorna,
                new Label("Inventario (ordine di scadenza):"), elenco,
                new Label("⚠ In scadenza entro 3 giorni:"), scadenzaAvviso,
                indietro);
        root.setPrefSize(900, 640);
        return root;
    }
}
