package it.uniroma2.ispw.ciboamico.bootstrap;

import it.uniroma2.ispw.ciboamico.boundary.ViewFactory;

/**
 * Entry point della modalità CLI (doppia interfaccia, criterio 30/30).
 * Avvio: ApplicationModeManager → DAOFactory → ViewFactory (famiglia CLI) → login → home.
 * I controller applicativi sono gli stessi della GUI: cambia solo la famiglia
 * di boundary (Abstract Factory).
 */
public final class MainCLI {

    private MainCLI() { }

    public static void main(String[] args) {
        ApplicationModeManager modeManager = ApplicationModeManager.getInstance();
        System.out.println("CiboAmico (CLI) — modalità: " + modeManager.getActiveMode());
        MainApplication.seedDemoDataSeNecessario();
        // Configura la LazyFactory degli ordini con la DAOFactory attiva
        // (stessa inizializzazione di Runner.avvia: necessaria per UC-04).
        it.uniroma2.ispw.ciboamico.pattern.factory.OrdineLazyFactory
                .configure(modeManager.getDAOFactory());
        ViewFactory.configure("cli"); // famiglia CLI (Abstract Factory, Singleton)
        ViewFactory factory = ViewFactory.getFactory();

        boolean running = true;
        while (running) {
            factory.createLoginView().display();
            if (CLISession.hasUser()) {
                factory.createHomeView().display();
            }
            running = CLISession.wantsToContinue();
        }
        System.out.println("Arrivederci!");
    }
}
