package it.uniroma2.ispw.ciboamico.entity;

/**
 * Ruolo Amministratore: approva venditori/ricette, gestisce catalogo.
 */
public class RuoloAmministratore extends Ruolo {
    @Override
    public String getNomeRuolo() { return "AMMINISTRATORE"; }
}
