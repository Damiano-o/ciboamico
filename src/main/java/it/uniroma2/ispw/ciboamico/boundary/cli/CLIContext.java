package it.uniroma2.ispw.ciboamico.boundary.cli;

import it.uniroma2.ispw.ciboamico.bean.UtenteBean;
import it.uniroma2.ispw.ciboamico.pattern.singleton.SessionManager;
import it.uniroma2.ispw.ciboamico.persistence.factory.DAOFactory;

import java.util.Scanner;

/**
 * Contesto condiviso delle view CLI: input da tastiera (Scanner), la
 * DAOFactory attiva e l'utente loggato (dalla sessione singleton).
 * Le view CLI scambiano SOLO Bean con i controller applicativi (invariati).
 */
public final class CLIContext {

    /** Unico canale di input per tutte le view CLI (evita Scanner multipli su System.in). */
    private static final Scanner SCANNER = new Scanner(System.in);

    private final DAOFactory factory;

    public CLIContext(DAOFactory factory) {
        this.factory = factory;
    }

    public DAOFactory getFactory() {
        return factory;
    }

    /** Utente loggato dalla sessione (Bean-only, mai Entity verso la view). */
    public UtenteBean getLoggedUser() {
        return SessionManager.getInstance().getLoggedUser();
    }

    public String leggiStringa(String prompt) {
        System.out.print(prompt);
        return SCANNER.nextLine().trim();
    }

    public Double leggiDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(SCANNER.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Numero non valido, riprova.");
            }
        }
    }

    public Long leggiLong(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Long.parseLong(SCANNER.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Numero non valido, riprova.");
            }
        }
    }

    public boolean leggiSiNo(String prompt) {
        System.out.print(prompt + " (s/n): ");
        return SCANNER.nextLine().trim().equalsIgnoreCase("s");
    }

    /** Riga successiva dall'input (usata dal loop principale). */
    public String prossimaRiga() {
        return SCANNER.nextLine().trim();
    }

    /** Accesso statico allo scanner condiviso (usato da CLISession). */
    public static String leggiRiga() {
        return SCANNER.nextLine().trim();
    }

    /** Scanner condiviso per l'input utente (usato dal menu di avvio: un solo canale su System.in). */
    public static Scanner scannerCondiviso() {
        return SCANNER;
    }
}
