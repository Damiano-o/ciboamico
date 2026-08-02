package it.uniroma2.ispw.ciboamico.entity;

/**
 * Ingrediente di una ricetta: associa un Prodotto a quantità e unità.
 * I sostituti sono gestiti SOLO dentro SubstitutionMatchingStrategy (Low Coupling).
 */
public class Ingrediente {

    private final Prodotto prodotto;
    private final double quantita;
    private final UnitaEnum unita;

    public Ingrediente(Prodotto prodotto, double quantita, UnitaEnum unita) {
        this.prodotto = prodotto;
        this.quantita = quantita;
        this.unita = unita;
    }

    public Prodotto getProdotto() { return prodotto; }
    public double getQuantita() { return quantita; }
    public UnitaEnum getUnita() { return unita; }
}
