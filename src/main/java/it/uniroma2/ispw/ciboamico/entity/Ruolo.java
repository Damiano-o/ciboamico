package it.uniroma2.ispw.ciboamico.entity;

/**
 * Ruolo astratto — parte del pattern Whole-Part con Utente.
 * Le sottoclassi concrete definiscono i permessi specifici.
 * Metodo astratto per esprimere il contratto: ogni ruolo espone il proprio nome.
 */
public abstract class Ruolo {
    public abstract String getNomeRuolo();
}
