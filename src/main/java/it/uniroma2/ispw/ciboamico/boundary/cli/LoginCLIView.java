package it.uniroma2.ispw.ciboamico.boundary.cli;

import it.uniroma2.ispw.ciboamico.bean.UtenteBean;
import it.uniroma2.ispw.ciboamico.boundary.IView;
import it.uniroma2.ispw.ciboamico.control.AutenticazioneController;
import it.uniroma2.ispw.ciboamico.exception.AutenticazioneException;

/**
 * Boundary CLI — Login (UC-11). Stesso flusso della LoginView JavaFX,
 * scambia SOLO UtenteBean/AutenticazioneBean con AutenticazioneController.
 */
public class LoginCLIView implements IView {

    private final CLIContext ctx;
    private final AutenticazioneController controller;

    public LoginCLIView(CLIContext ctx) {
        this.ctx = ctx;
        this.controller = new AutenticazioneController(ctx.getFactory());
    }

    @Override
    public void display() {
        System.out.println("\n=== CiboAmico — Accedi ===");
        String email = ctx.leggiStringa("Email: ");
        String password = ctx.leggiStringa("Password: ");
        try {
            UtenteBean utente = controller.login(email, password);
            System.out.println("Benvenuto, " + utente.getUsername() + "! (ruolo: " + utente.getRuoloAttivo() + ")");
        } catch (AutenticazioneException e) {
            System.out.println("Credenziali non valide.");
        }
    }
}
