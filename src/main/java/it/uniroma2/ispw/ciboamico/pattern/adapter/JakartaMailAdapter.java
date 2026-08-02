package it.uniroma2.ispw.ciboamico.pattern.adapter;

import it.uniroma2.ispw.ciboamico.entity.Ordine;

/**
 * Adapter (GoF) per Jakarta Mail — invio notifiche email.
 * Stub: logga la notifica senza inviare email reali (test indipendenti dalla rete).
 * In produzione: Session SMTP + MimeMessage verso il recapito del destinatario.
 */
public class JakartaMailAdapter {

    /**
     * Invia la notifica di cambio stato (stub — logga e ritorna true).
     */
    public boolean inviaNotifica(Ordine ordine, String destinatarioEmail, String messaggio) {
        if (destinatarioEmail == null || destinatarioEmail.isBlank()) {
            return false;
        }
        // In produzione: Transport.send(message) verso il server SMTP configurato
        System.out.println("[MAIL-STUB] A: " + destinatarioEmail
                + " | Ordine " + ordine.getIdOrdine()
                + " | " + messaggio);
        return true;
    }
}
