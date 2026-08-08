package it.uniroma2.ispw.ciboamico.boundary;

import it.uniroma2.ispw.ciboamico.bean.UtenteBean;
import it.uniroma2.ispw.ciboamico.pattern.singleton.SessionManager;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

/**
 * Boundary JavaFX — Home (atterraggio post-login).
 * Centralizza la navigazione verso dispensa, ricette, lista spesa e
 * marketplace; mostra il ruolo dell'utente loggato (da sessione, Bean-only).
 *
 * UI minimalista: sidebar sinistra (sezioni) + area principale con griglia di
 * card di navigazione. Stile da styles.css (nessuna logica di business qui).
 */
public class HomeView {

    public Parent build() {
        UtenteBean utente = SessionManager.getInstance().getLoggedUser();

        // ---------- Sidebar ----------
        Label brand = new Label("CiboAmico");
        brand.getStyleClass().add("brand-title");

        Button itemDashboard = navItem("Dashboard", true);
        Button itemRicette = navItem("Ricette", false);
        itemRicette.setOnAction(e -> Navigator.getInstance().switchTo("ricette"));
        Button itemInventario = navItem("Inventario", false);
        itemInventario.setOnAction(e -> Navigator.getInstance().switchTo("inventario"));
        Button itemLista = navItem("Lista spesa", false);
        itemLista.setOnAction(e -> Navigator.getInstance().switchTo("lista-spesa"));
        Button itemMarket = navItem("Marketplace", false);
        itemMarket.setOnAction(e -> Navigator.getInstance().switchTo("marketplace"));

        // Voci specifiche per ruolo (coerenti con UiKit.sidebar)
        VBox sidebar = new VBox(2, brand, itemDashboard, itemRicette, itemInventario,
                itemLista, itemMarket);
        sidebar.getStyleClass().add("sidebar");

        String ruoloAttivo = utente != null ? utente.getRuoloAttivo() : "";
        if ("RuoloVenditore".equals(ruoloAttivo)) {
            sidebar.getChildren().add(navItemLink("Pubblica prodotto", "catalogo"));
            sidebar.getChildren().add(navItemLink("Ordini ricevuti", "ordini"));
        } else if ("RuoloNutrizionista".equals(ruoloAttivo)) {
            sidebar.getChildren().add(navItemLink("Crea ricetta", "crea-ricetta"));
        } else if ("RuoloAmministratore".equals(ruoloAttivo)) {
            sidebar.getChildren().add(navItemLink("Amministrazione", "admin"));
        }

        Button logout = new Button("Esci");
        logout.getStyleClass().add("button-outline");
        logout.setMaxWidth(Double.MAX_VALUE);
        logout.setOnAction(e -> {
            SessionManager.getInstance().logout();
            Navigator.getInstance().switchTo("login");
        });
        sidebar.getChildren().add(logout);
        VBox.setMargin(logout, new Insets(14, 0, 0, 0));

        // ---------- Area principale ----------
        Label benvenuto = new Label(utente != null
                ? "Benvenuto, " + utente.getUsername()
                : "Benvenuto in CiboAmico");
        benvenuto.getStyleClass().add("page-title");
        Label ruolo = new Label(utente != null
                ? "Ruolo attivo: " + ruoloLeggibile(utente.getRuoloAttivo())
                : "");
        ruolo.getStyleClass().add("page-subtitle");

        VBox main = new VBox(8, benvenuto, ruolo);
        main.getStyleClass().add("home-main");
        main.setAlignment(javafx.geometry.Pos.CENTER);

        // Griglia di card di navigazione (gap orizzontale/verticale 16)
        FlowPane grid = new FlowPane(20, 20);
        grid.setPadding(new Insets(8, 0, 0, 0));
        grid.getChildren().addAll(
                navCard("🥗", "Trova ricette", "Ricette compatibili con la dispensa", "ricette"),
                navCard("📦", "Inventario", "Gestisci i prodotti e le scadenze", "inventario"),
                navCard("📝", "Lista spesa", "Crea la lista dagli ingredienti", "lista-spesa"),
                navCard("🏪", "Marketplace", "Ordina dalla vendita locale", "marketplace"));
        main.getChildren().add(grid);

        BorderPane root = new BorderPane();
        root.setLeft(sidebar);
        root.setCenter(main);
        root.getStyleClass().add("home-root");
        root.setPrefSize(900, 640);
        BorderPane.setMargin(main, new Insets(20, 24, 20, 24));
        return root;
    }

    /** Traduzione del nome ruolo in forma leggibile. */
    private String ruoloLeggibile(String ruolo) {
        if (ruolo == null) {
            return "—";
        }
        return switch (ruolo) {
            case "RuoloCliente" -> "Cliente";
            case "RuoloVenditore" -> "Venditore";
            case "RuoloNutrizionista" -> "Nutrizionista";
            case "RuoloAmministratore" -> "Amministratore";
            default -> ruolo;
        };
    }

    /** Bottone di navigazione laterale; {@code attivo} evidenzia la sezione corrente. */
    private Button navItem(String testo, boolean attivo) {
        Button b = new Button(testo);
        b.setMaxWidth(Double.MAX_VALUE);
        b.getStyleClass().add("nav-item");
        if (attivo) {
            b.getStyleClass().add("active");
        }
        return b;
    }

    /** Voce di sidebar che naviga alla vista indicata. */
    private Button navItemLink(String testo, String vista) {
        Button b = navItem(testo, false);
        b.setOnAction(e -> Navigator.getInstance().switchTo(vista));
        return b;
    }

    /** Card di navigazione cliccabile che porta a una vista. */
    private VBox navCard(String icona, String titolo, String sottotitolo, String vista) {
        VBox card = new VBox(6);
        card.getStyleClass().add("card");
        card.setPrefSize(280, 150);
        Label ic = new Label(icona);
        ic.getStyleClass().add("icon");
        Label t = new Label(titolo);
        t.getStyleClass().add("card-title");
        Label s = new Label(sottotitolo);
        s.getStyleClass().add("card-subtitle");
        s.setWrapText(true);
        card.getChildren().addAll(ic, t, s);
        card.setOnMouseClicked(e -> Navigator.getInstance().switchTo(vista));
        card.setId("card-" + vista);
        return card;
    }
}