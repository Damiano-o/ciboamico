package it.uniroma2.ispw.ciboamico.bootstrap;

/**
 * Bean di avvio: incapsula la scelta dell'utente a runtime.
 * Raggruppa interfaccia (GUI/CLI) e modalità di persistenza
 * (DEMO/FS/JDBC) in un unico oggetto passato al runner.
 *
 * Segue il pattern dei progetti 30/30 (v. RouteX SelectMode):
 * Boundary di input -&gt; Bean -&gt; avvio con la config selezionata.
 */
public final class ApplicationModeBean {

    private String interfaccia;
    private String persistenza;

    public ApplicationModeBean() {
        // costruttore no-arg (convenzione bean)
    }

    public String getInterfaccia() {
        return interfaccia;
    }

    public void setInterfaccia(String interfaccia) {
        this.interfaccia = interfaccia;
    }

    public String getPersistenza() {
        return persistenza;
    }

    public void setPersistenza(String persistenza) {
        this.persistenza = persistenza;
    }

    public boolean gui() {
        return "gui".equalsIgnoreCase(interfaccia);
    }
}