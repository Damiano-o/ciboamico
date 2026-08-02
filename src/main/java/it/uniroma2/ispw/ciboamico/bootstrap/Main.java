package it.uniroma2.ispw.ciboamico.bootstrap;

/**
 * Entry point dell'applicazione (JavaFX).
 * Bootstrap: ApplicationModeManager → DAOFactory → Navigator → UI.
 */
public class Main {

    public static void main(String[] args) {
        ApplicationModeManager modeManager = ApplicationModeManager.getInstance();
        System.out.println("CiboAmico avviato in modalità: " + modeManager.getActiveMode());
        System.out.println("DAOFactory: " + modeManager.getDAOFactory().getClass().getSimpleName());
        MainApplication.main(args);
    }
}
