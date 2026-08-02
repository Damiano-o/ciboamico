package it.uniroma2.ispw.ciboamico.exception;

/**
 * Sottoclasse di BusinessValidationException per le transizioni di stato non
 * valide dell'Ordine (BR-04). Incapsula il caso specifico in cui un cambio di
 * stato non è consentito dalla macchina a stati, rendendo il messaggio
 * semanticamente immediato per il layer di presentazione.
 */
public class InvalidStateTransitionException extends BusinessValidationException {

    public InvalidStateTransitionException(String message) {
        super(message);
    }
}