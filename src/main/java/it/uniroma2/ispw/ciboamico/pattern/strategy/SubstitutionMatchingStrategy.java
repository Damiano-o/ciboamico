package it.uniroma2.ispw.ciboamico.pattern.strategy;

import it.uniroma2.ispw.ciboamico.entity.ProdottoInventario;
import it.uniroma2.ispw.ciboamico.entity.Ricetta;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Strategy concreta (elemento innovativo D-01): trova ricette anche se manca
 * un ingrediente, proponendo sostituti equivalenti con fattore di conversione.
 * I sostituti sono gestiti QUI dentro (Low Coupling — non nel dominio).
 */
public class SubstitutionMatchingStrategy implements MatchingStrategy {

    /** Sostituto interno alla strategia: nome + fattore di conversione. */
    private record Sostituto(String nome, double fattore) { }

    @Override
    public List<Ricetta> match(List<ProdottoInventario> inventario, List<Ricetta> ricette) {
        return ricette.stream()
                .filter(r -> ricettaCompatibileConSostituzione(r, inventario))
                .collect(Collectors.toList());
    }

    private boolean ricettaCompatibileConSostituzione(Ricetta ricetta, List<ProdottoInventario> inventario) {
        return ricetta.getIngredienti().stream()
                .allMatch(ing -> disponibileODerivabile(ing.getProdotto().getNome(), inventario));
    }

    private boolean disponibileODerivabile(String nomeIngrediente, List<ProdottoInventario> inventario) {
        // 1. Disponibile direttamente
        boolean diretto = inventario.stream()
                .anyMatch(pi -> pi.getNome().equalsIgnoreCase(nomeIngrediente));
        if (diretto) return true;
        // 2. Sostituibile (es. pomodori → passata ×0.5)
        return inventario.stream()
                .anyMatch(pi -> tabellaSostituti(pi.getNome()).stream()
                        .anyMatch(s -> s.nome.equalsIgnoreCase(nomeIngrediente)));
    }

    /** Tabella interna di sostituzioni (fattore = quantità sostituto per unità originale). */
    private List<Sostituto> tabellaSostituti(String prodottoDisponibile) {
        return switch (prodottoDisponibile.toLowerCase()) {
            case "passata di pomodoro" -> List.of(new Sostituto("pomodori", 0.5));
            case "margarina" -> List.of(new Sostituto("burro", 1.2));
            case "panna" -> List.of(new Sostituto("latte", 1.0));
            default -> List.of();
        };
    }
}
