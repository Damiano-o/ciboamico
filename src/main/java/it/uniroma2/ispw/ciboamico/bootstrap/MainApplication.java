package it.uniroma2.ispw.ciboamico.bootstrap;

import it.uniroma2.ispw.ciboamico.boundary.*;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Entry point JavaFX dell'applicazione.
 * Registra tutte le Boundary nel Navigator e avvia la Login.
 * Flusso: Main → ApplicationModeManager → DAOFactory → Navigator → Login.
 */
public final class MainApplication extends Application {

    private final ApplicationModeManager modeManager;

    public MainApplication() {
        this.modeManager = ApplicationModeManager.getInstance();
    }

    @Override
    public void start(Stage stage) {
        // In modalità DEMO carica i dati seed (utenti, prodotti, ricette).
        if (ApplicationModeManager.MODE_DEMO.equals(modeManager.getActiveMode())
                && modeManager.getDAOFactory() instanceof it.uniroma2.ispw.ciboamico.persistence.factory.DemoDAOFactory demo) {
            demo.seedDemoData();
        }

        Navigator navigator = Navigator.getInstance();
        navigator.init(stage);

        // Registrazione view (nomi simbolici, factory programmatiche)
        navigator.register("login", new LoginView(modeManager.getDAOFactory())::build);
        navigator.register("home", new HomeView()::build);
        navigator.register("inventario", new InventarioView(modeManager.getDAOFactory())::build);
        navigator.register("ricette", new RicetteView(modeManager.getDAOFactory())::build);
        navigator.register("lista-spesa", new ListaSpesaView(modeManager.getDAOFactory())::build);
        navigator.register("marketplace", new MarketplaceView(modeManager.getDAOFactory())::build);
        navigator.register("catalogo", new CatalogoVenditoreView(modeManager.getDAOFactory())::build);
        navigator.register("ordini", new OrdiniRicevutiView(modeManager.getDAOFactory())::build);
        navigator.register("crea-ricetta", new RicetteNutrizionistaView(modeManager.getDAOFactory())::build);
        navigator.register("admin", new AdminView(modeManager.getDAOFactory())::build);

        stage.setTitle("CiboAmico — " + modeManager.getActiveMode());
        navigator.switchTo("login");

        // Modalità demo: genera snapshot PNG (flag ciboamico.demo) oppure
        // naviga con pause per la registrazione dello schermo (flag ciboamico.demorun).
        if (System.getProperty("ciboamico.demo") != null
                || System.getProperty("ciboamico.demorun") != null) {
            new Thread(() -> {
                // attende che la scena sia visibile e la cattura schermo sia partita
                try { Thread.sleep(4000); } catch (InterruptedException ignored) { }
                DemoDriver.run();
            }).start();
        }
    }

    public static void main(String[] args) {
        ApplicationModeManager manager = ApplicationModeManager.getInstance();
        System.out.println("CiboAmico in modalità: " + manager.getActiveMode());
        launch(args);
    }
}
