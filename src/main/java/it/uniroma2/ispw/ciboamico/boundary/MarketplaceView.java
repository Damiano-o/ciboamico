package it.uniroma2.ispw.ciboamico.boundary;

import it.uniroma2.ispw.ciboamico.bean.OrdineBean;
import it.uniroma2.ispw.ciboamico.exception.BusinessValidationException;
import it.uniroma2.ispw.ciboamico.bean.ProdottoBean;
import it.uniroma2.ispw.ciboamico.bean.UtenteBean;
import it.uniroma2.ispw.ciboamico.control.OrdinaProdottoController;
import it.uniroma2.ispw.ciboamico.pattern.singleton.SessionManager;
import it.uniroma2.ispw.ciboamico.boundary.Navigator;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Boundary JavaFX — Marketplace (UC-04 Ordina Prodotto).
 * Elenca i prodotti pubblicati e crea un ordine (D-03).
 * Scambia SOLO OrdineBean con il controller.
 * UI: catalogo in card + selezione prodotto + pulsante ordina.
 */
public class MarketplaceView {

    private final OrdinaProdottoController ordinaController;

    public MarketplaceView() {
        this.ordinaController = new OrdinaProdottoController();
    }

    public Parent build() {
        UtenteBean utente = SessionManager.getInstance().getLoggedUser();

        Button aggiorna = new Button("Aggiorna catalogo");
        aggiorna.setId("btn-catalogo");
        aggiorna.setMaxWidth(Double.MAX_VALUE);

        FlowPane catalogo = new FlowPane(16, 16);
        catalogo.setPadding(new Insets(4, 0, 20, 0));

        // Selezione prodotto
        ComboBox<String> prodSelez = new ComboBox<>();
        prodSelez.setPromptText("Scegli un prodotto");
        prodSelez.setMaxWidth(Double.MAX_VALUE);

        Button ordina = new Button("Ordina prodotto");
        ordina.setId("btn-ordina");
        ordina.setMaxWidth(Double.MAX_VALUE);
        Label messaggio = new Label("Carica il catalogo e scegli un prodotto.");
        messaggio.getStyleClass().add("page-subtitle");
        messaggio.setWrapText(true);

        // Campo buono promozionale (estensione 4a UC-04)
        TextField buonoField = new TextField();
        buonoField.setId("buonoField");
        buonoField.setPromptText("Codice buono (opzionale)");
        buonoField.setMaxWidth(Double.MAX_VALUE);
        Button applicaBuono = new Button("Applica buono");
        applicaBuono.setId("btn-applica-buono");
        applicaBuono.setMaxWidth(Double.MAX_VALUE);

        aggiorna.setOnAction(e -> {
            List<ProdottoBean> prodotti = ordinaController.getProdottiDisponibili();
            catalogo.getChildren().clear();
            prodotti.forEach(p -> catalogo.getChildren()
                    .add(UiKit.card(p.getNome(),
                            String.format("%.2f EUR · %s disponibili",
                                    p.getPrezzo(), p.getQuantita().intValue()))));
            prodSelez.getItems().setAll(prodotti.stream()
                    .map(ProdottoBean::getNome).collect(Collectors.toList()));
            messaggio.setText(prodotti.size() + " prodotti disponibili nel marketplace locale.");
        });

        ordina.setOnAction(e -> {
            String nome = prodSelez.getValue();
            if (nome == null || nome.isBlank()) {
                messaggio.setText("Seleziona un prodotto dal catalogo.");
                return;
            }
            ProdottoBean selezionato = ordinaController.getProdottiDisponibili().stream()
                    .filter(p -> nome.equals(p.getNome()))
                    .findFirst()
                    .orElse(null);
            if (selezionato == null) {
                messaggio.setText("Prodotto non più disponibile.");
                return;
            }
            // ordine in checkout: totale pieno se non è già stato applicato un buono
            OrdineBean inCorso = SessionManager.getInstance().getOrdineInCorso();
            if (inCorso == null || !nome.equals(inCorso.getNomeProdotto())) {
                inCorso = new OrdineBean();
                inCorso.setNomeProdotto(nome);
                inCorso.setTotale(selezionato.getPrezzo());
            }
            SessionManager.getInstance().setOrdineInCorso(inCorso);
            Navigator.getInstance().switchTo("payment");
        });

        // Estensione 4a: applica il buono promozionale e aggiorna il checkout
        applicaBuono.setOnAction(ev -> {
            try {
                OrdineBean bean = new OrdineBean();
                bean.setNomeProdotto(prodSelez.getValue());
                OrdineBean ris = ordinaController.applicaBuonoPromozionale(
                        buonoField.getText(), bean, utente);
                // conserva il totale scontato per la PaymentView
                ris.setIdOrdine(bean.getIdOrdine());
                SessionManager.getInstance().setOrdineInCorso(ris);
                messaggio.setText("Buono \"" + ris.getCodiceBuono()
                        + "\" applicato ✓ — totale " + String.format("%.2f", ris.getTotale()) + " EUR");
            } catch (BusinessValidationException ex) {
                messaggio.setText(ex.getMessage());
            } catch (Exception ex) {
                messaggio.setText("Problema tecnico: verificare il codice buono.");
            }
        });

        BorderPane area = new BorderPane();
        VBox ordinePanel = new VBox(8,
                UiKit.field("Prodotto da ordinare"), prodSelez, ordina,
                new Label("Buono promozionale"), buonoField, applicaBuono);
        ordinePanel.getStyleClass().add("form-panel");
        BorderPane.setMargin(ordinePanel, new Insets(0, 0, 0, 16));
        area.setCenter(catalogo);
        area.setRight(ordinePanel);

        VBox corpo = new VBox(10, aggiorna, messaggio,
                new Label("Catalogo prodotti"), area);
        corpo.setPadding(new Insets(16, 0, 0, 0));
        return UiKit.pagina("Marketplace locale", "UC-04 · ordina dalla vendita locale", corpo, "marketplace");
    }
}