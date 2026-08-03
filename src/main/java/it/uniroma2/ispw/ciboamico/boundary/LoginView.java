package it.uniroma2.ispw.ciboamico.boundary;

import it.uniroma2.ispw.ciboamico.bean.UtenteBean;
import it.uniroma2.ispw.ciboamico.control.AutenticazioneController;
import it.uniroma2.ispw.ciboamico.exception.AutenticazioneException;
import it.uniroma2.ispw.ciboamico.persistence.factory.DAOFactory;
import it.uniroma2.ispw.ciboamico.bootstrap.ApplicationModeManager;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * Boundary JavaFX — Login (UC-11).
 * La view NON conosce le Entity: scambia solo String/Bean con il controller.
 * Dopo il login naviga alla Home via Navigator (Bean-only).
 */
public class LoginView {

    private final AutenticazioneController controller;

    public LoginView() {
        this.controller = new AutenticazioneController(
                ApplicationModeManager.getInstance().getDAOFactory());
    }

    public LoginView(DAOFactory factory) {
        this.controller = new AutenticazioneController(factory);
    }

    public Parent build() {
        Label titolo = new Label("CiboAmico — Accedi");
        TextField email = new TextField();
        email.setPromptText("email");
        PasswordField password = new PasswordField();
        password.setPromptText("password");
        Label messaggio = new Label();
        Button login = new Button("Accedi");
        login.setId("btn-login");

        login.setOnAction(e -> {
            try {
                UtenteBean utente = controller.login(email.getText(), password.getText());
                messaggio.setText("Benvenuto, " + utente.getUsername() + "!");
                Navigator.getInstance().switchTo("home");
            } catch (AutenticazioneException ex) {
                messaggio.setText("Credenziali non valide");
            }
        });

        VBox root = new VBox(10, titolo, email, password, login, messaggio);
        root.setPrefSize(900, 640);
        return root;
    }
}
