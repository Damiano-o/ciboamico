package it.uniroma2.ispw.ciboamico.boundary;

/**
 * Abstract Factory delle Boundary (pattern GoF, doppia interfaccia CLI/GUI).
 * Classe astratta con metodo statico {@code getFactory()} (integra Singleton):
 * legge la modalità di avvio e istanzia a runtime l'unica factory concreta
 * corretta (GUI o CLI). I controller applicativi restano invariati: cambiano
 * solo le view (prodotti astratti {@code IView}).
 */
public abstract class ViewFactory {

    private static ViewFactory instance;

    /** Family selezionata a runtime (default: GUI/JavaFX). */
    private static String family = "gui";

    protected ViewFactory() { }

    /**
     * Punto d'accesso globale (Singleton lazy): restituisce la factory della
     * family attiva. Il Main/MainCLI imposta la family con {@link #configure}.
     */
    public static synchronized ViewFactory getFactory() {
        if (instance == null) {
            instance = "cli".equalsIgnoreCase(family)
                    ? new it.uniroma2.ispw.ciboamico.boundary.cli.CLIViewFactory(
                            it.uniroma2.ispw.ciboamico.bootstrap.ApplicationModeManager.getInstance().getDAOFactory())
                    : new it.uniroma2.ispw.ciboamico.boundary.gui.JavaFXViewFactory();
        }
        return instance;
    }

    /** Seleziona la family di view (\"gui\" o \"cli\") prima del primo accesso. */
    public static synchronized void configure(String uiFamily) {
        family = uiFamily == null ? "gui" : uiFamily.toLowerCase();
        instance = null; // invalidate: la prossima getFactory() ricrea la factory giusta
    }

    public abstract IView createLoginView();

    public abstract IView createHomeView();

    public abstract IView createInventarioView();

    public abstract IView createRicetteView();

    public abstract IView createListaSpesaView();

    public abstract IView createMarketplaceView();

    public abstract IView createCatalogoVenditoreView();

    public abstract IView createOrdiniRicevutiView();

    public abstract IView createRicetteNutrizionistaView();

    public abstract IView createAdminView();
}
