package it.uniroma2.ispw.ciboamico.pattern.adapter;

import it.uniroma2.ispw.ciboamico.entity.Ordine;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Adapter (GoF) per Jakarta Mail — invio notifiche email.
 * Stub: logga la notifica senza inviare email reali (test indipendenti dalla rete).
 * In produzione: Session SMTP + MimeMessage verso il recapito del destinatario.
 */
public class JakartaMailAdapter {

    private static final Logger LOG = Logger.getLogger(JakartaMailAdapter.class.getName());

    /**
     * Invia la notifica di cambio stato (stub — logga e ritorna true).
     */
    public boolean inviaNotifica(Ordine ordine, String destinatarioEmail, String messaggio) {
        if (destinatarioEmail == null || destinatarioEmail.isBlank()) {
            return false;
        }
        // In produzione: Transport.send(message) verso il server SMTP configurato
        if (LOG.isLoggable(Level.INFO)) {
            LOG.log(Level.INFO, "[MAIL-STUB] A: {0} | Ordine {1} | {2}",
                    new Object[]{destinatarioEmail, ordine.getIdOrdine(), messaggio});
        }
        return true;
    }
}
