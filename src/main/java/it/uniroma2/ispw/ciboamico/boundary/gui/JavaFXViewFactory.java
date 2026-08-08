package it.uniroma2.ispw.ciboamico.boundary.gui;

import it.uniroma2.ispw.ciboamico.boundary.IView;
import it.uniroma2.ispw.ciboamico.boundary.Navigator;
import it.uniroma2.ispw.ciboamico.boundary.ViewFactory;

/**
 * Abstract Factory concreta — famiglia GUI (JavaFX) delle Boundary.
 * Le viste JavaFX sono già registrate nel Navigator da MainApplication;
 * questa factory le espone attraverso il contratto comune {@code IView},
 * delegando la visualizzazione al Navigator (switchTo).
 */
public class JavaFXViewFactory extends ViewFactory {

    @Override
    public IView createLoginView() {
        return () -> Navigator.getInstance().switchTo("login");
    }

    @Override
    public IView createHomeView() {
        return () -> Navigator.getInstance().switchTo("home");
    }

    @Override
    public IView createInventarioView() {
        return () -> Navigator.getInstance().switchTo("inventario");
    }

    @Override
    public IView createRicetteView() {
        return () -> Navigator.getInstance().switchTo("ricette");
    }

    @Override
    public IView createListaSpesaView() {
        return () -> Navigator.getInstance().switchTo("lista-spesa");
    }

    @Override
    public IView createMarketplaceView() {
        return () -> Navigator.getInstance().switchTo("marketplace");
    }

    @Override
    public IView createPaymentView() {
        return () -> Navigator.getInstance().switchTo("payment");
    }

    @Override
    public IView createCatalogoVenditoreView() {
        return () -> Navigator.getInstance().switchTo("catalogo");
    }

    @Override
    public IView createOrdiniRicevutiView() {
        return () -> Navigator.getInstance().switchTo("ordini");
    }

    @Override
    public IView createRicetteNutrizionistaView() {
        return () -> Navigator.getInstance().switchTo("crea-ricetta");
    }

    @Override
    public IView createAdminView() {
        return () -> Navigator.getInstance().switchTo("admin");
    }
}
