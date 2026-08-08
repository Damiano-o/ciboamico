package it.uniroma2.ispw.ciboamico.bootstrap;

import it.uniroma2.ispw.ciboamico.boundary.ViewFactory;
import it.uniroma2.ispw.ciboamico.pattern.factory.OrdineLazyFactory;

/**
 * Runner: applica la scelta dell'utente (ApplicationModeBean) e avvia
 * l'interfaccia selezionata con la modalità di persistenza scelta.
 *
 * Separazione boundary(menu)/control(runner) come nel pattern BCE:
 * AvvioMenu (input) -&gt; Runner (coordinamento) -&gt; MainGUI / MainCLI.
 */
public final class Runner {

    private Runner() {
        // utility, non istanziabile
    }

    public static void avvia(ApplicationModeBean scelta, String[] args) {
        ApplicationModeManager modeManager = ApplicationModeManager.getInstance();

        // 1) Imposta la modalità di persistenza scelta dall'utente
        modeManager.setActiveMode(scelta.getPersistenza());

        System.out.println("\nCiboAmico avviato in modalità: "
                + modeManager.getActiveMode());
        System.out.println("DAOFactory: "
                + modeManager.getDAOFactory().getClass().getSimpleName());

        // 2) Configura la LazyFactory degli ordini con la DAOFactory attiva
        OrdineLazyFactory.configure(modeManager.getDAOFactory());

        // 3) Avvia l'interfaccia scelta (doppia interfaccia, criterio 30/30)
        ViewFactory.configure(scelta.getInterfaccia());
        if (scelta.gui()) {
            MainApplication.main(args);
        } else {
            MainCLI.main(args);
        }
    }
}