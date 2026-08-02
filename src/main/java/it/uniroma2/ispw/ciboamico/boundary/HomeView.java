package it.uniroma2.ispw.ciboamico.boundary;

import it.uniroma2.ispw.ciboamico.bean.UtenteBean;
import it.uniroma2.ispw.ciboamico.pattern.singleton.SessionManager;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Boundary JavaFX — Home (atterraggio post-login).
 * Centralizza la navigazione verso dispensa, ricette, lista spesa e
 * marketplace; mostra il ruolo dell'utente loggato (da sessione, Bean-only).
 */
public class HomeView {

    public Parent build() {
        UtenteBean utente = SessionManager.getInstance().getLoggedUser();
        Label titolo = new Label("CiboAmico — Home");
        Label benvenuto = new Label(utente != null
                ? "Benvenuto, " + utente.getUsername()
                : "Benvenuto in CiboAmico");
        Label ruolo = new Label(utente != null ? "Ruolo: " + utente.getRuoloAttivo() : "");

        Button inventario = new Button("Dispensa e inventario");
        inventario.setOnAction(e -> Navigator.getInstance().switchTo("inventario"));

        Button ricette = new Button("Trova ricette compatibili");
        ricette.setOnAction(e -> Navigator.getInstance().switchTo("ricette"));

        Button listaSpesa = new Button("Lista della spesa");
        listaSpesa.setOnAction(e -> Navigator.getInstance().switchTo("lista-spesa"));

        Button marketplace = new Button("Marketplace locale");
        marketplace.setOnAction(e -> Navigator.getInstance().switchTo("marketplace"));

        Button logout = new Button("Esci");
        logout.setOnAction(e -> {
            SessionManager.getInstance().logout();
            Navigator.getInstance().switchTo("login");
        });

        VBox root = new VBox(10, titolo, benvenuto, ruolo,
                inventario, ricette, listaSpesa, marketplace, logout);
        root.setPrefSize(900, 640);
        return root;
    }
}
