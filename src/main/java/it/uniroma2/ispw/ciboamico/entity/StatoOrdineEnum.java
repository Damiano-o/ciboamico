package it.uniroma2.ispw.ciboamico.entity;

/**
 * Ciclo di vita dell'ordine (BR-04):
 * CREATO → CONFERMATO → IN_CONSEGNA → CONSEGNATO
 * ANNULLATO ammesso solo da CREATO/CONFERMATO.
 */
public enum StatoOrdineEnum {
    CREATO, CONFERMATO, IN_CONSEGNA, CONSEGNATO, ANNULLATO
}
