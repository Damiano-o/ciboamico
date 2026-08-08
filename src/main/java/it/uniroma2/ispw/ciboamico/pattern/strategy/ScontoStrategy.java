package it.uniroma2.ispw.ciboamico.pattern.strategy;

/**
 * Strategy (GoF - Behavioral) per il calcolo dello sconto di un buono promozionale.
 * Incapsula l'algoritmo di sconto dietro un'interfaccia stabile, consentendo di
 * aggiungere nuovi tipi di sconto (percentuale, importo fisso, spedizione gratuita...)
 * senza modificare l'entità Ordine né il Controller (principio Open/Closed).
 */
public interface ScontoStrategy {

    /**
     * Restituisce l'importo totale da pagare dopo l'applicazione dello sconto
     * sull'importo di partenza (subtotoale già calcolato).
     *
     * @param subtotale importo lordo dell'ordine prima dello sconto
     * @return importo finale, mai negativo
     */
    double applicaSconto(double subtotale);

    /** Etichetta descrittiva dello sconto (es. "-20%", "-5.00 €") usata dai bean. */
    String descrizione();

    /** Tipo costante dello sconto (es. "PERCENTUALE" / "FISSO") per la persistenza. */
    String getTipo();

    /** Valore numerico dello sconto (percentuale in [0,1] oppure importo fisso in €). */
    double getValore();
}
