package it.uniroma2.ispw.ciboamico.bootstrap;

import java.util.Locale;

/**
 * Entry point dell'applicazione (doppia interfaccia CLI/GUI).
 *
 * L'UTENTE sceglie come avviare l'applicazione tramite il menu
 * interattivo (AvvioMenu): interfaccia (GUI/CLI) e modalità di
 * persistenza (DEMO/FS/JDBC). La scelta viene applicata da Runner.
 *
 * Backward-compat: un argomento "gui" o "cli" avvia direttamente
 * quell'interfaccia in modalità config.properties, senza menu
 * (utile per automazione test/demo).
 */
public final class Main {

    private Main() {
        // classe utility: solo entry point statico
    }

    public static void main(String[] args) {
        String ui = args.length > 0 ? args[0].toLowerCase(Locale.ROOT) : null;

        if ("gui".equals(ui) || "cli".equals(ui)) {
            // avvio diretto (senza menu) — interfaccia dall'argomento,
            // persistenza da ApplicationModeManager (file config, default DEMO)
            ApplicationModeBean bean = new ApplicationModeBean();
            bean.setInterfaccia(ui);
            bean.setPersistenza(ApplicationModeManager.getInstance().getActiveMode());
            Runner.avvia(bean, args);
            return;
        }

        // Avvio normale: l'utente sceglie dal menu interattivo
        ApplicationModeBean bean = AvvioMenu.chiediScelta();
        Runner.avvia(bean, args);
    }
}