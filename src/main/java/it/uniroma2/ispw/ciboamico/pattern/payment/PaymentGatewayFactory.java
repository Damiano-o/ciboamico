package it.uniroma2.ispw.ciboamico.pattern.payment;

/**
 * Simple Factory del {@link PaymentGateway} (passo 6 UC-04, estensione 6a).
 * Disaccoppia il controller dallo specifico PSP: qui si concentra la scelta
 * dell'implementazione concreta (attualmente lo stub in-memory). Separata dal
 * mondo dei DAO (il pagamento non è persistenza), garantendo Alta Coesione:
 * concordato in audit NotebookLM 2026-08-07.
 * Open/Closed: un nuovo PSP richiede solo un nuovo ramo qui, senza toccare i DAO.
 */
public final class PaymentGatewayFactory {

    private PaymentGatewayFactory() { }

    /**
     * Restituisce il gateway di pagamento attivo. Attualmente lo stub
     * in-memory (nessun PSP reale), deterministico e testabile.
     */
    public static PaymentGateway createGateway() {
        return new StubPaymentGateway();
    }

    /** Stub configurabile per test (es. forzare rifiuto = estensione 6a). */
    public static PaymentGateway createGateway(boolean approvaSempre) {
        return new StubPaymentGateway(approvaSempre);
    }
}