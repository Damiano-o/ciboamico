package it.uniroma2.ispw.ciboamico.exception;

/**
 * Eccezione di dominio per le violazioni delle regole di business e delle
 * validazioni semantiche (es. BR-01..BR-07). Runtime perché la maggior parte
 * degli errori deriva da input utente malformato o da flussi non conformi,
 * gestiti nei controller grafici. Sostituisce l'uso generico di
 * IllegalArgumentException per i vincoli di dominio, dando un'astrazione
 * espressiva nel layer di errore (cap. 9 relazione).
 */
public class BusinessValidationException extends RuntimeException {

    public BusinessValidationException(String message) {
        super(message);
    }

    public BusinessValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}