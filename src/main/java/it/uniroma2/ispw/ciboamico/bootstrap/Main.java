package it.uniroma2.ispw.ciboamico.bootstrap;

import it.uniroma2.ispw.ciboamico.boundary.ViewFactory;

/**
 * Entry point dell'applicazione (doppia interfaccia CLI/GUI).
 * Bootstrap: ApplicationModeManager → DAOFactory → ViewFactory (GUI o CLI).
 * Uso: java ...Main [gui|cli]  (default: gui)
 */
public class Main {

    public static void main(String[] args) {
        String ui = args.length > 0 ? args[0].toLowerCase() : "gui";
        ApplicationModeManager modeManager = ApplicationModeManager.getInstance();
        System.out.println("CiboAmico avviato in modalità: " + modeManager.getActiveMode());
        System.out.println("DAOFactory: " + modeManager.getDAOFactory().getClass().getSimpleName());
        ViewFactory.configure(ui);
        if ("cli".equals(ui)) {
            MainCLI.main(args);
        } else {
            MainApplication.main(args);
        }
    }
}
