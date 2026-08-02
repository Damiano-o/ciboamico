package it.uniroma2.ispw.ciboamico.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Utente — classe radice del dominio.
 * Pattern Whole-Part: aggrega dinamicamente più Ruolo per superare la metamorfosi
 * (un utente può diventare venditore a runtime senza ristrutturare le classi).
 */
public class Utente {

    private String nome;
    private String email;
    private String passwordHash;
    private final List<Ruolo> ruoli = new ArrayList<>();

    public Utente(String nome, String email, String passwordHash) {
        this.nome = nome;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    /** Aggiunge un ruolo all'utente (metamorfosi dinamica) con back-reference. */
    public void aggiungiRuolo(Ruolo ruolo) {
        if (ruolo instanceof RuoloVenditore rv) {
            rv.setUtente(this);
        }
        ruoli.add(ruolo);
    }

    public boolean haRuolo(Class<? extends Ruolo> tipoRuolo) {
        return ruoli.stream().anyMatch(tipoRuolo::isInstance);
    }

    public <T extends Ruolo> T getRuolo(Class<T> tipoRuolo) {
        return ruoli.stream()
                .filter(tipoRuolo::isInstance)
                .map(tipoRuolo::cast)
                .findFirst()
                .orElse(null);
    }

    public boolean isVenditoreApprovato() {
        RuoloVenditore v = getRuolo(RuoloVenditore.class);
        return v != null && v.getStato() == StatoVenditoreEnum.APPROVATO;
    }

    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public List<Ruolo> getRuoli() { return ruoli; }
}
