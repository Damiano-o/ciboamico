package it.uniroma2.ispw.ciboamico.boundary;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton: isola lo Stage JavaFX e gestisce il cambio schermata.
 * Le view si registrano con un nome simbolico; switchTo sostituisce la root
 * della Scene senza ricrearla (minimalismo, nessun CSS/FXML).
 */
public final class Navigator {

    private static Navigator instance;
    private Stage stage;
    private final Map<String, ViewFactory> registry = new HashMap<>();

    private Navigator() { }

    public static synchronized Navigator getInstance() {
        if (instance == null) {
            instance = new Navigator();
        }
        return instance;
    }

    public void init(Stage stage) {
        this.stage = stage;
    }

    public void register(String viewName, ViewFactory factory) {
        registry.put(viewName, factory);
    }

    /** Cambia schermata: crea la Scene la prima volta, poi sostituisce la root. */
    public void switchTo(String viewName) {
        ViewFactory factory = registry.get(viewName);
        if (factory == null) {
            throw new IllegalArgumentException("View non registrata: " + viewName);
        }
        Parent root = factory.build();
        if (stage.getScene() == null) {
            stage.setScene(new Scene(root, 900, 640));
        } else {
            stage.getScene().setRoot(root);
        }
        stage.show();
    }
}
