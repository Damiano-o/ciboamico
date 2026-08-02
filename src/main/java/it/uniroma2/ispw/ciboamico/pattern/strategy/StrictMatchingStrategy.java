package it.uniroma2.ispw.ciboamico.pattern.strategy;

import it.uniroma2.ispw.ciboamico.entity.ProdottoInventario;
import it.uniroma2.ispw.ciboamico.entity.Ricetta;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Strategy concreta: solo ricette 100% compatibili (tutti gli ingredienti disponibili).
 */
public class StrictMatchingStrategy implements MatchingStrategy {

    @Override
    public List<Ricetta> match(List<ProdottoInventario> inventario, List<Ricetta> ricette) {
        return ricette.stream()
                .filter(r -> ricettaCompatibile(r, inventario))
                .collect(Collectors.toList());
    }

    private boolean ricettaCompatibile(Ricetta ricetta, List<ProdottoInventario> inventario) {
        return ricetta.getIngredienti().stream()
                .allMatch(ing -> inventario.stream()
                        .anyMatch(pi -> pi.getNome().equalsIgnoreCase(ing.getProdotto().getNome())
                                && pi.getQuantita() >= ing.getQuantita()));
    }
}
