package it.uniroma2.ispw.ciboamico.bootstrap;

/**
 * Entry point JavaFX dell'applicazione.
 * Il flusso: Main → ApplicationModeManager → DAOFactory → LoginView.
 */
public final class MainApplication {

    private MainApplication() { }

    public static void main(String[] args) {
        ApplicationModeManager manager = ApplicationModeManager.getInstance();
        System.out.println("CiboAmico in modalità: " + manager.getActiveMode());
        LoginView.main(args);
    }
}
