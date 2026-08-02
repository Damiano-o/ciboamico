package it.uniroma2.ispw.ciboamico.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Ricetta creata dal Nutrizionista (stato PROPOSTA → APPROVATA/RIFIUTATA).
 * Contiene almeno 2 ingredienti (BR-05).
 */
public class Ricetta {

    private final String nome;
    private final String istruzioni;
    private StatoRicettaEnum stato;
    private final List<Ingrediente> ingredienti = new ArrayList<>();
    private final RuoloNutrizionista autore;

    public Ricetta(String nome, String istruzioni, RuoloNutrizionista autore) {
        this.nome = nome;
        this.istruzioni = istruzioni;
        this.autore = autore;
        this.stato = StatoRicettaEnum.PROPOSTA;
    }

    /** BR-05: almeno 2 ingredienti per la validazione finale. */
    public boolean haAlmenoDueIngredienti() {
        return ingredienti.size() >= 2;
    }

    public void aggiungiIngrediente(Ingrediente ingrediente) {
        ingredienti.add(ingrediente);
    }

    public String getNome() { return nome; }
    public String getIstruzioni() { return istruzioni; }
    public StatoRicettaEnum getStato() { return stato; }
    public List<Ingrediente> getIngredienti() { return ingredienti; }
    public RuoloNutrizionista getAutore() { return autore; }

    public void setStato(StatoRicettaEnum stato) { this.stato = stato; }
}
