package it.uniroma2.ispw.ciboamico.exception;

// Autorizzazione di pagamento negata (estensione 6a del caso d'uso UC-04).

public class PaymentRejectedException extends Exception {

    public PaymentRejectedException(String message) {
        super(message);
    }
}