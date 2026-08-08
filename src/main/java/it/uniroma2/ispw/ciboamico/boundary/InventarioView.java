package it.uniroma2.ispw.ciboamico.boundary;

import it.uniroma2.ispw.ciboamico.bean.ProdottoBean;
import it.uniroma2.ispw.ciboamico.bean.UtenteBean;
import it.uniroma2.ispw.ciboamico.control.GestisciInventarioController;
import it.uniroma2.ispw.ciboamico.pattern.singleton.SessionManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Boundary JavaFX — Dispensa/Inventario (UC-01).
 * Aggiunge prodotto (FR-01), mostra inventario (FR-02) e avvisi scadenza
 * (FR-03). Scambia SOLO ProdottoBean con il controller (Bean-only).
 *
 * UI minimalista: header con contatori + form aggiunta (card) +
 * lista prodotti come card con badge di scadenza e filtro per posizione.
 */
public class InventarioView {

    private static final long SOGLIA_URGENZA = 3;
    private static final String TUTTI = "Tutti";

    private final GestisciInventarioController controller;

    public InventarioView() {
        this.controller = new GestisciInventarioController();
    }

    public Parent build() {
        UtenteBean utente = SessionManager.getInstance().getLoggedUser();

        // ---------- Header ----------
        Label titolo = new Label("Dispensa");
        titolo.getStyleClass().add("screen-title");
        Label sottotitolo = new Label("Gestisci i prodotti e le loro scadenze");
        sottotitolo.getStyleClass().add("page-subtitle");
        VBox header = new VBox(2, titolo, sottotitolo);

        Label totVal = new Label("0");
        Label scadVal = new Label("0");
        HBox stats = new HBox(12,
                statChip("🗂", totVal, "prodotti", "ok"),
                statChip("⏳", scadVal, "in scadenza", "warn"));

        // ---------- Form aggiunta (card laterale) ----------
        TextField nome = new TextField();
        nome.setPromptText("es. Latte");
        TextField quantita = new TextField();
        quantita.setPromptText("es. 1000");
        DatePicker scadenza = new DatePicker(LocalDate.now().plusDays(7));
        ComboBox<String> posizione = new ComboBox<>();
        posizione.getItems().addAll("Frigo", "Dispensa", "Congelatore", "Cantina", "Altro");
        posizione.setValue("Frigo");
        ComboBox<String> unita = new ComboBox<>();
        unita.getItems().addAll("PEZZI", "GRAMMI", "ML", "LITRI");
        unita.setValue("GRAMMI");
        Label messaggio = new Label();
        messaggio.setWrapText(true);

        // Filtro + lista (dichiarati prima del form per uso nel lambda)
        ComboBox<String> filtro = new ComboBox<>();
        filtro.getItems().addAll(TUTTI, "Frigo", "Dispensa");
        filtro.setValue(TUTTI);
        FlowPane griglia = new FlowPane(16, 16);
        griglia.setPadding(new Insets(4, 0, 0, 0));

        Button aggiungi = new Button("Aggiungi prodotto");
        aggiungi.setMaxWidth(Double.MAX_VALUE);
        aggiungi.setOnAction(e -> {
            messaggio.setText("");
            try {
                ProdottoBean bean = new ProdottoBean();
                bean.setNome(nome.getText());
                bean.setQuantita(Double.parseDouble(quantita.getText()));
                bean.setScadenza(scadenza.getValue());
                bean.setPosizione(posizione.getValue());
                bean.setUnitaMisura(unita.getValue());
                controller.aggiungiProdotto(bean, utente.getEmail());
                messaggio.setText("✓ Aggiunto: " + bean.getNome()
                        + " (" + bean.getQuantita().intValue() + " " + bean.getUnitaMisura() + ")");
                nome.clear(); quantita.clear(); posizione.setValue("Frigo");
                refresh(utente, griglia, totVal, scadVal, filtro);
            } catch (Exception ex) {
                messaggio.setText("Errore: " + ex.getMessage());
            }
        });

        VBox form = new VBox(8,
                UiKit.field("Nome prodotto"), nome,
                UiKit.field("Quantità"), quantita,
                UiKit.field("Scadenza"), scadenza,
                UiKit.field("Posizione"), posizione,
                UiKit.field("Unità"), unita,
                aggiungi, messaggio);
        form.getStyleClass().add("form-panel");

        // ---------- Filtro + lista ----------
        Label filtroLbl = new Label("Filtra per posizione");
        filtroLbl.getStyleClass().add("page-subtitle");
        filtro.setOnAction(e -> refresh(utente, griglia, totVal, scadVal, filtro));

        VBox listaArea = new VBox(8, filtroLbl, filtro, griglia);
        listaArea.setPadding(new Insets(20, 24, 20, 24));

        // ---------- Root ----------
        VBox topo = new VBox(12, header, stats);
        topo.setPadding(new Insets(16, 24, 0, 24));

        ScrollPane scroll = new ScrollPane();
        scroll.setContent(new VBox(0, topo, listaArea));
        scroll.setFitToWidth(true);
        scroll.setPannable(true);

        // Form laterale a destra (Aggiungi prodotto)
        BorderPane.setMargin(form, new Insets(16, 24, 16, 0));

        BorderPane centro = new BorderPane();
        centro.setCenter(scroll);
        centro.setRight(form);

        BorderPane root = new BorderPane();
        root.setLeft(UiKit.sidebar("inventario"));
        root.setCenter(centro);
        root.getStyleClass().add("home-root");
        root.setPrefSize(900, 640);

        refresh(utente, griglia, totVal, scadVal, filtro);
        return root;
    }

    private HBox statChip(String icona, Label value, String testo, String stato) {
        Label ic = new Label(icona);
        ic.getStyleClass().add("icon");
        Label lbl = new Label(testo);
        lbl.getStyleClass().add("stat-label");
        VBox num = new VBox(0, value, lbl);
        value.getStyleClass().add("stat-value");
        HBox chip = new HBox(8, ic, num);
        chip.setAlignment(Pos.CENTER_LEFT);
        chip.getStyleClass().addAll("stat-chip", stato);
        return chip;
    }

    private void refresh(UtenteBean utente, FlowPane griglia, Label tot,
            Label scad, ComboBox<String> filtro) {
        List<ProdottoBean> ordinati = controller.visualizzaInventarioOrdinato(utente.getEmail());
        long inScad = controller.prodottiInScadenza(utente.getEmail()).size();
        tot.setText(String.valueOf(ordinati.size()));
        scad.setText(String.valueOf(inScad));

        String f = filtro.getValue();
        griglia.getChildren().clear();
        ordinati.stream()
                .filter(p -> TUTTI.equals(f) || f.equalsIgnoreCase(p.getPosizione()))
                .forEach(p -> griglia.getChildren().add(prodottoCard(p)));
    }

    private VBox prodottoCard(ProdottoBean p) {
        Label nome = new Label(p.getNome());
        nome.getStyleClass().add("prodotto-nome");
        Label badge = new Label(badgeTesto(p));
        badge.getStyleClass().addAll("badge", badgeClasse(p));
        Region spazio = new Region();
        HBox.setHgrow(spazio, Priority.ALWAYS);
        HBox testa = new HBox(8, nome, spazio, badge);
        Label dettagli = new Label(p.getQuantita().intValue() + " " + p.getUnitaMisura()
                + " · " + (p.getPosizione() == null ? "—" : p.getPosizione())
                + " · scade " + p.getScadenza());
        dettagli.getStyleClass().add("prodotto-dett");
        VBox card = new VBox(6, testa, dettagli);
        card.getStyleClass().add("prodotto-card");
        card.setPrefWidth(300);
        return card;
    }

    private String badgeTesto(ProdottoBean p) {
        if (scaduto(p)) {
            return "SCADUTO";
        }
        long g = ChronoUnit.DAYS.between(LocalDate.now(), p.getScadenza());
        return g <= SOGLIA_URGENZA ? "Scade tra " + g + "g" : "OK";
    }

    private String badgeClasse(ProdottoBean p) {
        if (scaduto(p)) {
            return "badge-scaduto";
        }
        long g = ChronoUnit.DAYS.between(LocalDate.now(), p.getScadenza());
        return g <= SOGLIA_URGENZA ? "badge-urgente" : "badge-ok";
    }

    private boolean scaduto(ProdottoBean p) {
        return p.getScadenza() != null && p.getScadenza().isBefore(LocalDate.now());
    }
}