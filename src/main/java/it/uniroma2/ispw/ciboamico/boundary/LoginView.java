package it.uniroma2.ispw.ciboamico.bootstrap;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import it.uniroma2.ispw.ciboamico.control.AutenticazioneController;
import it.uniroma2.ispw.ciboamico.persistence.factory.DAOFactory;

/**
 * Boundary JavaFX — Login (UC-11).
 * La view NON conosce le Entity: scambia solo String/Bean con il controller.
 */
public class LoginView extends Application {

    private final AutenticazioneController controller;

    public LoginView() {
        this.controller = new AutenticazioneController(ApplicationModeManager.getInstance().getDAOFactory());
    }

    public LoginView(DAOFactory factory) {
        this.controller = new AutenticazioneController(factory);
    }

    @Override
    public void start(Stage stage) {
        Label titolo = new Label("CiboAmico — Accedi");
        TextField email = new TextField();
        email.setPromptText("email");
        PasswordField password = new PasswordField();
        password.setPromptText("password");
        Label messaggio = new Label();
        Button login = new Button("Accedi");

        login.setOnAction(e -> {
            try {
                controller.login(email.getText(), password.getText());
                messaggio.setText("Benvenuto!");
            } catch (IllegalArgumentException ex) {
                messaggio.setText("Credenziali non valide");
            }
        });

        VBox root = new VBox(10, titolo, email, password, login, messaggio);
        Scene scene = new Scene(root, 300, 200);
        stage.setTitle("CiboAmico");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
