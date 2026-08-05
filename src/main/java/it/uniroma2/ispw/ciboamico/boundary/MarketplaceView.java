package it.uniroma2.ispw.ciboamico.boundary;

import it.uniroma2.ispw.ciboamico.bean.OrdineBean;
import it.uniroma2.ispw.ciboamico.exception.BusinessValidationException;
import it.uniroma2.ispw.ciboamico.bean.ProdottoBean;
import it.uniroma2.ispw.ciboamico.bean.UtenteBean;
import it.uniroma2.ispw.ciboamico.control.OrdinaProdottoController;
import it.uniroma2.ispw.ciboamico.pattern.singleton.SessionManager;
import it.uniroma2.ispw.ciboamico.persistence.factory.DAOFactory;
import it.uniroma2.ispw.ciboamico.bootstrap.ApplicationModeManager;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
        DAOFactory factory = ApplicationModeManager.getInstance().getDAOFactory();
        this.ordinaController = new OrdinaProdottoController(factory);
    }

    public MarketplaceView(DAOFactory factory) {
        this.ordinaController = new OrdinaProdottoController(factory);
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
            try {
                OrdineBean bean = new OrdineBean();
                bean.setNomeProdotto(prodSelez.getValue());
                OrdineBean risultato = ordinaController.submitOrdine(bean, utente);
                messaggio.setText("Ordine creato ✓ — stato " + risultato.getStato()
                        + ", totale " + String.format("%.2f", risultato.getTotale()) + " EUR");
            } catch (BusinessValidationException ex) {
                messaggio.setText(ex.getMessage());
            } catch (Exception ex) {
                messaggio.setText("Problema tecnico: riprovare più tardi.");
            }
        });

        BorderPane area = new BorderPane();
        VBox ordinePanel = new VBox(8,
                UiKit.field("Prodotto da ordinare"), prodSelez, ordina);
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