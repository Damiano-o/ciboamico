package it.uniroma2.ispw.ciboamico.pattern.strategy;

import it.uniroma2.ispw.ciboamico.entity.ProdottoInventario;
import it.uniroma2.ispw.ciboamico.entity.Ricetta;

import java.util.List;

/**
 * Strategy (GoF): algoritmo di matching ricette intercambiabile a runtime (D-01).
 */
public interface MatchingStrategy {

    /**
     * Restituisce le ricette compatibili con l'inventario, ordinate
     * per numero di ingredienti mancanti (0 = compatibili).
     */
    List<Ricetta> match(List<ProdottoInventario> inventario, List<Ricetta> ricette);
}
