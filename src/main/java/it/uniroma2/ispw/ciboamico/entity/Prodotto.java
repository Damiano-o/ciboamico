package it.uniroma2.ispw.ciboamico.entity;

import java.time.LocalDate;

/**
 * Prodotto pubblicato nel marketplace da un venditore.
 * Regole: prezzo > 0 (BR-06), quantità >= 0 (BR-03), non scaduto (BR-01).
 */
public class Prodotto {

    private final String nome;
    private double prezzo;
    private int quantitaDisponibile;
    private final LocalDate scadenza;
    private final UnitaEnum unita;
    private final RuoloVenditore venditore;

    public Prodotto(String nome, double prezzo, int quantitaDisponibile,
                    LocalDate scadenza, UnitaEnum unita, RuoloVenditore venditore) {
        if (prezzo <= 0) {
            throw new IllegalArgumentException("Il prezzo deve essere maggiore di 0 (BR-06)");
        }
        if (quantitaDisponibile < 0) {
            throw new IllegalArgumentException("La quantità non può essere negativa (BR-03)");
        }
        if (scadenza.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Un prodotto scaduto non può essere venduto (BR-01)");
        }
        this.nome = nome;
        this.prezzo = prezzo;
        this.quantitaDisponibile = quantitaDisponibile;
        this.scadenza = scadenza;
        this.unita = unita;
        this.venditore = venditore;
    }

    public boolean riduciDisponibilita(int quantita) {
        if (quantita > quantitaDisponibile) {
            return false;
        }
        quantitaDisponibile -= quantita;
        return true;
    }

    public String getNome() { return nome; }
    public double getPrezzo() { return prezzo; }
    public int getQuantitaDisponibile() { return quantitaDisponibile; }
    public LocalDate getScadenza() { return scadenza; }
    public UnitaEnum getUnita() { return unita; }
    public RuoloVenditore getVenditore() { return venditore; }

    public void setPrezzo(double prezzo) {
        if (prezzo <= 0) throw new IllegalArgumentException("Prezzo non valido (BR-06)");
        this.prezzo = prezzo;
    }
}
