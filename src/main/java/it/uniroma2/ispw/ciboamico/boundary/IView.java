package it.uniroma2.ispw.ciboamico.boundary;

/**
 * Contratto comune delle Boundary (doppia interfaccia, criterio 30/30).
 * Sia le viste GUI (JavaFX) sia le viste CLI implementano {@code display()}.
 * Le Boundary NON conoscono le Entity: scambiano solo String/Bean con i
 * controller applicativi (regola bean-only).
 */
public interface IView {

    /**
     * Mostra la schermata. Nella GUI delega al Navigator; nella CLI
     * esegue il menu testuale (Scanner) e chiama i controller applicativi.
     */
    void display();
}
