package it.uniroma2.ispw.ciboamico.pattern.payment;

/**
 * Autorizzazione di pagamento negata (estensione 6a del caso d'uso UC-04).
 * Lanciata dal {@link PaymentGateway} quando l'addebito non è autorizzato
 * (fondi insufficienti, carta rifiutata, ecc.).
 */
public class PaymentRejectedException extends Exception {

    public PaymentRejectedException(String message) {
        super(message);
    }
}