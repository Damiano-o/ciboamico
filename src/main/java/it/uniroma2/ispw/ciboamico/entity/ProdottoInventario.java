package it.uniroma2.ispw.ciboamico.entity;

import java.time.LocalDate;

/**
 * Prodotto presente nell'inventario domestico (frigo/dispensa).
 * Ordinabile per scadenza (FR-02), avviso se scadenza <= 3 giorni (FR-03).
 */
public class ProdottoInventario implements Comparable<ProdottoInventario> {

    private final String nome;
    private int quantita;
    private final LocalDate scadenza;
    private final String posizione;
    private final UnitaEnum unita;
    private final Prodotto riferimento;

    public ProdottoInventario(String nome, int quantita, LocalDate scadenza,
                              String posizione, UnitaEnum unita, Prodotto riferimento) {
        if (quantita <= 0) {
            throw new IllegalArgumentException("La quantità deve essere > 0");
        }
        if (scadenza.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La scadenza non può essere nel passato");
        }
        this.nome = nome;
        this.quantita = quantita;
        this.scadenza = scadenza;
        this.posizione = posizione;
        this.unita = unita;
        this.riferimento = riferimento;
    }

    /** FR-03: prodotto in scadenza se mancano <= 3 giorni. */
    public boolean inScadenza() {
        return scadenza.isBefore(LocalDate.now().plusDays(3));
    }

    @Override
    public int compareTo(ProdottoInventario altro) {
        return this.scadenza.compareTo(altro.scadenza);
    }

    public String getNome() { return nome; }
    public int getQuantita() { return quantita; }
    public LocalDate getScadenza() { return scadenza; }
    public String getPosizione() { return posizione; }
    public UnitaEnum getUnita() { return unita; }
    public Prodotto getRiferimento() { return riferimento; }

    public void setQuantita(int quantita) {
        if (quantita < 0) throw new IllegalArgumentException("Quantità negativa");
        this.quantita = quantita;
    }
}
